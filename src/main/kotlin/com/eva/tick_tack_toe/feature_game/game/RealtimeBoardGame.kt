package com.eva.tick_tack_toe.feature_game.game

import com.eva.tick_tack_toe.feature_game.dto.BoardGameReceiveDataDto
import com.eva.tick_tack_toe.feature_game.dto.BoardGameSendDataDto
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json

class RealtimeBoardGame(
    private val playerServer: RoomAndPlayerServer
) {
    private lateinit var playerRoom: GameRoomModel

    /**
     * Runs when the players connect to play the game with specific room
     * @param session : [WebSocketSession]
     * @param userName : Username for the player
     * @param clientId : Unique Client Id for the user
     * @param room : Room id for the game
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

        playerRoom.players.forEach { gamePlayer ->
            if (gamePlayer != player)
                gamePlayer.session.sendSerialized(
                    ServerSendEventsDto
                        .ServerMessage(message = "${player.userName} joined the stream")
                )
        }

        return player
    }

    /**
     * Runs when the players connect to play an anonymous game
     * @param session : [WebSocketSession]
     * @param userName : Username for the player
     * @param clientId : Unique Client Id for the user
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

        playerRoom.players.forEach { gamePlayer ->
            if (gamePlayer != player)
                gamePlayer.session.sendSerialized(
                    ServerSendEventsDto
                        .ServerMessage(message = "${player.userName} joined the stream")
                )
        }

        return player
    }

    /**
     * Receives the events from the stream and updates the board accordingly
     */
    suspend fun onReceiveEvents(session: WebSocketServerSession) {
        session.incoming.consumeEach { frame ->
            (frame as? Frame.Text)
                ?.let { frameText ->
                    val readText = frameText.readText()
                    val receiveDataDto = Json.decodeFromString<BoardGameReceiveDataDto>(readText)
                    playerRoom.board.updateBoardState(
                        position = receiveDataDto.boardPosition.toModel(),
                        playerSymbols = BoardSymbols.fromSymbol(receiveDataDto.symbol)
                    )
                }
        }
    }

    /**
     * Broadcast the events to the stream
     */
    fun broadCastGameState(scope: CoroutineScope) {
        playerRoom.toDtoAsFlow()
            .onEach { board ->
                val boardGame = BoardGameSendDataDto(
                    playerX = playerRoom.players.find { it.symbol == BoardSymbols.XSymbol }?.toDto(),
                    playerO = playerRoom.players.find { it.symbol == BoardSymbols.OSymbol }?.toDto(),
                    board = board,
                    isAllPlayerJoined = playerRoom.players.size >= 2
                )
                playerRoom.players.forEach { player ->
                    player.session
                        .sendSerialized(ServerSendEventsDto.ServerGameState(state = boardGame))
                }
            }.launchIn(scope)
    }


    /**
     * Runs when the player want to disconnect from the session or the session is complete
     * @param player : [GamePlayerModel] the player itself
     */
    suspend fun onDisconnect(player: GamePlayerModel) {

        playerServer.removePlayerFromRoom(player)

        playerRoom.players.forEach { gamePlayer ->
            if (gamePlayer != player)
                gamePlayer.session.sendSerialized(
                    ServerSendEventsDto
                        .ServerMessage(message = "${player.userName} left the game stream")
                )
        }
    }
}