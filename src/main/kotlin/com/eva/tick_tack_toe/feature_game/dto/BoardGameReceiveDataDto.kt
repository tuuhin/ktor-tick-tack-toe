package com.eva.tick_tack_toe.feature_game.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardGameReceiveDataDto(
    @SerialName("player_symbol") val symbol: Char,
    @SerialName("position") val boardPosition: BoardPositionDto,
    @SerialName("client_id") val clientId: String
)
