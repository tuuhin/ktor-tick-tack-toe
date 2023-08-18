package com.eva.tick_tack_toe.feature_game.game

import com.eva.tick_tack_toe.feature_game.dto.BoardGameReceiveDataDto
import com.eva.tick_tack_toe.feature_game.dto.BoardGameSendDataDto
import com.eva.tick_tack_toe.feature_game.dto.ServerSendEventsDto
import com.eva.tick_tack_toe.feature_game.mapper.toDtoAsFlow
import com.eva.tick_tack_toe.feature_game.mapper.toModel
import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import com.eva.tick_tack_toe.feature_room.mappers.toDto
import io.ktor.websocket.*
import com.eva.tick_tack_toe.utils.RoomAndPlayerServer
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel
import com.eva.tick_tack_toe.utils.BoardSymbols
import io.ktor.server.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json

class RealtimeBoardGame(
    private val playerServer: RoomAndPlayerServer
) {
    private lateinit var playerRoom: GameRoomModel

    /**
     * Runs when the players connect to play the game
     * @param session : [WebSocketSession]
     * @param userName : Username for the player
     * @param clientId : Unique Client Id for the user
     * @return The newly created [GamePlayerModel]
     */
    suspend fun onConnect(
        session: WebSocketServerSession,
        userName: String,
        clientId: String,
        room: String? = null
    ): GamePlayerModel {
        val player = GamePlayerModel(userName = userName, clientId = clientId, session = session)


        room?.let { roomId ->
            playerServer.addPlayersToRoom(roomId, player)
        } ?: playerServer.addAnonymousPlayerToRoom(player)

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
     * Receives the events into the stream and updates the board accordingly
     */
    suspend fun onReceiveEvents(session: WebSocketServerSession) {
        session.incoming.consumeEach { frame ->
            (frame as? Frame.Text)?.let { frameText ->
                val readHead = frameText.readText()
                val receiveData = Json.decodeFromString<BoardGameReceiveDataDto>(readHead)
                playerRoom.board.updateBoardState(
                    position = receiveData.pos.toModel(),
                    playerSymbols = BoardSymbols.fromSymbol(receiveData.symbol)
                )
            }
        }
    }

    /**
     * BoardCast the events to the stream
     */
    fun broadCastGameState(scope: CoroutineScope) {
        playerRoom.toDtoAsFlow()
            .onEach { board ->
                playerRoom.players.forEach { player ->
                    val boardGame = BoardGameSendDataDto(
                        player = player.toDto(),
                        board = board
                    )
                    player.session.sendSerialized(
                        ServerSendEventsDto.ServerGameState(state = boardGame)
                    )
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