package com.eva.tick_tack_toe.feature_game.mapper

import com.eva.tick_tack_toe.feature_game.data.dto.BoardPositionDto
import com.eva.tick_tack_toe.feature_game.models.BoardPosition

fun BoardPositionDto.toModel() = BoardPosition(x = x, y = y)

fun BoardPosition.toDto() = BoardPositionDto(x = x, y = y)