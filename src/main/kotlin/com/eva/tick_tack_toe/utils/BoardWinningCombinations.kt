package com.eva.tick_tack_toe.utils

import com.eva.tick_tack_toe.feature_game.models.BoardPosition


sealed class BoardWinningCombinations(
    val combinations: List<List<BoardPosition>>
) {
    data object HorizontalCombinations : BoardWinningCombinations(
        combinations = List(3) { x ->
            List(3) { y -> BoardPosition(x, y) }
        }
    )

    data object VerticalCombinations : BoardWinningCombinations(
        combinations = List(3) { x ->
            List(3) { y -> BoardPosition(y, x) }
        }
    )

    data object DiagonalCombinations : BoardWinningCombinations(
        combinations = listOf(
            listOf(BoardPosition(0, 0), BoardPosition(1, 1), BoardPosition(2, 2)),
            listOf(BoardPosition(0, 2), BoardPosition(1, 1), BoardPosition(2, 0)),
        )
    )

}