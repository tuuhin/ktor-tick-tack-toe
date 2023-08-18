package com.eva.tick_tack_toe.feature_game.dto

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
sealed interface ServerSendEventsDto {
    @Serializable
    data class ServerMessage(
        @EncodeDefault @SerialName("type") val type: String = ServerSendEventTypes.MESSAGE_TYPE.type,
        @SerialName("result") val message: String
    ) : ServerSendEventsDto

    @Serializable
    data class ServerGameState(
        @EncodeDefault @SerialName("type") val type: String = ServerSendEventTypes.GAME_STATE_TYPE.type,
        @SerialName("result") val state: BoardGameSendDataDto
    ) : ServerSendEventsDto
}