package com.eva.tick_tack_toe.feature_room.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GamePlayerDto(
    @SerialName("user_name") val userName: String,
    @SerialName("win_count") val winCount: Int,
    @SerialName("draw_count") val drawCount: Int,
    @SerialName("lost_count") val lostCount: Int,
    @SerialName("symbol") val playerSymbol: Char,
    @SerialName("client_id") val clientId: String
)