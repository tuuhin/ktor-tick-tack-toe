package com.eva.tick_tack_toe.feature_game.mapper

import com.eva.tick_tack_toe.feature_game.dto.GameRoomDto
import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import kotlinx.coroutines.flow.*

fun GameRoomModel.toDto() = GameRoomDto(
    boardLayout = board.gameState.value.boardState.map { symbolRow -> symbolRow.map { symbol -> symbol.symbol } },
    boardCount = boardCount,
    isAnonymous = isAnonymous,
    room = room
)

fun GameRoomModel.toDtoAsFlow(): Flow<GameRoomDto> {
    return board.gameState
        .map { state ->
            GameRoomDto(
                boardLayout = state.boardState.map { symbolRow -> symbolRow.map { symbol -> symbol.symbol } },
                boardCount = boardCount,
                isAnonymous = isAnonymous,
                room = room
            )
        }
}