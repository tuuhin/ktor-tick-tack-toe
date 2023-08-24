package com.eva.tick_tack_toe.feature_game.dto

import com.eva.tick_tack_toe.feature_room.dto.GamePlayerDto
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class BoardGameSendDataDto(

    @EncodeDefault
    @SerialName("x_player")
    val playerX: GamePlayerDto? = null,

    @EncodeDefault
    @SerialName("o_player")
    val playerO: GamePlayerDto? = null,

    @SerialName("room")
    val board: GameRoomDto,

    @SerialName("is_ready")
    val isAllPlayerJoined: Boolean
)