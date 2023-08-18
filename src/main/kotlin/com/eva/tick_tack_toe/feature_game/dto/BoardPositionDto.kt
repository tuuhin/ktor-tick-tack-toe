package com.eva.tick_tack_toe.feature_game.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardPositionDto(
    @SerialName("x") val x: Int,
    @SerialName("y") val y: Int,
)