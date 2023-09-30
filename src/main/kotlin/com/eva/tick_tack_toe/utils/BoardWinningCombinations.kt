package com.eva.tick_tack_toe.utils

import com.eva.tick_tack_toe.feature_game.models.BoardPosition

/**
 * All the available Winning Combinations i.e., Horizontal, Vertical and Diagonal.
 */
sealed class BoardWinningCombinations(
    val combinations: List<List<BoardPosition>>
) {
    /**
     * Includes All the Horizontal or Row-Based Combinations
     */
    data object HorizontalCombinations : BoardWinningCombinations(
        combinations = List(3) { x ->
            List(3) { y -> BoardPosition(x, y) }
        }
    )

    /**
     * Includes All the Vertical or Column-Based Combinations
     */
    data object VerticalCombinations : BoardWinningCombinations(
        combinations = List(3) { x ->
            List(3) { y -> BoardPosition(y, x) }
        }
    )

    /**
     * Includes All the Diagonal Combinations
     */
    data object DiagonalCombinations : BoardWinningCombinations(
        combinations = listOf(
            List(3) { idx -> BoardPosition(idx, idx) },
            List(3) { idx -> BoardPosition(idx, 2 - idx) },
        )
    )

    companion object {
        /**
         * List of winning combinations; Rather, then computing the results, multiple times the winning combinations
         * are only checked.
         */
        val WINNING_COMBINATIONS = listOf(HorizontalCombinations, VerticalCombinations, DiagonalCombinations)
    }
}