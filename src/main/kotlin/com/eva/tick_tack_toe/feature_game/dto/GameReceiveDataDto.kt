package com.eva.tick_tack_toe.feature_game.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameReceiveDataDto(
    @SerialName("client_id") val clientId: String,
    @SerialName("position") val boardPosition: BoardPositionDto,
)
