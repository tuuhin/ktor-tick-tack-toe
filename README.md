# 🎲 Tic Tac Toe Game (Backend)

This project is a backend implementation for a Tic Tac Toe game using `Ktor framework`.It provides the functionality to
create game rooms and play Tic Tac Toe in real-time with other users through websockets.

## ⚒️ Features

- Create Game Room: Users can create game rooms to start a new Tic Tac Toe game, or they can anonymously join a room and
  play with a random user.
- Real-time Gameplay: Utilizes websockets to enable real-time interactions and gameplay.
- Multiplayer Experience: Players can join a game room and take turns to play the game.
- Simple and Efficient: Built using Ktor, providing a lightweight and efficient backend solution.

## 🍴 Endpoints

Endpoints to create and verify the room

- [x] `POST /create-room`: Create a new game room and get the room ID.
- [x] `POST /join-room/`: Check id a room is join able.

Websockets endpoints to play the game

- [x] `WEBSOCKETS /ws/game/{room}`: Connect to the room's websocket to play Tic Tac Toe in real-time in a particular
  room.
- [x] `WEBSOCKETS /ws/game/`: Connect to an anonymous room to play the game.

### 🕹️ Actual Game

The actual game is an android application for more information.Head over
to [Tic Tac Toe Game](https://github.com/tuuhin/AndroidTicTacToe).

### 🙂 Conclusion

As its said, a project is never complete, The game handles most of the correct use cases of the `Tic Tac Toe` game.And
the game is complete with the necessities to be taken into consideration

If the frontend demands, more changes will be added latter.
