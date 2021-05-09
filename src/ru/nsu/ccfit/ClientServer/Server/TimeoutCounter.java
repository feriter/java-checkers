package ru.nsu.ccfit.ClientServer.Server;

public class TimeoutCounter extends Thread {
    private int interval = 10; // seconds until disconnect
    private int currentInterval = 0;
    private final Session session;

    public TimeoutCounter(Session s) {
        session = s;
    }

    public void check() {
        currentInterval = 0;
    }

    @Override
    public void run() {
        if (currentInterval < interval) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentInterval++;
        } else {
            session.interrupt();
        }
    }
}
