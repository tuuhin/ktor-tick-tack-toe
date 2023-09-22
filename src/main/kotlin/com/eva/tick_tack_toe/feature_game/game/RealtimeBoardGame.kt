package com.eva.tick_tack_toe.feature_game.game

import com.eva.tick_tack_toe.Logger
import com.eva.tick_tack_toe.feature_game.dto.GameAchievementDto
import com.eva.tick_tack_toe.feature_game.dto.GameReceiveDataDto
import com.eva.tick_tack_toe.feature_game.dto.GameSendDataDto
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
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

class RealtimeBoardGame(
    private val playerServer: RoomAndPlayerServer
) {

    /**
     * [GameRoomModel] instance of the room found when the user connects to the game room
     */
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
            player = player,
            message = "${player.userName}: Joined the Game"
        )

        return player
    }


    /**
     * Runs when the players connect to play an anonymous game
     * @param session  [WebSocketServerSession] The websocket session for the server
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
            player = player,
            message = "${player.userName}: Joined the Game"
        )

        return player
    }


    /**
     * Receives the events from the stream and updates the board accordingly
     * @param session [WebSocketServerSession] to which incoming request are listened to.
     */
    suspend fun onReceiveEvents(session: WebSocketServerSession) = session.incoming.consumeEach { frame ->
        (frame as? Frame.Text)?.let { frameText ->

            val readText = frameText.readText()
            val data = Json.decodeFromString<GameReceiveDataDto>(readText)
            if (playerRoom.game.canUpdateBoard && playerRoom.isReady) {
                playerRoom.players.find { it.clientId == data.clientId }?.let { player ->

                    playerRoom.game.updateBoardState(
                        position = data.boardPosition.toModel(),
                        playerSymbols = player.symbol
                    )
                } ?: Logger.error("Cannot find a proper socket info about the player")
            }
        }
    }

    /**
     * Broadcast the events to the stream
     */
    suspend fun broadCastGameState() = playerRoom.toDtoAsFlow()
        .collect { board ->
            // If the board is not av
            if (!playerRoom.game.canUpdateBoard) {
                playerRoom.updatePlayerPoints()
                if (playerRoom.isNextRoundAvailable) {
                    playerRoom.incrementBoardCount()
                    sendServerMessage(
                        players = playerRoom.players,
                        message = "Moving to next round after delay of 5 seconds"
                    )

                    delay(5.seconds)

                    playerRoom.clearAndCreateNewRoom()
                } else {
                    val winner = playerRoom.gameWinner()
                    sendAchievement(
                        players = playerRoom.players,
                        message = "Game is over the winner is ",
                        winnerSymbols = winner.symbol,
                        winnerName = winner.userName
                    )
                }
            }else {
                // If it can be updated, then only events are sent
                val boardGame = GameSendDataDto(
                    playerX = playerRoom.players.find { it.symbol == BoardSymbols.XSymbol }?.toDto(),
                    playerO = playerRoom.players.find { it.symbol == BoardSymbols.OSymbol }?.toDto(),
                    board = board
                )
                playerRoom.players.forEach { player ->
                    player.session
                        .sendSerialized(ServerSendEventsDto.ServerGameState(state = boardGame))
                }
            }
        }


    /**
     * Runs when the player wants to disconnect from the session or the session is complete
     * @param player : [GamePlayerModel] the player itself which is to be disconnected
     */
    suspend fun onDisconnect(player: GamePlayerModel) {

        playerServer.removePlayerFromRoom(player = player)
        if (playerRoom.players.isNotEmpty()) {
            sendAssociatedMessage(
                players = playerRoom.players,
                player = player,
                message = "The other player left the room  you are the winner"
            )
        }
    }


    /**
     * A helper function to send an associated message to the end user
     * @param players  List of [GamePlayerModel] to which the message to be sent
     * @param player [GamePlayerModel] which sends the message to the other users
     * @param message  An associated [String] message to be sent by the player.
     */
    private suspend fun sendAssociatedMessage(
        players: List<GamePlayerModel>,
        player: GamePlayerModel,
        message: String
    ) = players.forEach {
        if (it != player)
            it.session.sendSerialized(
                ServerSendEventsDto.ServerMessage(message = message)
            )
    }

    private suspend fun sendAchievement(
        players: List<GamePlayerModel>,
        message: String,
        winnerSymbols: BoardSymbols,
        associatedText: String? = null,
        winnerName: String? = null
    ) {
        players.forEach { player ->
            player.session.sendSerialized(
                ServerSendEventsDto.GameAchievementState(
                    result = GameAchievementDto(
                        text = message,
                        secondaryText = associatedText,
                        winnerSymbols = winnerSymbols,
                        winnerName = winnerName
                    )
                )
            )
        }
    }


    /**
     * A utility to send server messages to the users/players
     * @param players List of [GamePlayerModel] to which the message to be sent
     * @param message The message that is to be sent
     */
    private suspend fun sendServerMessage(
        players: List<GamePlayerModel>,
        message: String
    ) = players.forEach { player ->
        player.session.sendSerialized(
            ServerSendEventsDto.ServerMessage(message = message)
        )
    }

}