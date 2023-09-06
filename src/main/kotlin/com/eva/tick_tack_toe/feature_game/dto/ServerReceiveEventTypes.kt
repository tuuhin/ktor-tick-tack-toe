package com.eva.tick_tack_toe.feature_game.dto

import kotlinx.serialization.Serializable

@Serializable
enum class ServerReceiveEventTypes(val type: String) {
    GAME_STATE_TYPE(type = "GAME")
}