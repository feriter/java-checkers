package ru.nsu.ccfit.ClientServer.Exceptions;

public class InvalidCommandException extends Exception {
    public InvalidCommandException(String message) {
        super(message);
    }
}
