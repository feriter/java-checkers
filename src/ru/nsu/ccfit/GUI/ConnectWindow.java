package ru.nsu.ccfit.GUI;

import ru.nsu.ccfit.ClientServer.Client.Client;
import ru.nsu.ccfit.ClientServer.Messages.CommandMessage;
import ru.nsu.ccfit.ClientServer.Messages.CommandType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ConnectWindow extends JFrame {
    private final JTextField roomNumberField;
    private final Client client;
    private final GameGUI gui;

    public ConnectWindow(Client c, GameGUI g) {
        client = c;
        gui = g;

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
            this.setVisible(false);
            gui.getMainMenu().setVisible(true);
        });

        var layout = new SpringLayout();
        container.setLayout(layout);
        CustomPlacing.placeComponent(layout, container, createButton, 125, 10, 150, 30);
        CustomPlacing.placeComponent(layout, container, roomNumberField, 125, 60, 150, 30);
        CustomPlacing.placeComponent(layout, container, connectButton, 125, 110, 150, 30);
        CustomPlacing.placeComponent(layout, container, backButton, 125, 160, 150, 30);
    }
}
