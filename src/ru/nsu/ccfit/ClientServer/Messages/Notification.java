package ru.nsu.ccfit.ClientServer.Messages;

public class Notification extends BaseMessage {
    private String text;

    public Notification() {}

    public Notification(String t) {
        super(MessageType.Notification);
        text = t;
    }

    public String getText() {
        return text;
    }
}
