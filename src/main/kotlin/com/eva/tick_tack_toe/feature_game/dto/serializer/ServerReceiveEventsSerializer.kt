package com.eva.tick_tack_toe.feature_game.dto.serializer

import com.eva.tick_tack_toe.feature_game.dto.ServerReceiveEventTypes
import com.eva.tick_tack_toe.feature_game.dto.ServerReceiveEvents
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ServerReceiveEventsSerializer :
    JsonContentPolymorphicSerializer<ServerReceiveEvents>(ServerReceiveEvents::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ServerReceiveEvents> {
        return when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            ServerReceiveEventTypes.GAME_STATE_TYPE.type -> ServerReceiveEvents.ReceiveGameData.serializer()
            else -> throw Exception("Unknown key 'type' not found")
        }
    }
}