package com.eva.tick_tack_toe.utils.ext

import com.eva.tick_tack_toe.feature_game.models.BoardPosition
import com.eva.tick_tack_toe.feature_game.models.GameBoardFace
import com.eva.tick_tack_toe.utils.BoardSymbols

/**
 * Converts the symbolic board or List of [BoardSymbols] to a Board of filled positions or List of [BoardPosition]
 * @param symbol The [BoardSymbols] for which a list of [BoardPosition] are to be created
 */
fun GameBoardFace.getPositionForSymbol(symbol: BoardSymbols): List<List<BoardPosition>> {
    val finalOut = mutableListOf<List<BoardPosition>>()

    //horizontal
    List(3) horizontal@{ x ->
        val horizontalCombinations = mutableListOf<BoardPosition>()
        List(3) { y ->
            if (symbol == this[x][y])
                horizontalCombinations.add(BoardPosition(x, y))
        }
        if (horizontalCombinations.size == 3) {
            finalOut.add(horizontalCombinations)
            return@horizontal
        }
    }

    //vertical
    List(3) vertical@{ x ->
        val verticalCombinations = mutableListOf<BoardPosition>()
        List(3) { y ->
            if (symbol == this[y][x])
                verticalCombinations.add(BoardPosition(y, x))
        }
        if (verticalCombinations.size == 3) {
            finalOut.add(verticalCombinations)
            return@vertical
        }
    }

    //diagonals
    val diagonalLeft = mutableListOf<BoardPosition>()
    val diagonalRight = mutableListOf<BoardPosition>()
    List(3) { idx ->
        if (symbol == this[idx][idx])
            diagonalLeft.add(BoardPosition(idx, idx))
        if (symbol == this[idx][2 - idx])
            diagonalRight.add(BoardPosition(idx, 2 - idx))
    }
    if (diagonalRight.size == 3) {
        finalOut.add(diagonalRight)
    }
    if (diagonalLeft.size == 3) {
        finalOut.add(diagonalLeft)
    }

    return finalOut
}