package com.eva.tick_tack_toe.feature_room.routes

import com.eva.tick_tack_toe.dto.BaseHttpException
import com.eva.tick_tack_toe.feature_room.dto.CreateRoomSerializer
import com.eva.tick_tack_toe.feature_room.dto.RoomSerializer
import com.eva.tick_tack_toe.utils.constants.ApiPaths
import com.eva.tick_tack_toe.utils.RoomAndPlayerServer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.koin.ktor.ext.inject

fun Route.createRoomRoute() {
    route(path = ApiPaths.CREATE_ROOM) {
        val server by inject<RoomAndPlayerServer>()

        get {
            call.respond(
                status = HttpStatusCode.NotAcceptable,
                message = BaseHttpException("Get method not allowed")
            )
        }

        post {
            call.receiveNullable<CreateRoomSerializer>()
                ?.let { serializer ->
                    generateNonce()
                        .also { roomId ->
                        server.addToRoom(room = roomId, board = serializer.rounds)
                            ?.let { room ->
                                call.respond(
                                    status = HttpStatusCode.OK,
                                    message = RoomSerializer(room = room.room, rounds = room.boardCount)
                                )
                            }
                    }
                } ?: run {
                call.respond(
                    status = HttpStatusCode.PreconditionFailed,
                    message = BaseHttpException(detail = "Failed to create a room")
                )
            }
        }
    }
}