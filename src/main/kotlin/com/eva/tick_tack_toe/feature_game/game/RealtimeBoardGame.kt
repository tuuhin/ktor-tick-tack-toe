package com.eva.tick_tack_toe.feature_game.game

import com.eva.tick_tack_toe.feature_game.dto.GameSendDataDto
import com.eva.tick_tack_toe.feature_game.dto.ServerReceiveEvents
import com.eva.tick_tack_toe.feature_game.dto.ServerSendEventsDto
import com.eva.tick_tack_toe.feature_game.mapper.toDtoAsFlow
import com.eva.tick_tack_toe.feature_game.mapper.toModel
import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import com.eva.tick_tack_toe.feature_room.mappers.toDto
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel
import com.eva.tick_tack_toe.utils.BoardSymbols
import com.eva.tick_tack_toe.utils.RoomAndPlayerServer
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

class RealtimeBoardGame(
    private val playerServer: RoomAndPlayerServer
) {
    private lateinit var playerRoom: GameRoomModel

    /**
     * Runs when the players connect to play the game with specific room
     * @param session  [WebSocketSession]
     * @param userName  Username for the player
     * @param clientId  Unique Client Id for the user
     * @param room  Room id for the game
     * @return The newly created [GamePlayerModel]
     */
    suspend fun onConnect(
        session: WebSocketServerSession,
        userName: String,
        clientId: String,
        room: String
    ): GamePlayerModel {
        val player = GamePlayerModel(userName = userName, clientId = clientId, session = session)

        playerServer.addPlayersToRoom(room, player)
        playerRoom = playerServer.getRoomFromClientId(clientId)

        sendAssociatedMessage(
            players = playerRoom.players,
            currentPlayer = player,
            message = "${player.userName}: Joined the Game"
        )

        return player
    }

    /**
     * Runs when the players connect to play an anonymous game
     * @param session  [WebSocketSession]
     * @param userName  Username for the player
     * @param clientId  Unique Client Id for the user
     * @return The newly created [GamePlayerModel]
     */
    suspend fun onConnect(
        session: WebSocketServerSession,
        userName: String,
        clientId: String,
    ): GamePlayerModel {
        val player = GamePlayerModel(userName = userName, clientId = clientId, session = session)

        playerServer.addAnonymousPlayerToRoom(player)

        playerRoom = playerServer.getRoomFromClientId(clientId)

        sendAssociatedMessage(
            players = playerRoom.players,
            currentPlayer = player,
            message = "${player.userName}: Joined the Game"
        )

        return player
    }

    /**
     * Receives the events from the stream and updates the board accordingly
     */
    suspend fun onReceiveEvents(session: WebSocketServerSession) = session.incoming.consumeEach { frame ->
        (frame as? Frame.Text)?.let { frameText ->

            val readText = frameText.readText()
            when (val receiveData = Json.decodeFromString<ServerReceiveEvents>(readText)) {

                is ServerReceiveEvents.ReceiveGameData -> {
                    val data = receiveData.data
                    playerRoom.players.find { it.clientId == data.clientId }?.let { player ->
                        playerRoom.board.updateBoardState(
                            position = data.boardPosition.toModel(),
                            playerSymbols = player.symbol
                        )
                    }
                }

            }
        }
    }


    /**
     * Broadcast the events to the stream
     */
    suspend fun broadCastGameState() = playerRoom.toDtoAsFlow()
        .collect { board ->
            val boardGame = GameSendDataDto(
                playerX = playerRoom.players.find { it.symbol == BoardSymbols.XSymbol }?.toDto(),
                playerO = playerRoom.players.find { it.symbol == BoardSymbols.OSymbol }?.toDto(),
                board = board,
                isAllPlayerJoined = playerRoom.players.size >= 2
            )
            playerRoom.players.forEach { player ->
                player.session
                    .sendSerialized(ServerSendEventsDto.ServerGameState(state = boardGame))
            }
        }

    /**
     * Runs when the player wants to disconnect from the session or the session is complete
     * @param player : [GamePlayerModel] the player itself which is to be disconnected
     */
    suspend fun onDisconnect(player: GamePlayerModel) {

        playerServer.removePlayerFromRoom(player = player)

        sendAssociatedMessage(
            players = playerRoom.players,
            currentPlayer = player,
            message = "${player.userName} :Player left the room"
        )
    }

    /**
     * A helper function to send an associated message to the end user
     * @param players  List of [GamePlayerModel] to which the message to be sent
     * @param currentPlayer [GamePlayerModel] which sends the message to the other users
     * @param message  An associated [String] message to be sent by the player.
     */
    private suspend fun sendAssociatedMessage(
        players: List<GamePlayerModel>,
        currentPlayer: GamePlayerModel,
        message: String
    ) {
        players.forEach { gamePlayer ->
            if (gamePlayer != currentPlayer)
                gamePlayer.session.sendSerialized(
                    ServerSendEventsDto
                        .ServerMessage(message = message)
                )
        }
    }
}