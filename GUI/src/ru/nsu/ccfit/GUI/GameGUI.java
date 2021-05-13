package ru.nsu.ccfit.GUI;

import ru.nsu.ccfit.Client.Client;
import ru.nsu.ccfit.GUIAction;
import ru.nsu.ccfit.GUIObserver;

public class GameGUI implements GUIObserver {
    private final MainMenuWindow mainMenu;
    private final GameWindow gameWindow;
    private final ConnectWindow connectWindow;

    public GameGUI(Client c) {
        mainMenu = new MainMenuWindow(c);
        gameWindow = new GameWindow(c);
        connectWindow = new ConnectWindow(c);
        mainMenu.registerObserver(this);
        gameWindow.registerObserver(this);
        connectWindow.registerObserver(this);
        c.registerObserver(this);
        mainMenu.setVisible(true);
    }

    @Override
    public void update(GUIAction a) {
        switch (a) {
            case Connected:
                connectWindow.setVisible(false);
                gameWindow.setVisible(true);
                break;
            case Play:
                mainMenu.setVisible(false);
                connectWindow.setVisible(true);
                break;
            case ConnectBack:
                connectWindow.setVisible(false);
                mainMenu.setVisible(true);
                break;
            case Exit:
                System.exit(0);
                break;
            default:
                break;
        }
    }
}
