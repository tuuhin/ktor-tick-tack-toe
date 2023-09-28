package com.eva.tick_tack_toe.feature_game.game

import app.cash.turbine.turbineScope
import com.eva.tick_tack_toe.feature_game.models.BoardPosition
import com.eva.tick_tack_toe.feature_game.models.GameState
import com.eva.tick_tack_toe.utils.BoardSymbols
import com.eva.tick_tack_toe.feature_game.utils.TestBoardCombinations
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class BoardGameWorkingTest {
    private lateinit var boardGame: BoardGame

    @BeforeTest
    fun `Setting up the game`() {
        boardGame = BoardGame()
    }

    @AfterTest
    fun `clean the board`() {
        boardGame.prepareNewBoard()
    }

    @Test
    fun `check the initial configuration of the board`() = runTest {

        assertTrue(message = "THe board is filled with B i.e, its a blank board") {
            boardGame.board.face == GameState.emptyBoardState()
        }

        assertTrue(message = "Can update the board ") {
            boardGame.canUpdateBoard
        }

        assertTrue(message = "X is the winner of the current configuration") {
            boardGame.board.winnerSymbol == null
        }

        assertFalse(message = "Its not in a  a draw configuration") {
            boardGame.board.isDraw
        }
    }

    @Test
    fun `check the flow of changes for the board with diagonal fill with X`() = runTest {

        repeat(3) { dig ->
            boardGame.updateBoardState(BoardPosition(dig, dig), BoardSymbols.XSymbol)
        }

        assertTrue(message = "Filled diagonal with x checking the board state") {
            boardGame.gameState.value.face == TestBoardCombinations.DiagonalFilledWithSameSymbol(BoardSymbols.XSymbol).combinations
        }

        assertTrue(message = "X is the winner of the current configuration") {
            boardGame.board.winnerSymbol == BoardSymbols.XSymbol
        }

        assertFalse(message = "Its not in a  a draw configuration") {
            boardGame.board.isDraw
        }
    }


    @Test
    fun `check the flow of changes for the board left row fill with X and tested the game state flow`() = runTest {

        turbineScope {
            val state = boardGame.gameState.testIn(this)

            assertTrue("Its a empty board") {
                state.awaitItem().face == GameState.emptyBoardState()
            }

            boardGame.updateBoardState(BoardPosition(0, 0), BoardSymbols.XSymbol)
            assertTrue("put x on [0][0] co-ordinate and top row is xbb") {
                state.awaitItem().face == List(3) { x ->
                    List(3) { y -> if (x == 0 && y in arrayOf(0)) BoardSymbols.XSymbol else BoardSymbols.Blank }
                }
            }
            boardGame.updateBoardState(BoardPosition(0, 1), BoardSymbols.XSymbol)
            assertTrue("put x on [0][1] co-ordinate and top row is xxb") {
                state.awaitItem().face == List(3) { x ->
                    List(3) { y ->
                        if (x == 0 && y in arrayOf(0, 1)) BoardSymbols.XSymbol else BoardSymbols.Blank
                    }
                }
            }
            boardGame.updateBoardState(BoardPosition(0, 2), BoardSymbols.XSymbol)
            assertTrue("put x on [0][2] co-ordinate and top row is xxx") {
                state.awaitItem().face == List(3) { x ->
                    List(3) { y ->
                        if (x == 0 && y in arrayOf(0, 1, 2)) BoardSymbols.XSymbol else BoardSymbols.Blank
                    }
                }
            }
            state.cancelAndIgnoreRemainingEvents()
        }

        assertTrue(message = "X is the winner of the current configuration") {
            boardGame.board.winnerSymbol == BoardSymbols.XSymbol
        }

        assertFalse(message = "Its not in a  a draw configuration") {
            boardGame.board.isDraw
        }
    }

    @Test
    fun `checking if the board is cleared or not via preparing a new board`() = runTest {

        assertTrue(message = "THe board is filled with B i.e, its a blank board") {
            boardGame.board.face == GameState.emptyBoardState()
        }

    }

    @Test
    fun `check a random combinations of X and O's`() = runTest {

        repeat(3) { col ->

            boardGame.updateBoardState(
                BoardPosition(0, col),
                if (col == 1) BoardSymbols.OSymbol else BoardSymbols.XSymbol
            )
        }

        assertTrue(message = "Is the board is filled in filled in xox pattern in the top row") {
            boardGame.gameState.value.face == TestBoardCombinations.LeftFilledXOXCombination.combinations
        }

        assertTrue(message = "There is no winner for the current configuration") {
            boardGame.board.winnerSymbol == null
        }

        assertFalse(message = "Its not in a  a draw configuration") {
            boardGame.board.isDraw
        }

    }

    @Test
    fun `check a filled board combinations if it results in a draw or not`() = runTest {

        /**
         * Filled pattern
         * | x | o | x |
         * | o | o | x |
         * | x | x | o |
         */
        repeat(3) { row ->
            repeat(3) { col ->
                if (row + col % 2 == 0) boardGame.updateBoardState(BoardPosition(row, col), BoardSymbols.XSymbol)
                else boardGame.updateBoardState(BoardPosition(row, col), BoardSymbols.OSymbol)
            }
        }

        assertFalse(message = "Cannot update the board anymore as its in draw config") {
            boardGame.canUpdateBoard
        }

        assertTrue(message = "There is no winner for the current configuration") {
            boardGame.board.winnerSymbol == null
        }

        assertTrue(message = "Its in a draw configuration") {
            boardGame.board.isDraw
        }
    }
}