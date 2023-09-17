package com.eva.tick_tack_toe.feature_game.models

data class GamePoints(
    val winCount: Int = 0,
    val drawCount: Int = 0,
    val looseCount: Int = 0,
)
