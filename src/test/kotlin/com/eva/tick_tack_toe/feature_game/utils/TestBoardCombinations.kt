package com.eva.tick_tack_toe.feature_game.utils

import com.eva.tick_tack_toe.utils.BoardSymbols

sealed class TestBoardCombinations(val combinations: List<List<BoardSymbols>>) {

    data object UnfilledCombinations : TestBoardCombinations(
        combinations = List(3) { _ ->
            List(3) { _ -> BoardSymbols.Blank }
        }
    )

    data object DiagonalFilledFromTopWithXXXCombination : TestBoardCombinations(
        combinations = List(3) { row ->
            List(3) { col ->
                if (row == col) BoardSymbols.XSymbol
                else BoardSymbols.Blank
            }
        }
    )

    data object LeftFilledXXXCombination : TestBoardCombinations(
        combinations = List(3) { row ->
            List(3) { _ ->
                if (row == 0) BoardSymbols.XSymbol
                else BoardSymbols.Blank
            }
        }
    )

    data object LeftFilledXOXCombination : TestBoardCombinations(
        combinations = List(3) { row ->
            List(3) { col ->
                if (row == 0)
                    if (col == 1) BoardSymbols.OSymbol
                    else BoardSymbols.XSymbol
                else BoardSymbols.Blank
            }
        }
    )
}