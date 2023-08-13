package com.eva.tick_tack_toe.utils

sealed class BoardSymbols(val symbol: Char = ' ') {
    data object XSymbol : BoardSymbols(symbol = 'X')
    data object OSymbol : BoardSymbols(symbol = 'O')
    data object Blank : BoardSymbols()
}
