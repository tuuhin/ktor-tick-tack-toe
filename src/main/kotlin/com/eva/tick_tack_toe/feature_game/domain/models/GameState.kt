package com.eva.tick_tack_toe.feature_game.domain.models

import com.eva.tick_tack_toe.utils.BoardSymbols

data class GameState(
    val playerSymbol: BoardSymbols = BoardSymbols.XSymbol,
    val boardState: List<List<BoardSymbols>> = emptyBoardState(),
    val isDraw: Boolean = false,
    val winnerSymbol: BoardSymbols? = null
) {
    companion object {
        fun emptyBoardState(): List<List<BoardSymbols>> {
            return listOf(
                listOf(BoardSymbols.Blank, BoardSymbols.Blank, BoardSymbols.Blank),
                listOf(BoardSymbols.Blank, BoardSymbols.Blank, BoardSymbols.Blank),
                listOf(BoardSymbols.Blank, BoardSymbols.Blank, BoardSymbols.Blank)
            )
        }
    }
}
