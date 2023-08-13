package com.eva.tick_tack_toe.feature_game.game

import com.eva.tick_tack_toe.feature_game.models.BoardPosition
import com.eva.tick_tack_toe.feature_game.models.GameState
import com.eva.tick_tack_toe.utils.BoardSymbols
import com.eva.tick_tack_toe.feature_game.utils.TestBoardCombinations
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class BoardGameWorkingTest {
    private lateinit var boardGame: BoardGame

    @Before
    fun `Setting up the game`() {
        boardGame = BoardGame()
    }

    @Test
    fun `check the initial state of the board`() = runTest {
        val boardState = boardGame.gameState.value.boardState
        val winner = boardGame.gameState.value.winnerSymbol
        val isDraw = boardGame.gameState.value.isDraw
        assertEquals(GameState.emptyBoardState(), boardState)
        assertEquals(null, winner)
        assertEquals(false, isDraw)
    }

    @Test
    fun `check the flow of changes for the board with diagonal fill with X`() = runTest {
        boardGame.updateBoardState(BoardPosition(0, 0), BoardSymbols.XSymbol)
        boardGame.updateBoardState(BoardPosition(1, 1), BoardSymbols.XSymbol)
        boardGame.updateBoardState(BoardPosition(2, 2), BoardSymbols.XSymbol)
        assertEquals(
            boardGame.gameState.value.boardState,
            TestBoardCombinations.DiagonalFilledFromTopWithXXXCombination.combinations
        )
    }

    @Test
    fun `check if the board is clear or not`() = runTest {
        boardGame.prepareNewBoard()
        assertEquals(GameState.emptyBoardState(), boardGame.gameState.value.boardState)
    }

    @Test
    fun `check the flow of changes for the board left row fill with O`() = runTest {
        boardGame.updateBoardState(BoardPosition(0, 0), BoardSymbols.XSymbol)
        boardGame.updateBoardState(BoardPosition(0, 1), BoardSymbols.XSymbol)
        boardGame.updateBoardState(BoardPosition(0, 2), BoardSymbols.XSymbol)
        assertEquals(
            boardGame.gameState.value.boardState,
            TestBoardCombinations.LeftFilledXXXCombination.combinations
        )
        assertEquals(boardGame.gameState.value.winnerSymbol, BoardSymbols.XSymbol)
        assertEquals(boardGame.gameState.value.isDraw, actual = false)
    }


    @Test
    fun `checking if the board is cleared or not`() = runTest {
        boardGame.prepareNewBoard()
        assertEquals(
            boardGame.gameState.value.boardState,
            TestBoardCombinations.UnfilledCombinations.combinations
        )
    }

    @Test
    fun `check a random combinations of X and O's`() = runTest {

        boardGame.updateBoardState(BoardPosition(0, 0), BoardSymbols.XSymbol)
        boardGame.updateBoardState(BoardPosition(0, 1), BoardSymbols.OSymbol)
        boardGame.updateBoardState(BoardPosition(0, 2), BoardSymbols.XSymbol)
        assertEquals(
            boardGame.gameState.value.boardState,
            TestBoardCombinations.LeftFilledXOXCombination.combinations
        )
        assertEquals(boardGame.gameState.value.winnerSymbol, null)
        assertEquals(boardGame.gameState.value.isDraw, false)
    }

    @Test
    fun `check a filled board combinations if it results in a draw or not`() = runTest {

        /**
         * Filled pattern
         * | x | o | x |
         * | o | o | x |
         * | x | x | o |
         */
        boardGame.updateBoardState(BoardPosition(0, 0), BoardSymbols.XSymbol)
        boardGame.updateBoardState(BoardPosition(0, 1), BoardSymbols.OSymbol)
        boardGame.updateBoardState(BoardPosition(0, 2), BoardSymbols.XSymbol)
        boardGame.updateBoardState(BoardPosition(1, 0), BoardSymbols.OSymbol)
        boardGame.updateBoardState(BoardPosition(1, 1), BoardSymbols.OSymbol)
        boardGame.updateBoardState(BoardPosition(1, 2), BoardSymbols.XSymbol)
        boardGame.updateBoardState(BoardPosition(2, 0), BoardSymbols.XSymbol)
        boardGame.updateBoardState(BoardPosition(2, 1), BoardSymbols.OSymbol)
        boardGame.updateBoardState(BoardPosition(2, 2), BoardSymbols.XSymbol)

        assertEquals(boardGame.gameState.value.winnerSymbol, null)
        assertEquals(boardGame.gameState.value.isDraw, true)

    }
}