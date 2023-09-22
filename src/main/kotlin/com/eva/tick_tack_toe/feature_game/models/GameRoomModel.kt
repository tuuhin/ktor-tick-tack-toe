package com.eva.tick_tack_toe.feature_game.models

import com.eva.tick_tack_toe.feature_game.game.BoardGame
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel
import com.eva.tick_tack_toe.utils.BoardSymbols
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.update


/**
 * Represents a Game Room
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

    var currentBoard = 0
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

    fun gameWinner(): GamePlayerModel = players.maxBy { it.points.winCount }

    /**
     * Increases the board count by 1
     */
    fun incrementBoardCount() = currentBoard++

    /**
     * Updates the player points according to the [winnerSymbol] and [isDraw] state
     */
    fun updatePlayerPoints() = players.forEach { player ->
        player.pointsFlow.update { points ->
            points.copy(
                winCount = points.winCount + if (player.symbol == winnerSymbol) 1 else 0,
                drawCount = points.drawCount + if (isDraw) 1 else 0,
                looseCount = points.winCount + if (player.symbol != winnerSymbol) 1 else 0,
            )
        }
    }

    /**
     * Adds a new player to the player list
     * @param player The instance of [GamePlayerModel] to be added
     * @return [Boolean] if the result is successful
     */
    fun addPlayersToTheRoom(player: GamePlayerModel) = _players.add(player)

    /**
     * Removes the player from the player list
     * @param player The instance of the player to be removed
     * @return [Boolean] if the result is successful
     */
    fun removePlayersFromTheRoom(player: GamePlayerModel) = _players.remove(player)


    /**
     * Clears the old board and create a new one
     */
    fun clearAndCreateNewRoom() = game.prepareNewBoard()

}