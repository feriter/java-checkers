package ru.nsu.ccfit;

import ru.nsu.ccfit.ClientServer.Client.Client;
import ru.nsu.ccfit.ClientServer.Server.Server;

public class Main {

    public static void main(String[] args) {
        var server = new Server();
        server.start();

        var client1 = new Client();
        var client2 = new Client();
//        var client3 = new Client();
//        var client4 = new Client();

        client1.start();
        client2.start();
//        client3.start();
//        client4.start();
    }
}
