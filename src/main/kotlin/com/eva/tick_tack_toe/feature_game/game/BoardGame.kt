package com.eva.tick_tack_toe.feature_game.game

import com.eva.tick_tack_toe.feature_game.models.BoardPosition
import com.eva.tick_tack_toe.feature_game.models.GameState
import com.eva.tick_tack_toe.utils.BoardSymbols
import com.eva.tick_tack_toe.utils.BoardWinningCombinations
import com.eva.tick_tack_toe.utils.ext.getPositionForSymbol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.withContext


/**
 * Represents the Actual Game
 * @property board The [GameState] for the current Board
 * @property gameState The flow version of [GameState] for the current board
 * @property canUpdateBoard Can the board be updated i.e., no winner or not in draw configuration
 */
class BoardGame {

    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    val board: GameState
        get() = gameState.value

    val canUpdateBoard: Boolean
        get() = board.winnerSymbol == null && !board.isDraw

    /**
     * Updates the board state according to the [BoardPosition] and [BoardSymbols] received
     * @param position [BoardPosition] to be set by the player
     * @param playerSymbols [BoardSymbols] for the player
     */
    suspend fun updateBoardState(
        position: BoardPosition,
        playerSymbols: BoardSymbols
    ) = withContext(Dispatchers.Default) {
        val updatedBoard = _gameState.value.face.mapIndexed { rowIndex, rowList ->
            rowList.mapIndexed { colIndex, symbols ->
                if (position.x == rowIndex && position.y == colIndex)
                    playerSymbols
                else symbols
            }
        }

        // As its checks for all the combinations, it's better to check it on a different coroutine
        val foundCombinations = async(Dispatchers.Default) {
            updatedBoard.getPositionForSymbol(symbol = playerSymbols)
        }.await()

        val successfulCombinations = BoardWinningCombinations.WINNING_COMBINATIONS.map { it.combinations }

        val hasWinningCombination = successfulCombinations.any { winningCombinations ->
            winningCombinations.any { comb -> foundCombinations.any { found -> comb == found } }
        }


        val allFilled = updatedBoard.all { boardRow -> boardRow.all { it != BoardSymbols.Blank } }
        _gameState.update { state ->
            state.copy(
                isDraw = allFilled,
                winnerSymbol = if (hasWinningCombination) playerSymbols else null,
                face = updatedBoard
            )
        }
    }


    /**
     * Prepares a new board when there is a winner or there is a draw
     */
    fun prepareNewBoard() = _gameState.updateAndGet {
        it.copy(
            face = GameState.emptyBoardState(),
            winnerSymbol = null,
            isDraw = false
        )
    }
}