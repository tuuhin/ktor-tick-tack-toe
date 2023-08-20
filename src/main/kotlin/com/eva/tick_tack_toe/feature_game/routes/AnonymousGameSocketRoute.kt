package com.eva.tick_tack_toe.feature_game.routes

import com.eva.tick_tack_toe.Logger
import com.eva.tick_tack_toe.feature_game.game.RealtimeBoardGame
import com.eva.tick_tack_toe.utils.GameSessions
import com.eva.tick_tack_toe.utils.constants.ApiPaths
import com.eva.tick_tack_toe.utils.constants.GameConstants
import io.ktor.serialization.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.koin.ktor.ext.inject

/**
 * An anonymous game route,if the user don't have a room id then this route will be used
 */
fun Route.anonymousGameSocketRoute() {
    webSocket(path = ApiPaths.GAME_SOCKET_PATH) {

        call.sessions.get<GameSessions>()?.let { session ->

            val boardGame: RealtimeBoardGame by inject()
            val userName = call.request.queryParameters[GameConstants.CLIENT_USERNAME_PARAMS] ?: "Anonymous"

            val player = boardGame.onConnect(
                session = this,
                userName = userName,
                clientId = session.clientId,
            )

            try {
                boardGame.broadCastGameState(this)
                boardGame.onReceiveEvents(this)

            } catch (e: ClosedReceiveChannelException) {
                e.printStackTrace()
                Logger.error(e.localizedMessage)

            } catch (e: WebsocketDeserializeException) {
                e.printStackTrace()
                Logger.error(e.localizedMessage)

            } catch (e: Exception) {
                e.printStackTrace()
                Logger.error(e.localizedMessage)

            } finally {
                boardGame.onDisconnect(player)
                close(
                    reason = CloseReason(
                        code = CloseReason.Codes.INTERNAL_ERROR,
                        message = "Closing the connection"
                    )
                )
            }
        } ?: close(
            reason = CloseReason(
                code = CloseReason.Codes.CANNOT_ACCEPT,
                message = "Cannot accept the connection client id not provided"
            )
        )
    }
}