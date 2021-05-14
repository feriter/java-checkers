package ru.nsu.ccfit.GUI;

import ru.nsu.ccfit.Client.Client;

public class app {
    public static void main(String[] args) {
        var client = new Client();
        client.start();
        var gui = new GameGUI(client);
    }
}
