package com.eva.tick_tack_toe

import com.eva.tick_tack_toe.utils.constants.ApiPaths
import com.eva.tick_tack_toe.utils.constants.GameConstants

object TestApiPaths {
    const val ANONYMOUS_WEBSOCKET_GAME_API_PATHS =
        "${ApiPaths.WEBSOCKET_ROUTE}${ApiPaths.GAME_SOCKET_PATH}?${GameConstants.GAME_CLIENT_ID_PARAMS}"

    const val CREATE_ROOM_API_ROUTE = "${ApiPaths.ROOM_ROUTE}${ApiPaths.CREATE_ROOM_PATH}"

    const val CHECK_ROOM_API_ROUTE = "${ApiPaths.ROOM_ROUTE}${ApiPaths.CHECK_ROOM_PATH}"

}