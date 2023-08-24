package com.eva.tick_tack_toe.feature_room.routes

import com.eva.tick_tack_toe.dto.BaseHttpException
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
 * Create Room route,creates a new room by passing the number of board count as a body
 */
fun Route.createRoomRoute() {
    route(path = ApiPaths.CREATE_ROOM_PATH) {

        val server by inject<RoomAndPlayerServer>()

        get {
            call.respond(
                status = HttpStatusCode.NotAcceptable,
                message = BaseHttpException(ApiMessage.ROOM_GET_REQUEST_MESSAGE)
            )
        }
        post {
            try {
                call.receiveNullable<CreateRoomSerializer>()
                    ?.let { serializer ->
                        val game = server.createGameRoom(board = serializer.rounds)
                        call.respond(
                            status = HttpStatusCode.OK,
                            message = RoomSerializer(room = game.room, rounds = game.boardCount)
                        )
                    } ?: call.respond(
                    status = HttpStatusCode.PreconditionFailed,
                    message = BaseHttpException(detail = ApiMessage.ROOM_CREATION_FAILED)
                )
            } catch (e: SerializationException) {
                call.respond(
                    status = HttpStatusCode.NoContent,
                    message = BaseHttpException(ApiMessage.SERIALIZATION_EXCEPTION_MESSAGE)
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.FailedDependency,
                    message = BaseHttpException(ApiMessage.UNKNOWN_EXCEPTION_MESSAGE)
                )
            }
        }
    }
}