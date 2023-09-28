package com.eva.tick_tack_toe.feature_room

import com.eva.tick_tack_toe.dto.BaseHttpResponse
import com.eva.tick_tack_toe.setNegotiationConfig
import com.eva.tick_tack_toe.utils.constants.ApiMessage
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlin.test.*

class RoomRouteTests {

    @Test
    fun `test create room requests with rounds and check if the room is join-able or not`() = testApplication {
        val testClient = createClient {
            setNegotiationConfig()
        }

        val playerApi = PlayerApi(testClient)

        val createRoom = playerApi.createRoom(2)
        assertTrue(message = "checking the number of rounds") {
            createRoom.rounds == 2
        }

        assertTrue(message = "There is a roomId received which is not empty and is not blank") {
            createRoom.room.isNotEmpty() && createRoom.room.isNotBlank()
        }

        val roomIdReceived = createRoom.room

        val verifyRoom = playerApi.checkRoom(roomIdReceived)

        assertTrue(message = "checking the number of rounds is it matching with with the results returned during creation") {
            verifyRoom.roomSerializer.rounds == 2
        }
        assertTrue(message = " Room is join-able or not") {
            verifyRoom.message == ApiMessage.ROOM_JOIN_ABLE_MESSAGE
        }

    }

    @Test
    fun `test create room requests without mentioning rounds`() = testApplication {
        val testClient = createClient {
            setNegotiationConfig()
        }
        val player = PlayerApi(testClient)
        val room = player.createRoom()

        assertTrue(message = "checking the number of rounds") {
            room.rounds == 1
        }

        assertTrue(message = "There is a roomId received which is not empty and is not blank") {
            room.room.isNotEmpty() && room.room.isNotBlank()
        }
    }

    @Test
    fun `test a random room which is not present testing room not found exception`() = testApplication {
        val testClient = createClient {
            setNegotiationConfig()
        }
        val playerApi = PlayerApi(testClient)
        val roomId = generateNonce()
        try {
            playerApi.checkRoom(roomId)
        } catch (e: ClientRequestException) {
            val clientException = e.response.body<BaseHttpResponse>()

            assertTrue(message = "Checking the status code to be same or not") {
                HttpStatusCode.BadRequest == e.response.status
            }

            assertTrue(message = "Checking the client exception is room not found") {
                ApiMessage.ROOM_KEY_DO_NOT_EXITS == clientException.detail
            }
        }
    }
}