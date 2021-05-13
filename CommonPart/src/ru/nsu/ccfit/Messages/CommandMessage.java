package ru.nsu.ccfit.Messages;

public class CommandMessage extends BaseMessage {
    private String args;
    private CommandType type;

    public CommandMessage() {}

    public CommandMessage(CommandType t, String a) {
        super(MessageType.CommandMessage);
        args = a;
        type = t;
    }

    public CommandType getCommandType() {
        return type;
    }

    public String getArgs() {
        return args;
    }
}
