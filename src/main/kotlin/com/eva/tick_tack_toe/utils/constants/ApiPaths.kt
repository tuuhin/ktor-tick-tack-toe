package com.eva.tick_tack_toe.utils.constants

object ApiPaths {

    const val ROOM_ROUTE = "/room"

    const val CREATE_ROOM_PATH = "/create"
    const val CHECK_ROOM_PATH = "/join"

    const val WEBSOCKET_ROUTE = "/ws"

    const val GAME_SOCKET_PATH = "/game"
    const val GAME_SOCKET_PATH_WITH_ROOM_PARAMS = "/game/{${GameConstants.GAME_ROOM_ID_PARAMS}}"
}