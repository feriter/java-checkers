package ru.nsu.ccfit.Messages;

public class ChatText extends BaseMessage {
    private String text;

    public ChatText() {}

    public ChatText(String t) {
        super(MessageType.ChatText);
        text = t;
    }

    public String getText() {
        return text;
    }
}
