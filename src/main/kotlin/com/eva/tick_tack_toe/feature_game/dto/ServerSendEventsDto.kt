package com.eva.tick_tack_toe.feature_game.dto

import com.eva.tick_tack_toe.feature_game.dto.serializer.ServerSendEventsSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(ServerSendEventsSerializer::class)
@OptIn(ExperimentalSerializationApi::class)
sealed interface ServerSendEventsDto {
    @Serializable
    data class ServerMessage(

        @EncodeDefault
        @SerialName("type")
        val type: String = ServerSendEventTypes.MESSAGE_TYPE.type,

        @SerialName("result") val message: String

    ) : ServerSendEventsDto

    @Serializable
    data class ServerGameState(

        @EncodeDefault
        @SerialName("type")
        val type: String = ServerSendEventTypes.GAME_STATE_TYPE.type,

        @SerialName("result")
        val state: GameSendDataDto

    ) : ServerSendEventsDto

    @Serializable
    data class GameAchievementState(

        @EncodeDefault
        @SerialName("type")
        val type: String = ServerSendEventTypes.ACHIEVEMENT_TYPE.type,

        @SerialName("result")
        val result: GameAchievementDto

    ) : ServerSendEventsDto
}
