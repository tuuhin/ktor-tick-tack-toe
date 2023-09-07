package com.eva.tick_tack_toe.plugins

import com.eva.tick_tack_toe.utils.GameSessions
import com.eva.tick_tack_toe.utils.constants.GameConstants
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun Application.configureSecurity() {

    install(Sessions) {
        cookie<GameSessions>(GameConstants.GAME_SESSION_NAME) {
            cookie.httpOnly = true
        }
    }
}
