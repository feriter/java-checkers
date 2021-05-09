package ru.nsu.ccfit.ClientServer.Client;

import ru.nsu.ccfit.ClientServer.Messages.MessageType;
import ru.nsu.ccfit.Game.Observable;
import ru.nsu.ccfit.Game.Observer;

import java.util.ArrayList;

public class ChatMessagesDisplay implements Observable {
    private final int maxMessagesCount = 16;
    private final ArrayList<TextMessage> messages;
    private final ArrayList<Observer> observers;

    public class TextMessage {
        public String message;
        public MessageType type;

        public TextMessage(String str, MessageType t) {
            message = str;
            type = t;
        }
    }

    public void addMessage(String message, MessageType type) {
        if (messages.size() >= maxMessagesCount) {
            messages.remove(0);
        }
        messages.add(new TextMessage(message, type));
    }

    public ChatMessagesDisplay() {
        messages = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public ArrayList<TextMessage> getMessages() {
        return messages;
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (var obs : observers) {
            obs.update();
        }
    }
}
