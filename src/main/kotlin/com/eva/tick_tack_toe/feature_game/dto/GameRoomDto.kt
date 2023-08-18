package com.eva.tick_tack_toe.feature_game.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameRoomDto(
    @SerialName("board_count") val boardCount: Int,
    @SerialName("board_layout") val boardLayout: List<List<Char>>,
    @SerialName("is_anonymous") val isAnonymous: Boolean,
    @SerialName("room_id") val room: String
)
