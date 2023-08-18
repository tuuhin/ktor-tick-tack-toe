package com.eva.tick_tack_toe.feature_game.models

import com.eva.tick_tack_toe.feature_game.game.BoardGame
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel

data class GameRoomModel(
    val room: String,
    val players: List<GamePlayerModel> = emptyList(),
    val board: BoardGame = BoardGame(),
    val boardCount: Int = 1,
    val isAnonymous: Boolean = false,
    val isGameComplete: Boolean = false
)
