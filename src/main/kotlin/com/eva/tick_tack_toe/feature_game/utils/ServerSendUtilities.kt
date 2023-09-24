package com.eva.tick_tack_toe.feature_game.utils

import com.eva.tick_tack_toe.feature_game.dto.GameAchievementDto
import com.eva.tick_tack_toe.feature_game.dto.GameSendDataDto
import com.eva.tick_tack_toe.feature_game.dto.ServerSendEventsDto
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel
import com.eva.tick_tack_toe.utils.BoardSymbols
import io.ktor.server.websocket.*

/**
 * Contains helpful utilities to send [ServerSendEventsDto] based events to the client
 */
class ServerSendUtilities {

    /**
     * A helper function to send an associated message to the end user
     * @param players  List of [GamePlayerModel] to which the message to be sent
     * @param self [GamePlayerModel] which sends the message to the other users
     * @param message  An associated [String] message to be sent by the player.
     */
    suspend fun sendAssociatedMessage(
        players: List<GamePlayerModel>,
        self: GamePlayerModel,
        message: String
    ) = players.forEach { player ->
        if (player != self)
            self.session.sendSerialized(
                ServerSendEventsDto.ServerMessage(message = message)
            )
    }


    /**
     * A utility function to send the achievement at the end of the game
     * @param players The [List] of [GamePlayerModel] of the game
     * @param message The text message to be sent
     * @param winnerSymbols The winner symbol to be informed
     * @param winnerName Optional winner name
     * @param associatedText Some Extra text data to be sent
     */
    suspend fun sendAchievement(
        players: List<GamePlayerModel>,
        message: String,
        winnerSymbols: BoardSymbols,
        associatedText: String? = null,
        winnerName: String? = null
    ) = players.forEach { player ->
        player.session.sendSerialized(
            ServerSendEventsDto.GameAchievementState(
                result = GameAchievementDto(
                    text = message,
                    secondaryText = associatedText,
                    winnerSymbols = winnerSymbols,
                    winnerName = winnerName
                )
            )
        )
    }


    /**
     * Sends the [GameSendDataDto] as to the players, this is the actual info to be sent in the game
     * @param self The [GamePlayerModel] to which data is to be sent
     * @param board [GameSendDataDto] ,which contains the game data to be sent to the end user
     * @param sendSelf Is broadcast being allowed
     */
    suspend fun sendBoardGameState(
        players: List<GamePlayerModel>,
        self: GamePlayerModel,
        board: GameSendDataDto,
        sendSelf: Boolean = false,
    ) {
        players.forEach { player ->
            if (sendSelf)
                player.session.sendSerialized(
                    ServerSendEventsDto.ServerGameState(state = board)
                )
            else if (player != self) {
                player.session.sendSerialized(
                    ServerSendEventsDto.ServerGameState(state = board)
                )
            }
        }
    }


    /**
     * Sends server messages to all the user in a game room
     * @param players List of [GamePlayerModel] to which the message to be sent
     * @param message The message that is to be sent
     */
    suspend fun sendServerMessage(
        players: List<GamePlayerModel>,
        message: String
    ) = players.forEach { player ->
        player.session.sendSerialized(
            ServerSendEventsDto.ServerMessage(message = message)
        )
    }

}