package com.eva.tick_tack_toe.utils

import com.eva.tick_tack_toe.feature_game.models.BoardPosition


/**
 * All the available Winning Combinations ie, Horizontal,Vertical and Diagonal.
 */
sealed class BoardWinningCombinations(
    val combinations: List<List<BoardPosition>>
) {
    /**
     * Includes All the Horizontal or Row Based Combinations
     */
    data object HorizontalCombinations : BoardWinningCombinations(
        combinations = List(3) { x ->
            List(3) { y -> BoardPosition(x, y) }
        }
    )

    /**
     * Includes All the Vertical or Column Based Combinations
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
            listOf(BoardPosition(0, 0), BoardPosition(1, 1), BoardPosition(2, 2)),
            listOf(BoardPosition(0, 2), BoardPosition(1, 1), BoardPosition(2, 0)),
        )
    )

}