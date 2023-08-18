package com.eva.tick_tack_toe.feature_room.mappers

import com.eva.tick_tack_toe.feature_room.dto.GamePlayerDto
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel

fun GamePlayerModel.toDto() = GamePlayerDto(
    userName = userName,
    winCount = winCount,
    drawCount = drawCount,
    lostCount = looseCount,
    clientId = clientId,
    playerSymbol = symbol.symbol
)