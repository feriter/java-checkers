package ru.nsu.ccfit.ClientServer.Exceptions;

public class TooManyPlayersException extends Exception {
    public TooManyPlayersException(String message) {
        super(message);
    }
}
