package com.eva.tick_tack_toe.feature_room.routes

import com.eva.tick_tack_toe.dto.BaseHttpException
import com.eva.tick_tack_toe.dto.BaseHttpResponse
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
 * Checks if the room is join able i.e, the room exits and the number of players is lesser than 2.
 */
fun Route.checkJoinRoomRequest() {
    val server by inject<RoomAndPlayerServer>()
    route(path = ApiPaths.CHECK_ROOM_PATH) {
        get {
            call.respond(
                status = HttpStatusCode.NotAcceptable,
                message = BaseHttpException(ApiMessage.ROOM_GET_REQUEST_MESSAGE)
            )
        }
        post {
            //create a room id that is a string
            try {
                call.receiveNullable<RoomSerializer>()
                    ?.let { serializer ->
                        server.gameRooms.keys
                            .find { roomId -> roomId == serializer.room }
                            ?.let { key ->
                                server.gameRooms[key]?.let { model ->
                                    val roomContentSize = model.players.size
                                    if (roomContentSize < 2) {
                                        call.respond(
                                            status = HttpStatusCode.OK,
                                            message = BaseHttpResponse(detail = ApiMessage.ROOM_JOIN_ABLE_MESSAGE)
                                        )
                                        return@post
                                    }
                                    call.respond(
                                        status = HttpStatusCode.NotAcceptable,
                                        message = BaseHttpException(detail = ApiMessage.ROOM_FILLED_MESSAGE)
                                    )
                                }
                            } ?: call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = BaseHttpException(detail = ApiMessage.ROOM_KEY_DO_NOT_EXITS)
                        )
                    } ?: call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BaseHttpException(detail = ApiMessage.ROOM_JOIN_INVALID_DATA)
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