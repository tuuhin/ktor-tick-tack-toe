package com.eva.tick_tack_toe.utils

import com.eva.tick_tack_toe.feature_game.utils.PlayerRoomNotFoundException
import com.eva.tick_tack_toe.feature_game.models.GameRoomModel
import com.eva.tick_tack_toe.feature_room.models.GamePlayerModel
import io.ktor.util.*
import io.ktor.util.logging.*
import java.util.concurrent.ConcurrentHashMap

/**
 * [RoomAndPlayerServer] class will help to interact with the joined players and available gameRooms
 */
class RoomAndPlayerServer {

    /**
     * Logger for the connector Messages
     */
    private val playerLogger = KtorSimpleLogger("ROOM_AND_PLAYER_SERVER_LOGGER")


    /**
     * The lock variable for synchronized work of adding and removing players
     */
    private val lock = Any()

    /**
     * A [ConcurrentHashMap] to hold the Room and the Game Models.
     * When a player creates a room, a roomId is generated that is treated as the key.
     * But there is also an option to play the game without creating the room.
     * In that case the server will spin up a random roomId, and [GameRoomModel.isAnonymous]
     * flags shows if this an anonymous room
     */
    val gameRooms = ConcurrentHashMap<String, GameRoomModel>()

    /**
     * On creating a new room via post-request.
     * A new room will be prepared which latter can be used by the players
     * @param board  No of boards for the game
     * @param isAnonymous  Is the room being anonymous?
     * @return The newly created [GamePlayerModel] instance
     */
    fun createGameRoom(board: Int = 1, isAnonymous: Boolean = false): GameRoomModel {
        val room = generateNonce()
        val model = GameRoomModel(room = room, boardCount = board, isAnonymous = isAnonymous)

        gameRooms[room] = model
        return gameRooms.getOrDefault(room, defaultValue = model)
    }

    /**
     * Adds a player to an anonymous room,if there is no room its creates a new room and then adds the new user
     * @param player A [GamePlayerModel] representing the new player
     * @return The instance of the [GamePlayerModel]  either the newly created one or an instance to which
     * the new player is assigned
     **/
    fun addAnonymousPlayerToRoom(player: GamePlayerModel): GameRoomModel? {

        var gameRoom: GameRoomModel? = null

        synchronized(lock) {
            gameRooms.values.find { it.isAnonymous && it.players.size < 2 }
                ?.let { model ->
                    val xPlayerExits = model.players.any { it.symbol == BoardSymbols.XSymbol }

                    val playerWithSymbol = player.copy(
                        symbol = if (xPlayerExits) BoardSymbols.OSymbol else BoardSymbols.XSymbol
                    )
                    if (model.players.size < 2) {
                        gameRooms[model.room]?.addPlayersToTheRoom(playerWithSymbol)
                    }
                    gameRoom = gameRooms[model.room]
                    playerLogger.info("ADDED PLAYER TO THE ROOM WITH ROOM ID ${model.room}")
                } ?: run {
                createGameRoom(isAnonymous = true).also { model ->
                    model.addPlayersToTheRoom(player)
                    gameRooms[model.room] = model

                    gameRoom = gameRooms[model.room]
                    playerLogger.info("CREATED GAME ROOM WITH ROOM ID ${model.room}")
                }
            }
        }
        return gameRoom
    }


    /**
     * Adds the new player to a given room,also pick a proper symbol according to available choices
     * @param room RoomId
     * @param player A [GamePlayerModel] instance representing the new player
     * @return [GameRoomModel] for the given room or returns null
     */
    fun addPlayersToRoom(room: String, player: GamePlayerModel): GameRoomModel? {
        synchronized(lock) {
            gameRooms[room]?.let { model ->
                val xPlayerExits = model.players.any { it.symbol == BoardSymbols.XSymbol }

                val playerWithSymbol = player.copy(
                    symbol = if (xPlayerExits) BoardSymbols.OSymbol else BoardSymbols.XSymbol
                )

                if (model.players.size < 2) {
                    gameRooms[model.room]?.addPlayersToTheRoom(playerWithSymbol)
                    playerLogger.info("ADDED PLAYER TO THE ROOM WITH ROOM ID ${model.room}")
                } else {
                    playerLogger.info("THE ROOM CAN ONLY HAVE 2 MEMBERS, WHICH ARE ALREADY PRESENT")
                }
            } ?: playerLogger.warn("PROVIDED ROOM ID NOT FOUND")
        }
        return gameRooms.getOrDefault(room, null)
    }


    /**
     * Removes the player from the game room, and if the game room is found to be empty also deletes
     * the room
     * @param player The instance of [GamePlayerModel] to be removed
     */
    fun removePlayerFromRoom(player: GamePlayerModel) = synchronized(lock) {
        val playerRoom = getRoomFromClientId(player.clientId)
        gameRooms[playerRoom.room]?.let { model ->

            model.removePlayersFromTheRoom(player)
            playerLogger.info("REMOVING PLAYER ${player.clientId} FROM THE ROOM")

            if (model.players.isEmpty()) {

                playerLogger.info("DELETING THE ROOM WITH ID :${playerRoom.room}")
                playerRoom.cancelScope()
                deleteRoom(playerRoom.room)

            } else {
                gameRooms[playerRoom.room] = model
            }
        }
    }


    /**
     * Removes the Game room.
     * Should be only used when the game is over otherwise the game data will be lost
     * @param room  Room to be removed
     * @return the deleted instance of [GameRoomModel]
     */
    private fun deleteRoom(room: String) = gameRooms.remove(room)


    /**
     * Fetch the [GameRoomModel] where the [GamePlayerModel] has register with its client_id
     * @param clientId Client_Id of the player
     * @return [GameRoomModel] which has the client_id
     * @throws [PlayerRoomNotFoundException] if none room found.
     */
    fun getRoomFromClientId(clientId: String): GameRoomModel = gameRooms.values
        .find { rooms ->
            rooms.players.any { player -> player.clientId == clientId }
        } ?: throw PlayerRoomNotFoundException()

    /**
     * Fetches the [GameRoomModel] with the provided roomId
     * @param roomId The roomId of the room to be searched for.
     * @return The [GameRoomModel] with matching roomId
     * @throws [PlayerRoomNotFoundException] if roomId is not found
     */
    fun getRoomFromRoomId(roomId: String): GameRoomModel = gameRooms[roomId]
        ?: throw PlayerRoomNotFoundException()
}