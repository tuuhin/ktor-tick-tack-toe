package com.eva.tick_tack_toe.feature_game.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardPositionDto(
    @SerialName("x_point") val x: Int,
    @SerialName("y_point") val y: Int,
)