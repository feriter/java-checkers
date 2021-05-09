package ru.nsu.ccfit.ClientServer.Messages;

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
