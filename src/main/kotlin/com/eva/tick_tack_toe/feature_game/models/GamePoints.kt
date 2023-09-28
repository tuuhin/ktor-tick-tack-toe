package com.eva.tick_tack_toe.feature_game.models

/**
 * Shows the points for a user.
 * The property name depicts what they represent
 */
data class GamePoints(
    val winCount: Int = 0,
    val drawCount: Int = 0,
    val looseCount: Int = 0,
)
