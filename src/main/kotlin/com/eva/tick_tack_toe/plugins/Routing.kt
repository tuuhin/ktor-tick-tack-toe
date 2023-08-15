package com.eva.tick_tack_toe.plugins

import com.eva.tick_tack_toe.feature_room.routes.checkJoinRoomRequest
import com.eva.tick_tack_toe.feature_room.routes.createRoomRoute
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    install(ContentNegotiation) {
        json()
    }

    routing {
        createRoomRoute()
        checkJoinRoomRequest()
    }
}
