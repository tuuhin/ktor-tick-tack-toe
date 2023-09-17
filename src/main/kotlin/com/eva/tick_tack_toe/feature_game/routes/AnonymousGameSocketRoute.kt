package com.eva.tick_tack_toe.feature_game.routes

import com.eva.tick_tack_toe.Logger
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

        try {

            val broadcast = launch(Dispatchers.IO) { boardGame.broadCastGameState() }

            val receive = launch(Dispatchers.IO) { boardGame.onReceiveEvents(session = this@webSocket) }

            val jobs = listOf(broadcast, receive)
            jobs.joinAll()

        } catch (e: ClosedReceiveChannelException) {
            e.printStackTrace()
            Logger.error(e.localizedMessage)

        } catch (e: CancellationException) {
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
                    message = WebSocketCloseMessages.INTERNAL_ERROR
                )
            )
        }
    } ?: close(
        reason = CloseReason(
            code = CloseReason.Codes.CANNOT_ACCEPT,
            message = WebSocketCloseMessages.CLIENT_ID_NOT_PROVIDED
        )
    )
}
