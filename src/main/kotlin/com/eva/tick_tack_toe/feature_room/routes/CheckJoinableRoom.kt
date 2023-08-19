package com.eva.tick_tack_toe.feature_room.routes

import com.eva.tick_tack_toe.dto.BaseHttpException
import com.eva.tick_tack_toe.dto.BaseHttpResponse
import com.eva.tick_tack_toe.feature_room.dto.RoomSerializer
import com.eva.tick_tack_toe.utils.constants.ApiPaths
import com.eva.tick_tack_toe.utils.RoomAndPlayerServer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


/**
 * Checks if the room is join able i.e, the room exits and the number of players is lesser than 2.
 */
fun Route.checkJoinRoomRequest() {
    val server by inject<RoomAndPlayerServer>()
    route(path = ApiPaths.CHECK_ROOM_PATH) {
        get {
            call.respond(
                status = HttpStatusCode.NotAcceptable,
                message = BaseHttpException("Get method not allowed")
            )
        }
        post {
            //create a room id that is a string
            call.receiveNullable<RoomSerializer>()
                ?.let { serializer ->
                    server.gameRooms.keys
                        .find { roomId -> roomId == serializer.room }
                        ?.let { key ->
                            server.gameRooms[key]?.let { model ->
                                when (model.players.size) {
                                    0, 1 -> call.respond(
                                        status = HttpStatusCode.OK,
                                        message = BaseHttpResponse(detail = "Room is join-able for players")
                                    )

                                    else -> call.respond(
                                        status = HttpStatusCode.NotAcceptable,
                                        message = BaseHttpException(detail = "Room already filled")
                                    )
                                }
                            }
                        } ?: run {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = BaseHttpException(detail = "Provided room key do not exists")
                        )
                    }
                } ?: run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BaseHttpException(detail = "Didn't find proper data")
                )
            }
        }
    }
}