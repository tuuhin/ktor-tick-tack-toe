package com.eva.tick_tack_toe.plugins

import com.eva.tick_tack_toe.feature_game.routes.anonymousGameSocketRoute
import com.eva.tick_tack_toe.feature_game.routes.gameSocketRoute
import com.eva.tick_tack_toe.feature_room.routes.checkJoinRoomRequest
import com.eva.tick_tack_toe.feature_room.routes.createRoomRoute
import com.eva.tick_tack_toe.interceptor.configureInterceptor
import com.eva.tick_tack_toe.utils.constants.ApiPaths
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    install(ContentNegotiation) {
        json()
    }

    routing {
        route(path = ApiPaths.ROOM_ROUTE) {
            createRoomRoute()
            checkJoinRoomRequest()
        }
        route(path = ApiPaths.WEBSOCKET_ROUTE) {
            configureInterceptor()
            anonymousGameSocketRoute()
            gameSocketRoute()
        }
    }
}
