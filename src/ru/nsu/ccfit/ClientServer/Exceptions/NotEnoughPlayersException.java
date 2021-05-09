package ru.nsu.ccfit.ClientServer.Exceptions;

public class NotEnoughPlayersException extends Exception {
    public NotEnoughPlayersException(String message) {
        super(message);
    }
}
