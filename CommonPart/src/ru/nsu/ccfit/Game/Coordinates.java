package ru.nsu.ccfit.Game;

public class Coordinates {
    public int x;
    public int y;

    public Coordinates() {}

    public Coordinates(Coordinates c) {
        x = c.x;
        y = c.y;
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Coordinates c) {
        return (x == c.x && y == c.y);
    }
}
