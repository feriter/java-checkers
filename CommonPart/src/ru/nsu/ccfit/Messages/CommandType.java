package ru.nsu.ccfit.Messages;

public enum CommandType {
    Ping,
    Pong,
    SetUserName,
    CreateRoom,
    ConnectToRoom,
    DisconnectFromRoom,
    MakeMove,
    GetUserList,
    Help
}
