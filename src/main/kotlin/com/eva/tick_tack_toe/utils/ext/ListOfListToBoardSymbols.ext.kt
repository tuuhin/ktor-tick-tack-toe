package com.eva.tick_tack_toe.utils.ext

import com.eva.tick_tack_toe.utils.BoardSymbols

/**
 * Convert the 2D array of [Char] to 2D Array of [BoardSymbols] representing the current Board Pattern
 */
fun List<List<Char>>.toBoardLayout() = List(3) { row ->
    List(3) { col ->
        BoardSymbols.fromSymbol(this[row][col])
    }
}