package com.eva.tick_tack_toe.feature_game.dto

import com.eva.tick_tack_toe.utils.BoardSymbols
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class GameAchievementDto(

    @EncodeDefault
    @SerialName("text")
    val text: String = "",

    @EncodeDefault
    @SerialName("secondary")
    val secondaryText: String? = null,

    @SerialName("winner_symbol")
    val winnerSymbols: BoardSymbols? = null,

    @SerialName("winner_name")
    val winnerName: String? = null,

    @SerialName("is_draw")
    val isDraw: Boolean = false
)
