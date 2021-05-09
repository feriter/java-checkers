package ru.nsu.ccfit.ClientServer.Server;

import com.google.gson.Gson;
import ru.nsu.ccfit.ClientServer.Exceptions.NotEnoughPlayersException;
import ru.nsu.ccfit.ClientServer.Exceptions.TooManyPlayersException;
import ru.nsu.ccfit.ClientServer.Messages.CommandMessage;
import ru.nsu.ccfit.ClientServer.Messages.CommandType;
import ru.nsu.ccfit.ClientServer.Messages.Notification;
import ru.nsu.ccfit.Game.CheckerColor;
import ru.nsu.ccfit.Game.GameModel;
import ru.nsu.ccfit.Game.Move;
import ru.nsu.ccfit.Game.WrongMoveException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Room {
    private final int capacity = 2;
    private final ArrayList<Session> sessions;
    private final GameModel game;
    private final Gson g = new Gson();
    private CheckerColor currentTurn = CheckerColor.White;

    public Room() {
        sessions = new ArrayList<>();
        game = new GameModel();
    }

    public synchronized void startNewGame() throws NotEnoughPlayersException, IOException {
        if (sessions.size() < capacity) {
            throw new NotEnoughPlayersException("Need " + capacity + " players to start game");
        }
        var white = new Random().nextInt(2);
        sessions.get(white).setColor(CheckerColor.White);
        sessions.get((white + 1) % 2).setColor(CheckerColor.Black);
        var wNot = new Notification("You play as white");
        var bNot = new Notification("You play as black");
        sessions.get(white).sendMessage(wNot);
        sessions.get((white + 1) % 2).sendMessage(bNot);
        game.restart();
    }

    public synchronized void connect(Session session) throws TooManyPlayersException, NotEnoughPlayersException, IOException {
        if (sessions.size() == capacity) {
            throw new TooManyPlayersException("No more than " + capacity + " players allowed");
        }
        for (var s : sessions) {
            var msg = new Notification(session.getUserName() + " connected to this room");
            s.sendMessage(msg);
        }
        sessions.add(session);
    }

    public synchronized void disconnect(Session session) {
        sessions.remove(session);
    }

    public synchronized void makeMove(Move move, Session session) throws WrongMoveException {
        if (sessions.size() == capacity) {
            if (session.getColor() == currentTurn) {
                game.makeMove(move);
                game.notifyObservers();
                currentTurn = currentTurn == CheckerColor.White ? CheckerColor.Black : CheckerColor.White;
            } else {
                throw new WrongMoveException("Not your turn");
            }
        } else {
            throw new WrongMoveException("Cannot move before the game started");
        }
    }

    public boolean isEmpty() {
        return sessions.isEmpty();
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public int getCapacity() {
        return capacity;
    }
}
