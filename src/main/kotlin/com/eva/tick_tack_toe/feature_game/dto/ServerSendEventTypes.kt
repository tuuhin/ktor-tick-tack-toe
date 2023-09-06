package com.eva.tick_tack_toe.feature_game.dto

import kotlinx.serialization.Serializable

@Serializable
enum class ServerSendEventTypes(val type: String){
    MESSAGE_TYPE("MESSAGE"),
    GAME_STATE_TYPE("GAME");




}