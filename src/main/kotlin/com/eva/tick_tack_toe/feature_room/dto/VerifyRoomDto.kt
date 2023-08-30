package com.eva.tick_tack_toe.feature_room.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyRoomDto(
    @SerialName("message") val message: String,
    @SerialName("room") val roomSerializer: RoomSerializer
)
