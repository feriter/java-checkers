package ru.nsu.ccfit.Server;

import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import ru.nsu.ccfit.Exceptions.*;
import ru.nsu.ccfit.Messages.*;
import ru.nsu.ccfit.Game.Move;
import ru.nsu.ccfit.Game.WrongMoveException;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Server extends Thread {
    private final MessageFactory messageFactory;
    private final ArrayList<Session> sessions;
    private final HashMap<Integer, Room> rooms;
    private final ServerSocket serverSocket;
    private final Gson g = new Gson();
    private int roomId = 1;
    private static final Logger logger = LogManager.getLogger(Server.class);

    private interface MessageHandler {
        void handle(String message, Session session) throws InvalidCommandException, IncorrectMessageFormatException, WrongMoveException, InvalidRoomIdException, IOException;
    }
    private interface Command {
        void execute(String args, Session session) throws IOException, InvalidRoomIdException, WrongMoveException, IncorrectMessageFormatException;
    }

    private class MessageFactory {
        private final CommandFactory commandFactory;
        private final HashMap<MessageType, MessageHandler> handler;

        private class CommandMessageHandler implements MessageHandler {
            @Override
            public void handle(String message, Session session) throws InvalidCommandException, IncorrectMessageFormatException, WrongMoveException, InvalidRoomIdException, IOException {
                var cmdMsg = g.fromJson(message, CommandMessage.class);
                var cmdType = cmdMsg.getCommandType();
                if (cmdType == null) {
                    throw new InvalidCommandException("Command does not exist");
                }
                commandFactory.executeCommand(cmdMsg.getCommandType(), cmdMsg.getArgs(), session);
            }
        }
        private class ChatTextHandler implements MessageHandler {
            @Override
            public void handle(String message, Session session) throws IOException {
                var chatMsg = g.fromJson(message, ChatText.class);
                String text = chatMsg.getText();
                text = session.getUserName() + ":" + text;
                sendMessage(new ChatText(text), session);
            }
        }

        public MessageFactory() {
            commandFactory = new CommandFactory();
            handler = new HashMap<>();
            handler.put(MessageType.CommandMessage, new CommandMessageHandler());
            handler.put(MessageType.ChatText, new ChatTextHandler());
        }

        public void handleMessage(String message, Session session) throws IncorrectMessageFormatException, InvalidCommandException, WrongMoveException, InvalidRoomIdException, IOException {
            BaseMessage base = g.fromJson(message, BaseMessage.class);
            if (base.getMessageType() == null) {
                throw new IncorrectMessageFormatException("Incorrect message received");
            }
            handler.get(base.getMessageType()).handle(message, session);
        }

        private class CommandFactory {
            private final HashMap<CommandType, Command> commands;

            public CommandFactory() {
                commands = new HashMap<>();
                commands.put(CommandType.Ping, new Ping());
                commands.put(CommandType.SetUserName, new SetUserName());
                commands.put(CommandType.CreateRoom, new CreateRoom());
                commands.put(CommandType.ConnectToRoom, new ConnectToRoom());
                commands.put(CommandType.DisconnectFromRoom, new DisconnectFromRoom());
                commands.put(CommandType.MakeMove, new MakeMove());
                commands.put(CommandType.GetUserList, new GetUserList());
            }

            public void executeCommand(CommandType type, String args, Session session) throws InvalidCommandException, IOException, WrongMoveException, InvalidRoomIdException, IncorrectMessageFormatException {
                if (!commands.containsKey(type)) {
                    throw new InvalidCommandException("Command does not exist");
                }
                commands.get(type).execute(args, session);
            }

            public class Ping implements Command {
                @Override
                public void execute(String args, Session session) throws IOException {
                    session.check();
                    var response = new CommandMessage(CommandType.Pong, "");
                    sendMessage(response, session);
                }
            }
            public class SetUserName implements Command {
                @Override
                public void execute(String args, Session session) throws IOException {
                    CommandMessage msg = new CommandMessage(CommandType.SetUserName, "");
                    var lastName = session.getUserName();
                    session.setUserName(args.split(" ")[0]);
                    sendMessage(msg, session);
                    var not = new Notification(lastName + " has changed his name to " + session.getUserName());
                    for (var s : sessions) {
                        s.sendMessage(not);
                    }
                }
            }
            public class CreateRoom implements Command {
                @Override
                public void execute(String args, Session session) throws IOException {
                    int id = generateRoomId();
                    rooms.put(id, new Room());
                    var response = new CommandMessage(CommandType.CreateRoom, String.valueOf(id));
                    sendMessage(response, session);
                }
            }
            public class ConnectToRoom implements Command {
                @Override
                public void execute(String args, Session session) throws IOException {
                    CommandMessage response;
                    try {
                        var id = Integer.parseInt(args.split(" ")[0]);
                        if (!rooms.containsKey(id)) {
                            response = new CommandMessage(CommandType.ConnectToRoom, "-1");
                            sendMessage(response, session);
                            sendMessage(new Notification("Room number is " + args.split(" ")[0]), session);
                        } else {
                            rooms.get(id).connect(session);
                            session.setRoom(rooms.get(id));
                            response = new CommandMessage(CommandType.ConnectToRoom, args.split(" ")[0]);
                            sendMessage(response, session);
                            sendMessage(new Notification("Room number is " + args.split(" ")[0]), session);
                            if (rooms.get(id).getSessions().size() == rooms.get(id).getCapacity()) {
                                rooms.get(id).startNewGame();
                            }
                        }
                    } catch (TooManyPlayersException e) {
                        logger.info("Request from " + session.toString() + " to join room denied. Message: " + e.getMessage());
                        response = new CommandMessage(CommandType.ConnectToRoom, "0");
                        sendMessage(response, session);
                        sendMessage(new Notification("Room number is " + args.split(" ")[0]), session);
                    } catch (NotEnoughPlayersException e) {
                        logger.warn("Attempted to start a game, but error occurred. Message: " + e.getMessage());
                        response = new CommandMessage(CommandType.ConnectToRoom, args.split(" ")[0]);
                        sendMessage(response, session);
                        sendMessage(new Notification("Room number is " + args.split(" ")[0]), session);
                    }
                }
            }
            public class DisconnectFromRoom implements Command {
                @Override
                public void execute(String args, Session session) throws InvalidRoomIdException {
                    var id = Integer.parseInt(args.split(" ")[0]);
                    if (!rooms.containsKey(id)) {
                        throw new InvalidRoomIdException("Room does not exist");
                    }
                    rooms.get(id).disconnect(session);
                    if (rooms.get(id).isEmpty()) {
                        rooms.remove(id);
                    }
                }
            }
            public class MakeMove implements Command {
                @Override
                public void execute(String args, Session session) throws WrongMoveException, IOException, IncorrectMessageFormatException {
                    var move = g.fromJson(args, Move.class);
                    if (move == null) {
                        throw new IncorrectMessageFormatException("Move cannot be deserialized");
                    }
                    session.getRoom().makeMove(move, session);
                    var response = new CommandMessage(CommandType.MakeMove, args);
                    for (var s : session.getRoom().getSessions()) {
                        sendMessage(response, s);
                    }
                }
            }
            public class GetUserList implements Command {
                @Override
                public void execute(String args, Session session) throws IOException {
                    var roomSessions = session.getRoom().getSessions();
                    var userNames = new StringBuilder();
                    for (var s : roomSessions) {
                        userNames.append(s.getUserName()).append(",");
                    }
                    userNames.deleteCharAt(userNames.length() - 1);
                    var response = new CommandMessage(CommandType.GetUserList, userNames.toString());
                    sendMessage(response, session);
                }
            }
        }
    }

    public Server() {
        messageFactory = new MessageFactory();
        sessions = new ArrayList<>();
        rooms = new HashMap<>();
        var cfg = new Properties();
        ServerSocket serverSocket1 = null;
        try {
            cfg.load(new FileInputStream("src/resources/Server.cfg"));
            serverSocket1 = new ServerSocket(Integer.parseInt(cfg.getProperty("port")));
        } catch (IOException e) {
            logger.fatal("IO Error while starting. Message: " + e.getMessage());
        }
        serverSocket = serverSocket1;
    }

    public synchronized void handleMessage(String message, Session session) throws WrongMoveException, InvalidCommandException, IncorrectMessageFormatException, InvalidRoomIdException, IOException {
        logger.debug("Received message " + message + " from " + session.toString());
        messageFactory.handleMessage(message, session);
    }

    private synchronized void sendMessage(BaseMessage message, Session sender) throws IOException {
        if (message.getMessageType() != MessageType.ChatText) {
            sender.sendMessage(message);
        } else {
            var room = sender.getRoom();
            for (var u : room.getSessions()) {
                u.sendMessage(message);
            }
        }
    }

    public synchronized void closeSession(Session session) {
        session.interrupt();
        sessions.remove(session);
    }

    @Override
    public void run() {
        if (serverSocket == null) {
            return;
        }
        try {
            logger.info("Server started");
            while (!serverSocket.isClosed()) {
                Socket client = serverSocket.accept();
                logger.info("New connection: " + client.toString());
                var newSession = new Session(client, this);
                sessions.add(newSession);
                newSession.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer generateRoomId() {
        return roomId++;
    }
}
