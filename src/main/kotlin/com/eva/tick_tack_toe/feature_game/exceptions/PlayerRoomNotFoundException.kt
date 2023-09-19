package com.eva.tick_tack_toe.feature_game.exceptions

/**
 * Thrown when there is no room found.
 * @param message Extra associated messages for the exception
 */
class PlayerRoomNotFoundException(message: String ="") : Exception("Room Not found exception :${message}")