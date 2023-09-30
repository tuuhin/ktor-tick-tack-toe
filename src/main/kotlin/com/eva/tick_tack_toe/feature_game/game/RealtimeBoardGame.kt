package com.eva.tick_tack_toe.feature_game.game

import com.eva.tick_tack_toe.feature_game.dto.GameReceiveDataDto
import com.eva.tick_tack_toe.feature_game.dto.GameSendDataDto
import com.eva.tick_tack_toe.feature_game.mapper.toDtoAsFlow
import com.eva.tick_tack_toe.feature_game.mapper.toModel
import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import com.eva.tick_tack_toe.feature_game.utils.RoomUnInitializedException
import com.eva.tick_tack_toe.feature_game.utils.ServerSendUtilities
import com.eva.tick_tack_toe.feature_room.mappers.toDto
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel
import com.eva.tick_tack_toe.utils.BoardSymbols
import com.eva.tick_tack_toe.utils.RoomAndPlayerServer
import io.ktor.server.websocket.*
import io.ktor.util.logging.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json


/**
 * Manages the [WebSocketSession] for the server to play the game.
 *
 * Make sure [playerRoom] is initialized via [onConnect] function before using other methods otherwise it will throw
 * a [RoomUnInitializedException]
 *
 * @param playerServer [RoomAndPlayerServer]
 * Manages the room when a user joins either via room or anonymously
 *
 * @param serverUtils [ServerSendUtilities]
 * A set of utility functions that helps to send server and other information to the client
 */
class RealtimeBoardGame(
    private val playerServer: RoomAndPlayerServer,
    private val serverUtils: ServerSendUtilities,
) {

    /**
     * [Logger] for the [RealtimeBoardGame]
     */
    private val logger = KtorSimpleLogger("REALTIME_BOARD_GAME_LOGGER")

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
        room: String,
    ): GamePlayerModel {

        val player = GamePlayerModel(
            userName = userName,
            clientId = clientId,
            session = session,
        )

        playerServer.addPlayersToRoom(room, player)

        logger.info("ADDED PLAYER ${player.clientId} to the ROOM $room")

        playerRoom = playerServer.getRoomFromClientId(clientId)

        serverUtils.sendAssociatedMessage(
            players = playerRoom.players,
            self = player,
            message = "${player.userName}: Joined the Game",
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

        val player = GamePlayerModel(
            userName = userName,
            clientId = clientId,
            session = session,
        )

        val newRoom = playerServer.addAnonymousPlayerToRoom(player)

        logger.info("ADDED PLAYER ${player.clientId} to the ROOM ${newRoom?.room}")

        playerRoom = playerServer.getRoomFromClientId(clientId)

        serverUtils.sendAssociatedMessage(
            players = playerRoom.players,
            self = player,
            message = "${player.userName}: Joined the Game",
        )

        return player
    }

    /**
     * Receives the events from the stream and updates the board accordingly
     * @param session [WebSocketServerSession] to which incoming request are listened to.
     */
    suspend fun onReceiveEvents(session: WebSocketServerSession) {
        if (!::playerRoom.isInitialized) {
            throw RoomUnInitializedException()
        }
        return session.incoming.consumeAsFlow()
            .collect { frame ->
                (frame as? Frame.Text)?.let { frameText ->

                    val readText = frameText.readText()
                    val data = Json.decodeFromString<GameReceiveDataDto>(readText)

                    val isBoardReadyAndNoResults = playerRoom.game.canUpdateBoard && playerRoom.isReady

                    if (isBoardReadyAndNoResults) {

                        playerRoom.players.find { it.clientId == data.clientId }?.let { player ->
                            playerRoom.game.updateBoardState(
                                position = data.boardPosition.toModel(),
                                playerSymbols = player.symbol
                            )
                        } ?: logger.error("CANNOT FIND A MATCHING CLIENT ID IN THE ROOM TO RECEIVE DATA")
                    }
                }
            }
    }

    /**
     * Broadcast the events to the stream
     */
    suspend fun broadCastGameState(session: WebSocketServerSession) {
        if (!::playerRoom.isInitialized) {
            throw RoomUnInitializedException()
        }
        return playerRoom.toDtoAsFlow()
            .onEach { checkForUpdatesAndEnd() }
            .collect { board ->
                playerRoom.players.find { it.session == session }?.let { player ->

                    val boardGame = GameSendDataDto(
                        playerX = playerRoom.players.find { it.symbol == BoardSymbols.XSymbol }?.toDto(),
                        playerO = playerRoom.players.find { it.symbol == BoardSymbols.OSymbol }?.toDto(),
                        board = board
                    )

                    serverUtils.sendBoardGameState(
                        players = playerRoom.players,
                        self = player,
                        board = boardGame,
                        isBroadcast = !playerRoom.hasGameStarted
                    )
                } ?: logger.error("CANNOT FIND A MATCHING PLAYER TO BROADCAST DATA")
            }
    }

    /**
     * Checks if the room can be updated and accordingly updates the points and board count.
     */
    private suspend fun checkForUpdatesAndEnd() {
        if (playerRoom.game.canUpdateBoard) return
        if (playerRoom.isNextRoundAvailable) {

            serverUtils.sendServerMessage(
                players = playerRoom.players,
                message = "Moving to next round after delay of ${playerRoom.delay.inWholeSeconds} seconds"
            )
            return
        }
        playerRoom.checkAndGetGameWinner?.let { player ->
            serverUtils.sendWinnerAchievement(
                players = playerRoom.players,
                message = "Game is over the winner is ${player.symbol.symbol}",
                winnerSymbols = player.symbol,
                winnerName = player.userName
            )
        } ?: serverUtils.sendDrawAchievement(
            players = playerRoom.players,
            message = "Draw",
            associatedText = "The game ended in a draw both the players have same win-counts"
        )

    }


    /**
     * Runs when the player wants to disconnect from the session or the session is complete
     * @param player : [GamePlayerModel] the player itself which is to be disconnected
     */
    suspend fun onDisconnect(player: GamePlayerModel) {
        if (!::playerRoom.isInitialized) {
            throw RoomUnInitializedException()
        }
        logger.info("THE PLAYER: ${player.clientId} DISCONNECTED WITH THE GAME")

        playerServer.removePlayerFromRoom(player = player)

        // if a player disconnects the session, then send the other user a message that the user left
        if (playerRoom.players.isNotEmpty()) {

            serverUtils.sendAssociatedMessage(
                players = playerRoom.players,
                self = player,
                message = "The other player left the room  you are the winner"
            )
        }
    }
}