package com.eva.tick_tack_toe.feature_game.domain.facade

import io.ktor.websocket.*

interface SocketGameFacade {

    fun connect(session: WebSocketSession)
    fun disconnect()
    fun send()
}