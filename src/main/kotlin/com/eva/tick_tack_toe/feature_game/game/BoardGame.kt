package com.eva.tick_tack_toe.feature_game.game

import com.eva.tick_tack_toe.utils.ext.toBoardPositions
import com.eva.tick_tack_toe.feature_game.models.BoardPosition
import com.eva.tick_tack_toe.feature_game.models.GameState
import com.eva.tick_tack_toe.utils.BoardSymbols
import com.eva.tick_tack_toe.utils.BoardWinningCombinations
import kotlinx.coroutines.flow.*

class BoardGame {

    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    private val winningCombination: List<BoardWinningCombinations> = listOf(
        BoardWinningCombinations.HorizontalCombinations,
        BoardWinningCombinations.VerticalCombinations,
        BoardWinningCombinations.DiagonalCombinations
    )

    fun updateBoardState(position: BoardPosition, playerSymbols: BoardSymbols) {
        val updatedBoard = _gameState.value.boardState
            .mapIndexed { rowIndex, rowList ->
                rowList.mapIndexed { colIndex, symbols ->
                    if (position.x == rowIndex && position.y == colIndex)
                        playerSymbols
                    else symbols
                }
            }

        val hasWinningCombination = winningCombination.any { wComb ->
            wComb.combinations.any { comb ->
                comb == updatedBoard.toBoardPositions(playerSymbols)
            }
        }

        val allFilled = updatedBoard.all { boardRow ->
            boardRow.all { it != BoardSymbols.Blank }
        }
        _gameState.update { state ->
            state.copy(
                isDraw = allFilled,
                winnerSymbol = if (hasWinningCombination) playerSymbols else null,
                boardState = updatedBoard
            )
        }
    }

    fun prepareNewBoard() =
        _gameState.updateAndGet {
            it.copy(
                boardState = GameState.emptyBoardState(),
                winnerSymbol = null,
                isDraw = false
            )
        }
}