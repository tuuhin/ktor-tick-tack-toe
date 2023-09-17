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
import kotlin.test.Test
import kotlin.test.assertContains

class GameWinnerTest {

    private val clientId = generateNonce()

    @Test
    fun `testing if there is a winner if the winning credentials are met`() = testApplication {
        val socketClient = createClient {
            setWebsocketConfig()
        }

        socketClient.webSocket(
            method = HttpMethod.Get,
            host = "localhost",
            path = "${TestApiPaths.WEBSOCKET_GAME_API_PATHS}=$clientId",
            port = 80
        ) {

            repeat(3) { idx ->
                sendSerialized(
                    ServerReceiveEvents.ReceiveGameData(
                        data = GameReceiveDataDto(
                            clientId = clientId,
                            boardPosition = BoardPositionDto(idx, idx)
                        )
                    )
                )
                if (idx < 3) {
                    receiveDeserialized<ServerSendEventsDto>()
                }
            }

            when (val received = receiveDeserialized<ServerSendEventsDto>()) {
                is ServerSendEventsDto.ServerGameState -> {
                    assertContains(
                        array = arrayOf('X', 'O'),
                        element = received.state.board.winningSymbols,
                        message = "THere is a winner present "
                    )
                }

                is ServerSendEventsDto.ServerMessage -> {}
            }
            close(reason = CloseReason(code = CloseReason.Codes.NORMAL, message = "Disconnecting the session"))
        }
    }
}