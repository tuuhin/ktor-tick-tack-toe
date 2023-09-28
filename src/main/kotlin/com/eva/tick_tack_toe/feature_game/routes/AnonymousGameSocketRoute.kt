package com.eva.tick_tack_toe.feature_game.routes

import com.eva.tick_tack_toe.feature_game.game.RealtimeBoardGame
import com.eva.tick_tack_toe.utils.constants.ApiPaths
import com.eva.tick_tack_toe.utils.constants.GameConstants
import com.eva.tick_tack_toe.utils.constants.WebSocketCloseMessages
import io.ktor.serialization.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.koin.ktor.ext.inject

/**
 * An anonymous game route,if the user don't have a room id then this route will be used
 */
fun Route.anonymousGameSocketRoute() = webSocket(path = ApiPaths.GAME_SOCKET_PATH) {

    val clientId = call.request.queryParameters[GameConstants.GAME_CLIENT_ID_PARAMS]

    clientId?.let { id ->

        val boardGame: RealtimeBoardGame by inject()

        val userName = call.request.queryParameters[GameConstants.CLIENT_USERNAME_PARAMS]
            ?: GameConstants.GAME_USERNAME_ANONYMOUS

        val player = boardGame.onConnect(
            session = this,
            userName = userName,
            clientId = id,
        )

        var broadcast: Job? = null
        var receive: Job? = null

        try {

            broadcast = launch(Dispatchers.IO) { boardGame.broadCastGameState(session = this@webSocket) }

            receive = launch(Dispatchers.IO) { boardGame.onReceiveEvents(session = this@webSocket) }
            // As receive is the listener needed to join this, so this its wait until the coroutine is over
            receive.join()

        } catch (e: ClosedReceiveChannelException) {
            close(
                reason = CloseReason(
                    code = CloseReason.Codes.INTERNAL_ERROR,
                    message = WebSocketCloseMessages.CHANNEL_CLOSED_ERROR
                )
            )
        } catch (e: WebsocketDeserializeException) {
            close(
                reason = CloseReason(
                    code = CloseReason.Codes.CANNOT_ACCEPT,
                    message = WebSocketCloseMessages.WEBSOCKET_SERIALIZATION_ERROR
                )
            )
        } catch (e: WebsocketContentConvertException) {
            close(
                reason = CloseReason(
                    code = CloseReason.Codes.CANNOT_ACCEPT,
                    message = WebSocketCloseMessages.WEBSOCKET_SERIALIZATION_ERROR
                )
            )
        } catch (e: Exception) {
            close(
                reason = CloseReason(
                    code = CloseReason.Codes.INTERNAL_ERROR,
                    message = WebSocketCloseMessages.INTERNAL_ERROR
                )
            )
        } finally {
            broadcast?.cancel()
            receive?.cancel()
            boardGame.onDisconnect(player)
        }
    } ?: close(
        reason = CloseReason(
            code = CloseReason.Codes.CANNOT_ACCEPT,
            message = WebSocketCloseMessages.CLIENT_ID_NOT_PROVIDED
        )
    )
}
