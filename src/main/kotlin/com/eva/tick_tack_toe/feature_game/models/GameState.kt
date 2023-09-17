package com.eva.tick_tack_toe.feature_game.models

import com.eva.tick_tack_toe.utils.BoardSymbols


typealias GameBoardFace = List<List<BoardSymbols>>

data class GameState(
    val boardState: GameBoardFace = emptyBoardState(),
    val isDraw: Boolean = false,
    val winnerSymbol: BoardSymbols? = null
) {
    companion object {
        fun emptyBoardState() = List(3) { List(3) { BoardSymbols.Blank } }
    }
}
