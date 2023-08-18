package com.eva.tick_tack_toe.utils

enum class BoardSymbols(val symbol: Char) {
    XSymbol('X'),
    OSymbol(symbol = 'O'),
    Blank(symbol = 'B');

    companion object {
        fun fromSymbol(symbol: Char): BoardSymbols = when (symbol) {
            'X' -> XSymbol
            'Y' -> OSymbol
            else -> Blank
        }
    }
}
