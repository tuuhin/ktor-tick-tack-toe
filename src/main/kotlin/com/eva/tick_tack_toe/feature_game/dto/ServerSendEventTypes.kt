package com.eva.tick_tack_toe.feature_game.dto

enum class ServerSendEventTypes(val type: String) {

    MESSAGE_TYPE("MESSAGE"),

    GAME_STATE_TYPE("GAME"),

    ACHIEVEMENT_TYPE("ACHIEVEMENT");
}