package ru.nsu.ccfit.GUI;

import ru.nsu.ccfit.Client.Client;
import ru.nsu.ccfit.GUIAction;
import ru.nsu.ccfit.GUIObservable;
import ru.nsu.ccfit.GUIObserver;
import ru.nsu.ccfit.Messages.CommandMessage;
import ru.nsu.ccfit.Messages.CommandType;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ConnectWindow extends JFrame implements GUIObservable {
    private final JTextField roomNumberField;
    private final Client client;
    private final ArrayList<GUIObserver> observers;

    public ConnectWindow(Client c) {
        client = c;
        observers = new ArrayList<>();

        setBounds(400, 200, 400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Container container = getContentPane();
        roomNumberField = new JTextField("");
        roomNumberField.setFont(Fonts.Input);
        var connectButton = new JButton("Connect to room");
        connectButton.addActionListener(e -> {
            try {
                var msg = new CommandMessage(CommandType.ConnectToRoom, roomNumberField.getText());
                client.sendMessage(msg);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        var createButton = new JButton("Create new room");
        createButton.addActionListener(e -> {
            try {
                var msg = new CommandMessage(CommandType.CreateRoom, "");
                client.sendMessage(msg);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        var backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            notifyObservers(GUIAction.ConnectBack);
        });

        var layout = new SpringLayout();
        container.setLayout(layout);
        CustomPlacing.placeComponent(layout, container, createButton, 125, 10, 150, 30);
        CustomPlacing.placeComponent(layout, container, roomNumberField, 125, 60, 150, 30);
        CustomPlacing.placeComponent(layout, container, connectButton, 125, 110, 150, 30);
        CustomPlacing.placeComponent(layout, container, backButton, 125, 160, 150, 30);
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
