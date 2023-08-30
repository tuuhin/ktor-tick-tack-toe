package com.eva.tick_tack_toe.feature_room

import com.eva.tick_tack_toe.TestApiPaths
import com.eva.tick_tack_toe.feature_room.dto.CreateRoomSerializer
import com.eva.tick_tack_toe.feature_room.dto.RoomSerializer
import com.eva.tick_tack_toe.feature_room.dto.VerifyRoomDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class PlayerApi(
    private val client: HttpClient
) {
    suspend fun createRoom(rounds: Int = 1): RoomSerializer {
        val response = client.post {
            url {
                path(TestApiPaths.CREATE_ROOM_API_ROUTE)
            }
            contentType(ContentType.Application.Json)
            setBody(CreateRoomSerializer(rounds = rounds))
        }
        return when {
            response.status.isSuccess() -> response.body()
            else -> throw ClientRequestException(
                response,
                response.bodyAsText()
            )
        }
    }

    suspend fun checkRoom(roomId: String): VerifyRoomDto {
        val response = client.post {
            url {
                path(TestApiPaths.CHECK_ROOM_API_ROUTE)
            }
            contentType(ContentType.Application.Json)
            setBody(RoomSerializer(room = roomId))
        }
        return when {
            response.status.isSuccess() -> response.body()
            else -> throw ClientRequestException(
                response,
                response.bodyAsText()
            )
        }
    }

}