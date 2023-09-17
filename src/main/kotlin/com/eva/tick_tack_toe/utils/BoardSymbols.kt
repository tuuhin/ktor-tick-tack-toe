package com.eva.tick_tack_toe.utils


/**
 * Symbolizes the Possible Symbols, In Tic Tac Toe only X, O and B where B denotes the unfilled or blank position
 */

enum class BoardSymbols(val symbol: Char) {
    XSymbol(symbol = 'X'),
    OSymbol(symbol = 'O'),
    Blank(symbol = 'B');

    companion object {
        fun fromSymbol(symbol: Char): BoardSymbols = when (symbol) {
            XSymbol.symbol -> XSymbol
            OSymbol.symbol -> OSymbol
            else -> Blank
        }
    }
}
