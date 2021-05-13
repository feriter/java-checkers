package ru.nsu.ccfit.Server;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.ccfit.Exceptions.*;
import ru.nsu.ccfit.Messages.BaseMessage;
import ru.nsu.ccfit.Game.CheckerColor;
import ru.nsu.ccfit.Game.WrongMoveException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session extends Thread {
    private final Socket clientSocket;
    private final Server server;
    private Room room;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final static Logger logger = LogManager.getLogger(Server.class);
    private String userName = "Guest";
    private final Gson g = new Gson();
    private CheckerColor color = null;
    private TimeoutCounter timeoutCounter = new TimeoutCounter(this);

    public Session(Socket c, Server s) throws IOException {
        clientSocket = c;
        server = s;
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        timeoutCounter.start();
        String message = null;
        while (!clientSocket.isClosed() && !isInterrupted()) {
            try {
                message = in.readUTF();
                server.handleMessage(message, this);
            } catch (IOException e) {
                logger.error("Troubles with io. Close connection " +
                        clientSocket.toString() + ". Message: " + e.getMessage());
                server.closeSession(this);
            } catch (WrongMoveException e) {
                logger.warn("Received wrong move. Message: " + e.getMessage());
            } catch (IncorrectMessageFormatException e) {
                logger.warn("Received incorrect message. Message: " + message);
            } catch (InvalidRoomIdException e) {
                logger.warn("Room does not exist. Message: " + e.getMessage());
            } catch (InvalidCommandException e) {
                logger.error("Received incorrect command. Message: " + e.getMessage());
            }
        }
        server.closeSession(this);
    }

    public void check() {
        timeoutCounter.check();
    }

    public void sendMessage(BaseMessage message) throws IOException {
        out.writeUTF(g.toJson(message));
    }

    public void setRoom(Room r) {
        room = r;
    }

    public Room getRoom() {
        return room;
    }

    public String toString() {
        return clientSocket.toString();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setColor(CheckerColor color) {
        this.color = color;
    }

    public CheckerColor getColor() {
        return color;
    }
}
