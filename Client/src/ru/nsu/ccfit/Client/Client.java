package ru.nsu.ccfit.Client;

import com.google.gson.Gson;
import ru.nsu.ccfit.Exceptions.*;
import ru.nsu.ccfit.GUIAction;
import ru.nsu.ccfit.GUIObservable;
import ru.nsu.ccfit.GUIObserver;
import ru.nsu.ccfit.Messages.*;
import ru.nsu.ccfit.Game.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Client extends Thread implements GUIObservable {
    private final MessageFactory messageFactory;
    private final GameModel gameModel;
    private final ChatMessagesDisplay messagesDisplay;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String userName;
    private final Gson g = new Gson();
    private final ArrayList<GUIObserver> observers;
    private final AutoPinger autoPinger = new AutoPinger(this);

    private interface MessageHandler {
        void handle(String message) throws Exception;
    }
    private interface Command {
        void execute(String args) throws Exception;
    }

    private class MessageFactory {
        private final CommandFactory commandFactory;
        private final HashMap<MessageType, MessageHandler> handler;
        private final Gson g = new Gson();

        private class CommandFactory {
            private final HashMap<CommandType, Command> commands;
            private final Gson g = new Gson();

            public CommandFactory() {
                commands = new HashMap<>();
                commands.put(CommandType.Pong, new Pong());
                commands.put(CommandType.SetUserName, new SetUserName());
                commands.put(CommandType.CreateRoom, new CreateRoom());
                commands.put(CommandType.ConnectToRoom, new ConnectToRoom());
                commands.put(CommandType.DisconnectFromRoom, new DisconnectFromRoom());
                commands.put(CommandType.MakeMove, new MakeMove());
                commands.put(CommandType.GetUserList, new GetUserList());
            }

            public void executeCommand(CommandType type, String args) throws Exception {
                if (!commands.containsKey(type)) {
                    throw new InvalidCommandException("Command exists, but is not allowed");
                }
                commands.get(type).execute(args);
                getMessagesDisplay().notifyObservers();
            }

            private class Pong implements Command {
                @Override
                public void execute(String args) {
//                    var response = new CommandMessage(CommandType.Ping, "");
//                    out.writeUTF(g.toJson(response));
                }
            }
            private class SetUserName implements Command {
                @Override
                public void execute(String args) throws IOException {
                    userName = args.split(" ")[0];
                }
            }
            private class CreateRoom implements Command {
                @Override
                public void execute(String args) throws IOException {
                    var message = new CommandMessage(CommandType.ConnectToRoom, args);
                    sendMessage(message);
                }
            }
            private class ConnectToRoom implements Command {
                @Override
                public void execute(String args) throws InvalidRoomIdException, TooManyPlayersException, IOException {
                    switch (args) {
                        case "-1":
                            throw new InvalidRoomIdException("Baka. No such room exists");
                        case "0":
                            throw new TooManyPlayersException("No space for you");
                        default:
                            launchNewGame();
                            // sendMessage(new CommandMessage(CommandType.GetUserList, ""));
                            notifyObservers(GUIAction.Connected);
                            break;
                    }
                }
            }
            private class DisconnectFromRoom implements Command {
                @Override
                public void execute(String args) {

                }
            }
            private class MakeMove implements Command {
                @Override
                public void execute(String args) throws WrongMoveException {
                    var move = g.fromJson(args, Move.class);
                    if (move == null) {
                        throw new WrongMoveException("Cannot deserialize move");
                    }
                    gameModel.makeMove(move);
                }
            }
            private class GetUserList implements Command {
                @Override
                public void execute(String args) {
                    var users = args.split(",");
                    messagesDisplay.addMessage(users.length + " users in this room: ", MessageType.Notification);
                    for (var u : users) {
                        messagesDisplay.addMessage(u, MessageType.Notification);
                    }
                }
            }
        }

        public MessageFactory() {
            commandFactory = new CommandFactory();
            handler = new HashMap<>();
            handler.put(MessageType.ChatText, new ChatTextHandler());
            handler.put(MessageType.CommandMessage, new CommandMessageHandler());
            handler.put(MessageType.Notification, new NotificationHandler());
        }

        public void handleMessage(String message) throws Exception {
            BaseMessage base = g.fromJson(message, BaseMessage.class);
            if (base == null) {
                throw new IncorrectMessageFormatException("Incorrect message received");
            }
            handler.get(base.getMessageType()).handle(message);
            getMessagesDisplay().notifyObservers();
        }

        private class ChatTextHandler implements MessageHandler {
            @Override
            public void handle(String message) {
                var msg = g.fromJson(message, ChatText.class);
                getMessagesDisplay().addMessage(msg.getText(), MessageType.ChatText);
            }
        }
        private class CommandMessageHandler implements MessageHandler {
            @Override
            public void handle(String message) throws Exception {
                var cmdMsg = g.fromJson(message, CommandMessage.class);
                var cmdType = cmdMsg.getCommandType();
                if (cmdType == null) {
                    throw new InvalidCommandException("Command does not exist");
                }
                commandFactory.executeCommand(cmdMsg.getCommandType(), cmdMsg.getArgs());
            }
        }
        private class NotificationHandler implements MessageHandler {
            @Override
            public void handle(String message) {
                var msg = g.fromJson(message, Notification.class);
                getMessagesDisplay().addMessage(msg.getText(), MessageType.Notification);
                getMessagesDisplay().notifyObservers();
            }
        }
    }

    public void sendMessage(BaseMessage message) throws IOException {
        out.writeUTF(g.toJson(message));
    }

    public void launchNewGame() {
        gameModel.restart();
        messagesDisplay.getMessages().clear();
    }

    public Client() {
        gameModel = new GameModel();
        messagesDisplay = new ChatMessagesDisplay();
        observers = new ArrayList<>();
        var config = new Properties();
        Socket socket1 = null;
        DataInputStream in1 = null;
        DataOutputStream out1 = null;
        try {
            config.load(new FileInputStream("src/resources/Client.cfg"));
            userName = config.getProperty("userName");
            socket1 = new Socket(InetAddress.getByName(config.getProperty("address")),
                    Integer.parseInt(config.getProperty("port")));
            in1 = new DataInputStream(socket1.getInputStream());
            out1 = new DataOutputStream(socket1.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = socket1;
        in = in1;
        out = out1;
        messageFactory = new MessageFactory();
    }

    @Override
    public void run() {
        if (socket == null || in == null || out == null) {
            return;
        }
        try {
            sendMessage(new CommandMessage(CommandType.SetUserName, userName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        autoPinger.start();
        while (!socket.isClosed()) {
            String inMsg;
            try {
                inMsg = in.readUTF();
                messageFactory.handleMessage(inMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public ArrayList<Checker> getFigures() {
        return gameModel.getFigures();
    }

    public ChatMessagesDisplay getMessagesDisplay() {
        return messagesDisplay;
    }

    @Override
    public void registerObserver(GUIObserver o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(GUIObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(GUIAction a) {
        for (var o : observers) {
            o.update(a);
        }
    }
}
