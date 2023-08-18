package com.eva.tick_tack_toe.feature_room.models

import com.eva.tick_tack_toe.utils.BoardSymbols
import io.ktor.server.websocket.*

data class GamePlayerModel(
    val userName: String,
    val session: WebSocketServerSession,
    val clientId: String,
    val winCount: Int = 0,
    val drawCount: Int = 0,
    val looseCount: Int = 0,
    //   val symbol: BoardSymbols = BoardSymbols.XSymbol
    // REPLACE THIS AT END
    val symbol: BoardSymbols = BoardSymbols.entries.filter { it != BoardSymbols.Blank }.random()
)
