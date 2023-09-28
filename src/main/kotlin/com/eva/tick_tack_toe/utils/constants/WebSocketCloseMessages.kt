package com.eva.tick_tack_toe.utils.constants

object WebSocketCloseMessages {

    const val ROOM_ID_NOT_FOUND = "Room id not found"
    const val INTERNAL_ERROR = "Internal Error Occurred"
    const val WEBSOCKET_SERIALIZATION_ERROR = "Cannot Serialize the input data,check out the api properly"
    const val CHANNEL_CLOSED_ERROR = "The received channel was close for some reason,Cannot receive Data"
    const val CLIENT_ID_NOT_PROVIDED = "Client id not found in the query parameters"
}