package com.eva.tick_tack_toe.feature_game.models

import com.eva.tick_tack_toe.utils.BoardSymbols


typealias GameBoardFace = List<List<BoardSymbols>>

/**
 * Represents a basic game with the board face and winner and draw data
 * @property face The face of the board
 * @property winnerSymbol The winner of the board
 * @property isDraw Is the game ended in a draw state?
 */
data class GameState(
    val face: GameBoardFace = emptyBoardState(),
    val isDraw: Boolean = false,
    val winnerSymbol: BoardSymbols? = null
) {
    companion object {
        fun emptyBoardState() = List(3) { List(3) { BoardSymbols.Blank } }
    }
}
