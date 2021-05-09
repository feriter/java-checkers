package ru.nsu.ccfit.ClientServer.Messages;

public class BaseMessage {
    private MessageType messageType;

    public BaseMessage() {}

    public BaseMessage(MessageType type) {
        messageType = type;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
