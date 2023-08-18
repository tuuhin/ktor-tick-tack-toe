package com.eva.tick_tack_toe.interceptor

import com.eva.tick_tack_toe.Logger
import com.eva.tick_tack_toe.utils.constants.GameConstants
import com.eva.tick_tack_toe.utils.GameSessions
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*

/** This interceptor should be only available for websocket connections.
 * Interceptor ensures that there is a client id in the session passed as a query parameter
 */
fun Route.configureInterceptor() = intercept(ApplicationCallPipeline.Call) {
    val sessions = call.sessions.get<GameSessions>()
    if (sessions == null) {
        call.request.queryParameters[GameConstants.GAME_CLIENT_ID_PARAMS]
            ?.let { clientId ->
                call.sessions.set(
                    GameSessions(clientId = clientId, serverId = generateNonce())
                )
            } ?: call.sessions.clear<GameSessions>()
    }
    if (application.developmentMode) {
        Logger.info("THE NEW CLIENT_ID  IS ${call.sessions.get<GameSessions>()?.clientId}")
    }
}


