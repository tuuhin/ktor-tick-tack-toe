package com.eva.tick_tack_toe.feature_game.route

import com.eva.tick_tack_toe.TestApiPaths
import com.eva.tick_tack_toe.feature_game.dto.*
import com.eva.tick_tack_toe.feature_game.mapper.toDto
import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import com.eva.tick_tack_toe.feature_game.utils.TestBoardCombinations
import com.eva.tick_tack_toe.feature_room.dto.GamePlayerDto
import com.eva.tick_tack_toe.setWebsocketConfig
import com.eva.tick_tack_toe.utils.BoardSymbols
import com.eva.tick_tack_toe.utils.ext.toBoardLayout
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GameWebsocketTest {

    @Test
    fun `check the working of the websockets`() = testApplication {

        val clientId = generateNonce()
        var playerX: GamePlayerDto? = null
        var playerO: GamePlayerDto? = null
        var room: GameRoomDto? = null

        val socketClient = createClient {
            setWebsocketConfig()
        }

        socketClient.webSocket(
            method = HttpMethod.Get,
            host = "localhost",
            path = "${TestApiPaths.WEBSOCKET_GAME_API_PATHS}=$clientId",
            port = 80
        ) {
            //setting up the websocket connection

            when (val received = receiveDeserialized<ServerSendEventsDto>()) {
                is ServerSendEventsDto.ServerGameState -> {

                    assertEquals(
                        received.type,
                        ServerSendEventTypes.GAME_STATE_TYPE.type,
                        message = "check the receive type to be game type"
                    )

                    received.state.playerX?.let { player ->
                        playerX = GamePlayerDto(
                            userName = "Anonymous",
                            clientId = clientId,
                            winCount = 0,
                            lostCount = 0,
                            drawCount = 0,
                            playerSymbol = player.playerSymbol
                        )

                        room = GameRoomModel(
                            room = received.state.board.room,
                            isAnonymous = true
                        ).toDto()


                        assertEquals(
                            received.state.playerX,
                            playerX,
                            message = "check player_x is really player_x or not"
                        )
                    }
                    received.state.playerO?.let { player ->
                        playerO = GamePlayerDto(
                            userName = "Anonymous",
                            clientId = clientId,
                            winCount = 0,
                            lostCount = 0,
                            drawCount = 0,
                            playerSymbol = player.playerSymbol
                        )

                        room = GameRoomModel(
                            room = received.state.board.room,
                            isAnonymous = true
                        ).toDto()

                        assertEquals(
                            received.state.playerO,
                            playerO,
                            message = "check player o is player o or not"
                        )
                    }
                    assertEquals(
                        received.state.board,
                        room,
                        message = "Game room and board are same or not"
                    )
                }

                else -> {}
            }

            sendSerialized(
                ServerReceiveEvents.ReceiveGameData(
                    data = GameReceiveDataDto(
                        clientId = clientId,
                        boardPosition = BoardPositionDto(0, 0)
                    )
                )
            )

            when (val received = receiveDeserialized<ServerSendEventsDto>()) {
                is ServerSendEventsDto.ServerGameState -> {
                    received.state.playerO?.let { player ->
                        assertEquals(player, playerO, message = "Checking the state of player O")
                    }
                    received.state.playerX?.let { player ->
                        assertEquals(player, playerX, message = "Checking the state of player X")

                    }

                    val layout = received.state.board.boardLayout.toBoardLayout()

                    assertEquals(
                        received.type,
                        ServerSendEventTypes.GAME_STATE_TYPE.type,
                        message = "check the receive type to be game type"
                    )
                    received.state.playerO?.let { player ->

                        val playerSymbol = BoardSymbols.fromSymbol(player.playerSymbol)

                        assertEquals(
                            layout,
                            TestBoardCombinations.FilledAtTopCorner(playerSymbol).combinations,
                            message = "Checking if the board shape is filled at O at corner only"
                        )
                    }

                    received.state.playerX?.let { player ->

                        val playerSymbol = BoardSymbols.fromSymbol(player.playerSymbol)

                        assertEquals(
                            layout,
                            TestBoardCombinations.FilledAtTopCorner(playerSymbol).combinations,
                            message = "Checking if the board shape is filled at X at corner only"
                        )
                    }

                }

                else -> {}
            }

            close(reason = CloseReason(code = CloseReason.Codes.NORMAL, message = "Disconnecting the session"))
        }
    }
}

