package com.eva.tick_tack_toe.utils

import com.eva.tick_tack_toe.feature_game.exceptions.PlayerRoomNotFoundException
import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel
import io.ktor.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * [RoomAndPlayerServer] class will help to interact with the joined players and available gameRooms
 */
class RoomAndPlayerServer {

    /**
     * A [ConcurrentHashMap] to hold the Room and the Game Models.
     * When a player creates a room a roomId is generated that is treated as the key.
     * But there is also an option to play the game without creating the room.
     * In that case the server will spin up a random roomId, and [GameRoomModel.isAnonymous]
     * flags shows if this an anonymous room
     */
    val gameRooms = ConcurrentHashMap<String, GameRoomModel>()

    /**
     * On creating a new room via post request.
     * A new room will be prepared which latter can be used by the players
     * @param room  Unique Room Id
     * @param board  No of boards for the game
     * @param isAnonymous  Is the room is anonymous
     * @return The newly created [GamePlayerModel] instance
     */
    fun createGameRoom(room: String, board: Int = 1, isAnonymous: Boolean = false): GameRoomModel {
        val model = GameRoomModel(room = room, boardCount = board, isAnonymous = isAnonymous)
        gameRooms[room] = model
        return gameRooms.getOrDefault(room, defaultValue = model)
    }

    /**
     * Adds a player to A anonymous room,if there is no room its creates a new room and then adds the new user
     * @param player A [GamePlayerModel] representing the new player
     */
    fun addAnonymousPlayerToRoom(player: GamePlayerModel) {
        val isAnonymousRoomAvailable = gameRooms.values.find { it.isAnonymous && it.players.size < 2 }
        isAnonymousRoomAvailable?.let { model ->
            val xPlayerExits = model.players.any { it.symbol == BoardSymbols.XSymbol }

            val playerWithSymbol = player.copy(
                symbol = if (xPlayerExits) BoardSymbols.OSymbol else BoardSymbols.XSymbol
            )

            val players = when (model.players.size) {
                0, 1 -> model.players + playerWithSymbol
                else -> model.players
            }
            gameRooms[model.room] = model.copy(players = players)
        } ?: run {
            // Helps to generate a unique string
            val randomNewRoomId = generateNonce()
            createGameRoom(room = randomNewRoomId, isAnonymous = true).also { model ->
                gameRooms[model.room] = model.copy(players = listOf(player))
            }
        }
    }

    /**
     * Adds the new  player to a given room,also pick a proper symbol according to available choices
     * @param room RoomId
     * @param player A [GamePlayerModel] instance representing the new player
     */
    fun addPlayersToRoom(room: String, player: GamePlayerModel) {
        gameRooms[room]?.let { model ->
            val xPlayerExits = model.players.any { it.symbol == BoardSymbols.XSymbol }

            val playerWithSymbol = player.copy(
                symbol = if (xPlayerExits) BoardSymbols.OSymbol else BoardSymbols.XSymbol
            )

            val players = when (model.players.size) {
                0, 1 -> model.players + playerWithSymbol
                else -> model.players
            }
            gameRooms[room] = model.copy(players = players)
        } ?: println("Provided room not found")
    }

    fun removePlayerFromRoom(player: GamePlayerModel) {
        val playerRoom = getRoomFromClientId(player.clientId)
        gameRooms[playerRoom.room]?.let { model ->
            val currentRoomPlayers = model.players.toMutableList()
            currentRoomPlayers.remove(player)
            if (currentRoomPlayers.size == 0) {
                deleteRoom(playerRoom.room)
                return
            }
            gameRooms[playerRoom.room] = model.copy(players = currentRoomPlayers)
        }
    }


    /**
     * Removes the Game room.
     * Should be only used when the game is over otherwise the game data will be lost
     * @param room : Room to be removed
     * @return the deleted instance of [GameRoomModel]
     */
    private fun deleteRoom(room: String) = gameRooms.remove(room)

    /**
     * Fetch the [GameRoomModel] where the [GamePlayerModel] has register with its client_id
     * @param clientId Client_Id of the player
     * @return [GameRoomModel] which has the client_id
     */
    fun getRoomFromClientId(clientId: String): GameRoomModel = gameRooms.values
        .find { rooms ->
            rooms.players.any { player -> player.clientId == clientId }
        } ?: throw PlayerRoomNotFoundException()

}