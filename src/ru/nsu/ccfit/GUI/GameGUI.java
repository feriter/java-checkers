package ru.nsu.ccfit.GUI;

import ru.nsu.ccfit.ClientServer.Client.Client;
import ru.nsu.ccfit.Game.Observer;

public class GameGUI implements GUIObserver {
    private final MainMenuWindow mainMenu;
    private final GameWindow gameWindow;
    private final ConnectWindow connectWindow;
    private final Client client;

    public GameGUI(Client c) {
        client = c;
        mainMenu = new MainMenuWindow(c, this);
        gameWindow = new GameWindow(c, this);
        connectWindow = new ConnectWindow(c, this);
        c.registerObserver(this);
        mainMenu.setVisible(true);
    }

    public MainMenuWindow getMainMenu() {
        return mainMenu;
    }

    public GameWindow getGameWindow() {
        return gameWindow;
    }

    public ConnectWindow getConnectWindow() {
        return connectWindow;
    }

    @Override
    public void update() {
        connectWindow.setVisible(false);
        gameWindow.setVisible(true);
    }
}
