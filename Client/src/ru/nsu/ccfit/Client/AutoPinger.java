package ru.nsu.ccfit.Client;

import ru.nsu.ccfit.Messages.CommandMessage;
import ru.nsu.ccfit.Messages.CommandType;

import java.io.IOException;

public class AutoPinger extends Thread {
    private final Client client;

    public AutoPinger(Client c) {
        client = c;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                client.sendMessage(new CommandMessage(CommandType.Ping, ""));
                sleep(2000);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
