package com.eva.tick_tack_toe.feature_game.route

import com.eva.tick_tack_toe.TestApiPaths
import com.eva.tick_tack_toe.feature_game.dto.BoardPositionDto
import com.eva.tick_tack_toe.feature_game.dto.GameReceiveDataDto
import com.eva.tick_tack_toe.feature_game.dto.ServerReceiveEvents
import com.eva.tick_tack_toe.feature_game.dto.ServerSendEventsDto
import com.eva.tick_tack_toe.setWebsocketConfig
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameDrawTest {

    private val clientId = generateNonce()

    @Test
    fun `testing if the board is complete and it results in a draw configuration`() = testApplication {
        val socketClient = createClient {
            setWebsocketConfig()
        }
        coroutineScope {
            val user1 = async {
                socketClient.webSocket(
                    method = HttpMethod.Get,
                    host = "localhost",
                    path = "${TestApiPaths.WEBSOCKET_GAME_API_PATHS}=$clientId",
                    port = 80
                ) {

                    repeat(3) { row ->
                        repeat(3) { col ->
                            if (row + col % 2 == 0) {
                                sendSerialized(
                                    ServerReceiveEvents.ReceiveGameData(
                                        data = GameReceiveDataDto(
                                            clientId = clientId,
                                            boardPosition = BoardPositionDto(row, col)
                                        )
                                    )
                                )
                            }
                        }
                    }
                    when (val received = receiveDeserialized<ServerSendEventsDto>()) {
                        is ServerSendEventsDto.ServerGameState -> {
                            assertTrue(
                                message = "There is no winner found ",
                                block = { received.state.board.winningSymbols == null }
                            )
                            assertTrue(
                                message = "The board is in draw configuration ",
                                block = { received.state.board.isDraw },
                            )
                        }

                        is ServerSendEventsDto.ServerMessage -> {}
                    }
                    close(
                        reason = CloseReason(
                            code = CloseReason.Codes.NORMAL,
                            message = "Disconnecting the session",
                        ),
                    )
                }
            }

            val user2 = async {
                socketClient.webSocket(
                    method = HttpMethod.Get,
                    host = "localhost",
                    path = "${TestApiPaths.WEBSOCKET_GAME_API_PATHS}=$clientId",
                    port = 80
                ) {

                    repeat(3) { row ->
                        repeat(3) { col ->
                            if (row + col % 2 == 0) {
                                sendSerialized(
                                    ServerReceiveEvents.ReceiveGameData(
                                        data = GameReceiveDataDto(
                                            clientId = clientId,
                                            boardPosition = BoardPositionDto(row, col)
                                        )
                                    )
                                )
                            }
                        }
                    }

                    when (val received = receiveDeserialized<ServerSendEventsDto>()) {
                        is ServerSendEventsDto.ServerGameState -> {
                            assertTrue(
                                message = "There is no winner found ",
                                block = { received.state.board.winningSymbols == null }
                            )
                            assertTrue(
                                message = "The board is in draw configuration ",
                                block = { received.state.board.isDraw },
                            )
                        }

                        is ServerSendEventsDto.ServerMessage -> {}
                    }
                    close(
                        reason = CloseReason(
                            code = CloseReason.Codes.NORMAL,
                            message = "Disconnecting the session",
                        ),
                    )
                }
            }
            listOf(user1, user2).awaitAll()
        }
    }
}