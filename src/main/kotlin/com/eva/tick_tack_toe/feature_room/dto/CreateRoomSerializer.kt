package com.eva.tick_tack_toe.feature_room.dto

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class CreateRoomSerializer(
    @EncodeDefault @SerialName("no_of_rounds") val rounds: Int = 1
)
