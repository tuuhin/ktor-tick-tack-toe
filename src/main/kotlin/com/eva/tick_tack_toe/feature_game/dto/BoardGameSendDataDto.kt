package com.eva.tick_tack_toe.feature_game.dto

import com.eva.tick_tack_toe.feature_room.dto.GamePlayerDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BoardGameSendDataDto(
    @SerialName("player") val player: GamePlayerDto,
    @SerialName("board") val board: GameRoomDto,
)