# Ktor Tic Tac Toe Game (Backend)

This project is a backend implementation for a Tic Tac Toe game using Ktor framework. It provides the functionality to
create game rooms and play Tic Tac Toe in real-time with other users through websockets.

## Features

- Create Game Room: Users can create game rooms to start a new Tic Tac Toe game, or they can anonymously join a room and
  play with a random user.
- Real-time Gameplay: Utilizes websockets to enable real-time interactions and gameplay.
- Multiplayer Experience: Players can join a game room and take turns to play the game.
- Simple and Efficient: Built using Ktor, providing a lightweight and efficient backend solution.

## Endpoints

- [x] `POST /create-room`: Create a new game room and get the room ID.
- [x] `POST /join-room/`: Check id a room is join able.
- [x] `WEBSOCKETS /ws/game/{room}`: Connect to the room's websocket to play Tic Tac Toe in real-time in a particular
  room
- [x] `WEBSOCKETS /ws/game/`: Connect to an anonymous room to play the game
- [ ] `WEBSOCKETS /ws/chats`: Realtime chat between the players


