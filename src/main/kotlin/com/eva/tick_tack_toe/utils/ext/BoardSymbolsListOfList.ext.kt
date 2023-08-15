package com.eva.tick_tack_toe.utils.ext

import com.eva.tick_tack_toe.feature_game.models.BoardPosition
import com.eva.tick_tack_toe.utils.BoardSymbols

fun List<List<BoardSymbols>>.toBoardPositions(symbol: BoardSymbols): List<BoardPosition> {
    val positions = mutableListOf<BoardPosition>()
    for (rowIndex in indices) {
        for (columnIndex in indices) {
            if (symbol == this[rowIndex][columnIndex])
                positions.add(BoardPosition(rowIndex, columnIndex))
        }
    }
    return positions
}