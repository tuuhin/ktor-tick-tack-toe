package com.eva.tick_tack_toe.feature_room.models

import io.ktor.websocket.*

data class GamePlayerModel(
    val userName: String,
    val socketSession: WebSocketSession,
    val clientId: String,
    val winCount: Int,
    val drawCount: Int,
    val looseCount: Int
)
