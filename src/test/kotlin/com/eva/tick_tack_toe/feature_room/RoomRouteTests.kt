package com.eva.tick_tack_toe.feature_room

import com.eva.tick_tack_toe.dto.BaseHttpResponse
import com.eva.tick_tack_toe.setNegotiationConfig
import com.eva.tick_tack_toe.utils.constants.ApiMessage
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomRouteTests {
    @AfterTest
    fun stopApplication() {
        stopKoin()
    }

    @Test
    fun `test create room requests with rounds and check if the room is join-able or not`() = testApplication {
        val testClient = createClient {
            setNegotiationConfig()
        }

        val playerApi = PlayerApi(testClient)

        val createRoom = playerApi.createRoom(2)
        assertEquals(createRoom.rounds, 2, "checking the number of rounds")
        assertEquals(createRoom.room.length, 16, "room id is anonymous but will be always of length 16")

        val roomIdReceived = createRoom.room

        val verifyRoom = playerApi.checkRoom(roomIdReceived)

        assertEquals(verifyRoom.roomSerializer.rounds, 2, "checking the number of rounds")
        assertEquals(verifyRoom.message, ApiMessage.ROOM_JOIN_ABLE_MESSAGE, message = "Room is join-able or not")

    }

    @Test
    fun `test create room requests without mentioning rounds`() = testApplication {
        val testClient = createClient {
            setNegotiationConfig()
        }
        val player = PlayerApi(testClient)
        val room = player.createRoom()
        assertEquals(room.rounds, 1, "checking the number of rounds")
        assertEquals(room.room.length, 16, "room id is anonymous but will be always of length 16")
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

            assertEquals(
                HttpStatusCode.BadRequest,
                e.response.status,
                message = "Checking the status code to be same or not"
            )

            assertEquals(
                ApiMessage.ROOM_KEY_DO_NOT_EXITS,
                clientException.detail,
                message = "Checking the client exception is room not found"
            )
        }
    }
}