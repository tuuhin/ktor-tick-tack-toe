package com.eva.tick_tack_toe.feature_game.game

import com.eva.tick_tack_toe.feature_game.models.BoardPosition
import com.eva.tick_tack_toe.feature_game.models.GameState
import com.eva.tick_tack_toe.utils.BoardSymbols
import com.eva.tick_tack_toe.utils.BoardWinningCombinations
import com.eva.tick_tack_toe.utils.ext.toBoardPositions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet


/**
 * Represents the Actual Game
 */
class BoardGame {

    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    val board: GameState
        get() = gameState.value

    val canUpdateBoard: Boolean
        get() = gameState.value.winnerSymbol != null || !gameState.value.isDraw

    /**
     * Updates the board state according to the [BoardPosition] and [BoardSymbols] received
     */
    fun updateBoardState(position: BoardPosition, playerSymbols: BoardSymbols) {
        val updatedBoard = _gameState.value.boardState
            .mapIndexed { rowIndex, rowList ->
                rowList.mapIndexed { colIndex, symbols ->
                    if (position.x == rowIndex && position.y == colIndex)
                        playerSymbols
                    else symbols
                }
            }

        val hasWinningCombination = BoardWinningCombinations.WINNING_COMBINATIONS
            .any { wComb ->
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


    /**
     * Prepares a new board when there is a winner or there is a draw
     */
    fun prepareNewBoard() = _gameState.updateAndGet {
        it.copy(
            boardState = GameState.emptyBoardState(),
            winnerSymbol = null,
            isDraw = false
        )
    }
}