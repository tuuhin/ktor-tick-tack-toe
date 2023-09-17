package com.eva.tick_tack_toe.feature_room.models

import com.eva.tick_tack_toe.feature_game.models.GamePoints
import com.eva.tick_tack_toe.utils.BoardSymbols
import io.ktor.server.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow

data class GamePlayerModel(
    val userName: String,
    val session: WebSocketServerSession,
    val clientId: String,
    val pointsFlow: MutableStateFlow<GamePoints> = MutableStateFlow(GamePoints()),
    // val symbol: BoardSymbols = BoardSymbols.XSymbol
    // REPLACE THIS AT END
    val symbol: BoardSymbols = BoardSymbols.entries.filter { it != BoardSymbols.Blank }.random()
) {
    val points: GamePoints
        get() = pointsFlow.value
}
