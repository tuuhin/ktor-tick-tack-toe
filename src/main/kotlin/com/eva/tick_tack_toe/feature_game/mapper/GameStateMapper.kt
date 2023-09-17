package com.eva.tick_tack_toe.feature_game.mapper

import com.eva.tick_tack_toe.feature_game.dto.GameRoomDto
import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import kotlinx.coroutines.flow.*

fun GameRoomModel.toDto() = GameRoomDto(
    boardLayout = game.board.boardState
        .map { symbolRow -> symbolRow.map { symbol -> symbol.symbol } },
    boardCount = boardCount,
    room = room,
    winningSymbols = game.board.winnerSymbol?.symbol,
    isDraw = game.board.isDraw,
    isReady = isReady
)

fun GameRoomModel.toDtoAsFlow(): Flow<GameRoomDto> = game.gameState
    .map { state ->
        GameRoomDto(
            boardLayout = state.boardState
                .map { symbolRow -> symbolRow.map { symbol -> symbol.symbol } },
            boardCount = boardCount,
            room = room,
            winningSymbols = state.winnerSymbol?.symbol,
            isDraw = state.isDraw,
            isReady = isReady
        )
    }
