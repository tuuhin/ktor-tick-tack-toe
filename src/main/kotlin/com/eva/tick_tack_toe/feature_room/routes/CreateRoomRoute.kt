package com.eva.tick_tack_toe.feature_room.routes

import com.eva.tick_tack_toe.dto.BaseHttpResponse
import com.eva.tick_tack_toe.feature_room.dto.CreateRoomSerializer
import com.eva.tick_tack_toe.feature_room.dto.RoomSerializer
import com.eva.tick_tack_toe.utils.RoomAndPlayerServer
import com.eva.tick_tack_toe.utils.constants.ApiMessage
import com.eva.tick_tack_toe.utils.constants.ApiPaths
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerializationException
import org.koin.ktor.ext.inject

/**
 * Create Room route
 *
 * A new room is created with a randomId, and the specified rounds count.
 * BoardCount should be greater than 0
 */
fun Route.createRoomRoute() = route(path = ApiPaths.CREATE_ROOM_PATH) {
    val server by inject<RoomAndPlayerServer>()

    get {
        call.respond(
            status = HttpStatusCode.NotAcceptable,
            message = BaseHttpResponse(ApiMessage.ROOM_GET_REQUEST_MESSAGE)
        )
    }
    post {
        try {
            val serializer = call.receive<CreateRoomSerializer>()
            if (serializer.rounds > 0) {
                val game = server.createGameRoom(board = serializer.rounds)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = RoomSerializer(room = game.room, rounds = game.boardCount)
                )
                return@post
            }
            call.respond(
                status = HttpStatusCode.PreconditionFailed,
                message = BaseHttpResponse(detail = ApiMessage.ROOM_CREATION_FAILED)
            )
        } catch (e: SerializationException) {
            call.respond(
                status = HttpStatusCode.NotAcceptable,
                message = BaseHttpResponse(detail = ApiMessage.SERIALIZATION_EXCEPTION_MESSAGE)
            )
        } catch (e: ContentTransformationException) {
            call.respond(
                status = HttpStatusCode.NotAcceptable,
                message = BaseHttpResponse(detail = ApiMessage.WRONG_DTO_PROVIDED_OR_RECEIVED)
            )
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.FailedDependency,
                message = BaseHttpResponse(ApiMessage.UNKNOWN_EXCEPTION_MESSAGE)
            )
        }
    }
}