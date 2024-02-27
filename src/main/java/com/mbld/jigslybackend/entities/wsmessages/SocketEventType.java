package com.mbld.jigslybackend.entities.wsmessages;

public enum SocketEventType {
    Lobby_InitialData,
    Lobby_RoomCreated,
    Lobby_RoomRemoved,
    UserLobby_FailedToJoinToRoom,
    Room_InitialData,
    Room_UserJoined,
    Room_UserLeft,
    Room_Puzzle_Move,
    Room_Puzzle_Release
}
