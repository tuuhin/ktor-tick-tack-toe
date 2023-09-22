package com.eva.tick_tack_toe.feature_game.mapper

import com.eva.tick_tack_toe.feature_game.dto.GameRoomDto
import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import kotlinx.coroutines.flow.*

fun GameRoomModel.toDto() = GameRoomDto(
    boardLayout = game.board.face
        .map { symbolRow -> symbolRow.map { symbol -> symbol.symbol } },
    boardCount = boardCount,
    room = room,
    winningSymbols = winnerSymbol?.symbol,
    isDraw = isDraw,
    isReady = isReady,
    currentBoard = currentBoard
)

fun GameRoomModel.toDtoAsFlow(): Flow<GameRoomDto> = game.gameState
    .map { state ->
        GameRoomDto(
            boardLayout = state.face
                .map { symbolRow -> symbolRow.map { symbol -> symbol.symbol } },
            boardCount = boardCount,
            room = room,
            winningSymbols = winnerSymbol?.symbol,
            isDraw = isDraw,
            isReady = isReady,
            currentBoard = currentBoard
        )
    }
