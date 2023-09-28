package com.eva.tick_tack_toe.feature_room.routes

import com.eva.tick_tack_toe.dto.BaseHttpResponse
import com.eva.tick_tack_toe.feature_room.dto.RoomSerializer
import com.eva.tick_tack_toe.feature_room.dto.VerifyRoomDto
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
 * Verify the room if the room is join able
 *
 * The serializer data need to satisfy
 *  1. Room with the given roomId exits
 *  2. Room should have player size lesser than 2
 */
fun Route.checkJoinRoomRequest() {
    val server by inject<RoomAndPlayerServer>()
    route(path = ApiPaths.CHECK_ROOM_PATH) {
        get {
            call.respond(
                status = HttpStatusCode.NotAcceptable,
                message = BaseHttpResponse(ApiMessage.ROOM_GET_REQUEST_MESSAGE)
            )
        }
        post {
            try {
                val serializer = call.receive<RoomSerializer>()
                val roomId = server.gameRooms.keys
                    .find { roomId -> roomId == serializer.room }

                if (roomId == null) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = BaseHttpResponse(detail = ApiMessage.ROOM_KEY_DO_NOT_EXITS)
                    )
                    return@post
                }

                val gameRoom = server.gameRooms[roomId]

                if (gameRoom == null) {
                    call.respond(
                        status = HttpStatusCode.NotAcceptable,
                        message = BaseHttpResponse(detail = ApiMessage.ROOM_JOIN_INVALID_DATA)
                    )
                    return@post
                }
                val roomContentSize = gameRoom.players.size
                if (roomContentSize < 2) {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = VerifyRoomDto(
                            message = ApiMessage.ROOM_JOIN_ABLE_MESSAGE,
                            roomSerializer = RoomSerializer(
                                room = serializer.room,
                                rounds = gameRoom.boardCount
                            )
                        )
                    )
                    return@post
                }
                call.respond(
                    status = HttpStatusCode.NotAcceptable,
                    message = BaseHttpResponse(detail = ApiMessage.ROOM_FILLED_MESSAGE)
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
                e.printStackTrace()
                call.respond(
                    status = HttpStatusCode.FailedDependency,
                    message = BaseHttpResponse(ApiMessage.UNKNOWN_EXCEPTION_MESSAGE)
                )
            }
        }
    }
}