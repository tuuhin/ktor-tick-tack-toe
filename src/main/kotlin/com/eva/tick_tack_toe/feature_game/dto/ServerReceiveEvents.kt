package com.eva.tick_tack_toe.feature_game.dto

import com.eva.tick_tack_toe.feature_game.dto.serializer.ServerReceiveEventsSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(ServerReceiveEventsSerializer::class)
@OptIn(ExperimentalSerializationApi::class)
sealed interface ServerReceiveEvents {

    @Serializable
    data class ReceiveGameData(
        @EncodeDefault @SerialName("type") val type: String = ServerReceiveEventTypes.GAME_STATE_TYPE.type,
        @SerialName("data") val data: GameReceiveDataDto
    ) : ServerReceiveEvents

}