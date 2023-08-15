package com.eva.tick_tack_toe.utils

import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap


/**
 * [RoomAndPlayerServer] class will help to interact with the joined players and available gameRooms
 */
class RoomAndPlayerServer {

    /**
     * A [HashMap] to hold the players, the keys are the roomId with the player data as values
     * [GamePlayerModel] holds the [WebSocketSession] which serves as the main way to pass information
     */
    val players = ConcurrentHashMap<String, GamePlayerModel>()

    /**
     * A [HashMap] to hold the Room and the Game Models.
     * When a player creates a room a roomId is generated that is treated as the key.
     * But there is also an option to play the game without creating the room.
     * In that case the server will spin up a random roomId, and [GameRoomModel.isAnonymous]
     * flags shows if this an anonymous room
     */
    val gameRooms = ConcurrentHashMap<String, GameRoomModel>()

    /**
     * On creating a new room via post request.
     * A new room will be prepared which latter can be used by the players
     * @param room : Unique Room Id
     * @param board : No of boards for the game
     */
    fun addToRoom(room: String, board: Int): GameRoomModel? {
        gameRooms[room] = GameRoomModel(room = room, boardCount = board)
        return gameRooms[room]
    }

    /**
     * Removes the Game room.
     * Should be only used when the game is over otherwise the game data will be lost
     * @param room : Room to be removed
     */
    fun deleteRoom(room: String) = gameRooms.remove(room)

}