package com.eva.tick_tack_toe.plugins

import com.eva.tick_tack_toe.utils.constants.GameConstants
import com.eva.tick_tack_toe.utils.GameSessions
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import kotlin.time.Duration.Companion.days


fun Application.configureSecurity() {

    install(Sessions) {
        cookie<GameSessions>(GameConstants.GAME_SESSION_NAME) {
            val isDevMode = this@configureSecurity.environment.developmentMode
            if (!isDevMode) {
                cookie.secure = true
                cookie.maxAge = 1.days
            }
            cookie.httpOnly = true
        }
    }

}
