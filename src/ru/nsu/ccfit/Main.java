package ru.nsu.ccfit;

import ru.nsu.ccfit.Client.Client;
import ru.nsu.ccfit.GUI.GameGUI;
import ru.nsu.ccfit.Server.Server;

public class Main {

    public static void main(String[] args) {
        var server = new Server();
        server.start();

        var client1 = new Client();
        var client2 = new Client();

        var gui1 = new GameGUI(client1);
        var gui2 = new GameGUI(client2);

        client1.start();
        client2.start();
    }
}
