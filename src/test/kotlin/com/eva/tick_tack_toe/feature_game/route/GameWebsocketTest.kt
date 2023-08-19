package com.eva.tick_tack_toe.feature_game.route

import com.eva.tick_tack_toe.feature_game.dto.BoardGameReceiveDataDto
import com.eva.tick_tack_toe.feature_game.dto.BoardPositionDto
import com.eva.tick_tack_toe.feature_game.dto.ServerSendEventTypes
import com.eva.tick_tack_toe.feature_game.dto.ServerSendEventsDto
import com.eva.tick_tack_toe.feature_game.mapper.toDto
import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import com.eva.tick_tack_toe.TestApiPaths
import com.eva.tick_tack_toe.feature_game.utils.TestBoardCombinations
import com.eva.tick_tack_toe.feature_room.dto.GamePlayerDto
import com.eva.tick_tack_toe.utils.BoardSymbols
import com.eva.tick_tack_toe.utils.ext.toBoardLayout
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class GameWebsocketTest {

    private val  clientId = generateNonce()
    private lateinit var player: GamePlayerDto

    @Test
    fun `check the working of the websockets`() = testApplication {

        val socketClient = createClient {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
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
                    val currentPlayer = GamePlayerDto(
                        userName = "Anonymous",
                        clientId = clientId,
                        winCount = 0,
                        lostCount = 0,
                        drawCount = 0,
                        playerSymbol = received.state.player.playerSymbol
                    )

                    player = currentPlayer

                    val room = GameRoomModel(room = received.state.board.room, isAnonymous = true).toDto()

                    assertEquals(received.state.board, room)

                    assertEquals(
                        received.type,
                        ServerSendEventTypes.GAME_STATE_TYPE.type,
                        "check same receive type "
                    )
                    assertEquals(
                        received.state.player,
                        currentPlayer,
                        "check same player or not"
                    )
                }

                else -> {}
            }

            sendSerialized(
                BoardGameReceiveDataDto(
                    clientId = clientId,
                    symbol = player.playerSymbol,
                    pos = BoardPositionDto(0, 0)
                )
            )

            when (val received = receiveDeserialized<ServerSendEventsDto>()) {
                is ServerSendEventsDto.ServerGameState -> {
                    assertEquals(received.state.player, player)

                    val layout = received.state.board.boardLayout.toBoardLayout()

                    assertEquals(
                        received.type,
                        ServerSendEventTypes.GAME_STATE_TYPE.type,
                        "check same receive type "
                    )

                    val playerSymbol = BoardSymbols.fromSymbol(player.playerSymbol)

                    assertEquals(layout, TestBoardCombinations.XFilledAtTopLeftCorner(playerSymbol).combinations)

                }

                else -> {}
            }

            sendSerialized(
                BoardGameReceiveDataDto(
                    clientId = clientId,
                    symbol = player.playerSymbol,
                    pos = BoardPositionDto(1, 1)
                )
            )
            // Skipping the socket receive
            receiveDeserialized<ServerSendEventsDto>()

            sendSerialized(
                BoardGameReceiveDataDto(
                    clientId = clientId,
                    symbol = player.playerSymbol,
                    pos = BoardPositionDto(2, 2)
                )
            )

            when (val received = receiveDeserialized<ServerSendEventsDto>()) {
                is ServerSendEventsDto.ServerGameState -> {
                    assertEquals(received.state.player, player)

                    val layout = received.state.board.boardLayout.toBoardLayout()

                    assertEquals(
                        received.type,
                        ServerSendEventTypes.GAME_STATE_TYPE.type,
                        "check same receive type "
                    )

                    val playerSymbol = BoardSymbols.fromSymbol(player.playerSymbol)

                    assertEquals(
                        layout,
                        TestBoardCombinations.DiagonalFilledWithSameSymbol(playerSymbol).combinations
                    )
                }

                else -> {}
            }

            close(reason = CloseReason(code = CloseReason.Codes.NORMAL, message = "Disconnecting the session"))

        }
    }
}

