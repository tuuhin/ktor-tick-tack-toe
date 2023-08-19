package com.eva.tick_tack_toe.feature_room

import com.eva.tick_tack_toe.TestApiPaths
import com.eva.tick_tack_toe.dto.BaseHttpResponse
import com.eva.tick_tack_toe.feature_room.dto.CreateRoomSerializer
import com.eva.tick_tack_toe.feature_room.dto.RoomSerializer
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomRouteTests {

    @Test
    fun `test create room requests with rounds and check if the room is join-able or not`() = testApplication {
        val newClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var roomIdReceived: String

        newClient.post {
            url {
                host = "localhost"
                port = 80
                path(TestApiPaths.CREATE_ROOM_API_ROUTE)
            }
            headers { append(HttpHeaders.ContentType, ContentType.Application.Json) }
            contentType(ContentType.Application.Json)
            setBody(CreateRoomSerializer(rounds = 2))
        }.apply {
            assertEquals(status, HttpStatusCode.OK)
            body<RoomSerializer>().apply {
                assertEquals(rounds, 2)
                assertEquals(room.length, generateNonce().length)
                roomIdReceived = room
            }
        }

        newClient.post {
            url {
                host = "localhost"
                port = 80
                path(TestApiPaths.CHECK_ROOM_API_ROUTE)
            }
            headers { append(HttpHeaders.ContentType, ContentType.Application.Json) }
            contentType(ContentType.Application.Json)
            setBody(RoomSerializer(room = roomIdReceived, rounds = 2))
        }.apply {
            assertEquals(status, HttpStatusCode.OK)
            body<BaseHttpResponse>().apply {
                assertEquals(detail, "Room is join-able for players")
            }
        }


    }

    @Test
    fun `test create room requests without mentioning rounds`() = testApplication {
        val newClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = newClient.post {
            url {
                host = "localhost"
                port = 80
                path(TestApiPaths.CREATE_ROOM_API_ROUTE)
            }
            headers { append(HttpHeaders.ContentType, ContentType.Application.Json) }
            contentType(ContentType.Application.Json)
            setBody(CreateRoomSerializer())
        }
        assertEquals(response.status, HttpStatusCode.OK)

        val responseBody = response.body<RoomSerializer>()
        assertEquals(responseBody.rounds, 1)
        assertEquals(responseBody.room.length, generateNonce().length)
    }
}