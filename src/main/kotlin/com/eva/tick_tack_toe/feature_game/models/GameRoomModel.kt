package com.eva.tick_tack_toe.feature_game.models

import com.eva.tick_tack_toe.feature_game.game.BoardGame
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel
import com.eva.tick_tack_toe.utils.BoardSymbols
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


/**
 * Represents a Game Room
 * @property room RoomId of the Game room created or used
 * @property players A [List] of [GamePlayerModel] which have joined the game room or will be playing the game
 * @property game The Actual board game
 * @property boardCount How many times will the game be played between the users until its off.
 * @property isAnonymous denotes if the room created anonymously or not
 * @property isReady Marks the room to be ready to play
 */
data class GameRoomModel(
    val room: String,
    val players: List<GamePlayerModel> = emptyList(),
    val game: BoardGame = BoardGame(),
    val boardCount: Int = 1,
    val isAnonymous: Boolean,
) {
    val isReady: Boolean
        get() = players.size >= 2

    private val winnerSymbol: BoardSymbols?
        get() = game.board.winnerSymbol

    private val isDraw: Boolean
        get() = game.board.isDraw

    /**
     * Updates the player points according to the [winnerSymbol] and [isDraw] state
     */
    fun updatePlayerPoints() = players.forEach { player ->
        player.pointsFlow.update { gamePoints ->
            gamePoints.copy(
                winCount = gamePoints.winCount + if (player.symbol == game.board.winnerSymbol) 1 else 0,
                drawCount = gamePoints.drawCount + if (game.board.isDraw) 1 else 0,
                looseCount = gamePoints.winCount + if (player.symbol != game.board.winnerSymbol) 1 else 0,
            )
        }
    }
}