package com.eva.tick_tack_toe.feature_game.dto

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class GameRoomDto(

    @SerialName("board_count")
    val boardCount: Int,

    @SerialName("board_layout")
    val boardLayout: List<List<Char>>,

    @SerialName("room_id")
    val room: String,

    @EncodeDefault
    @SerialName("winning_symbol")
    val winningSymbols: Char? = null,

    @SerialName("is_draw")
    val isDraw: Boolean,

    @SerialName("is_ready")
    val isReady: Boolean
)
