package com.eva.tick_tack_toe.feature_game.models

import com.eva.tick_tack_toe.Logger
import com.eva.tick_tack_toe.feature_game.game.BoardGame
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel
import com.eva.tick_tack_toe.utils.BoardSymbols
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


/**
 * Represents a Game Room.
 * @property room RoomId of the Game room created or used
 * @property players A [List] of [GamePlayerModel] which have joined the game room or will be playing the game
 * @property game The Actual board game
 * @property boardCount How many times will the game be played between the users until its off.
 * @property isAnonymous denotes if the room created anonymously or not
 * @property isReady Marks the room to be ready to play
 * @property winnerSymbol Marks the round as complete and shows the current winner symbol
 * @property isDraw Marks the round as complete and shows if the board is drawn
 * @property isNextRoundAvailable Checks and evaluate if the server needs to update the board
 */
data class GameRoomModel(
    val room: String,
    val game: BoardGame = BoardGame(),
    val boardCount: Int = 1,
    val isAnonymous: Boolean = true,
) {
    private val _players: MutableList<GamePlayerModel> = mutableListOf()

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val delay: Duration = 2.seconds

    var currentBoard = 1
        private set

    val players: List<GamePlayerModel>
        get() = _players.toImmutableList()

    val isReady: Boolean
        get() = _players.size == 2

    val winnerSymbol: BoardSymbols?
        get() = game.board.winnerSymbol

    val isDraw: Boolean
        get() = game.board.isDraw

    val isNextRoundAvailable: Boolean
        get() = currentBoard < boardCount


    val hasGameStarted: Boolean
        get() = game.board.face.any { row -> row.any { it != BoardSymbols.Blank } }

    /**
     * Gets the game winner by player with most win points
     * @return [GamePlayerModel] with the max win points in case all players have same points returns null
     */
    val checkAndGetGameWinner: GamePlayerModel?
        get() {
            val winner = players.maxBy { it.points.winCount }
            val isAllPlayersHaveSameScore = players.all { it.points.winCount == winner.points.winCount }

            if (isAllPlayersHaveSameScore) return null
            return winner
        }

    /**
     * Increases the board count by 1
     */
    private fun incrementBoardCount() = synchronized(this) { currentBoard++ }


    init {
        // A central flow listener which listens for changes and fire updatePoints
        // and increases the board count and clears the board to create a new room
        game.gameState
            .onStart { Logger.info("STARTING A LISTENER FOR GAME STATE FOR GAME ROOM $room") }
            .catch { err -> Logger.error("ERROR OCCURRED ${err.localizedMessage}") }
            .onEach { state ->
                if (state.winnerSymbol != null || state.isDraw) {
                    updatePlayersPoints()
                    if (isNextRoundAvailable) {
                        delay(delay)
                        incrementBoardCount()
                        clearAndCreateNewRoom()
                    }
                }
            }.launchIn(coroutineScope)
    }

    /**
     * Updates the player points according to the [winnerSymbol] and [isDraw] state
     */
    private fun updatePlayersPoints() = players.forEach { player ->
        player.pointsFlow.update { points ->
            if (isDraw)
                points.copy(drawCount = points.drawCount + 1)
            else
                points.copy(
                    winCount = points.winCount + if (player.symbol == winnerSymbol) 1 else 0,
                    looseCount = points.looseCount + if (player.symbol != winnerSymbol) 1 else 0,
                )
        }
    }

    /**
     * Adds a new player to the player list
     * @param player The instance of [GamePlayerModel] to be added
     */
    fun addPlayersToTheRoom(player: GamePlayerModel) {
        if (_players.size < 2)
            _players.add(player)
    }

    /**
     * Removes the player from the player list
     * @param player The instance of the player to be removed
     */
    fun removePlayersFromTheRoom(player: GamePlayerModel) = _players.remove(player)

    /**
     * Clears the old board and create a new one
     */
    private fun clearAndCreateNewRoom() = game.prepareNewBoard()

    /**
     * Clears the scope for the game
     */
    fun cancelScope() = coroutineScope.cancel()

}