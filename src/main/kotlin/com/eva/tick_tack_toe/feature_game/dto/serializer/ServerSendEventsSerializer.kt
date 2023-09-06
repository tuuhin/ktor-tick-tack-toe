package com.eva.tick_tack_toe.feature_game.dto.serializer

import com.eva.tick_tack_toe.feature_game.dto.ServerSendEventTypes
import com.eva.tick_tack_toe.feature_game.dto.ServerSendEventsDto
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ServerSendEventsSerializer
    : JsonContentPolymorphicSerializer<ServerSendEventsDto>(ServerSendEventsDto::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ServerSendEventsDto> {
        return when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            ServerSendEventTypes.MESSAGE_TYPE.type -> ServerSendEventsDto.ServerMessage.serializer()
            ServerSendEventTypes.GAME_STATE_TYPE.type -> ServerSendEventsDto.ServerGameState.serializer()
            else -> throw Exception("Unknown Module: key 'type' not found or does not matches any module type")
        }
    }
}