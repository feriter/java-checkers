package ru.nsu.ccfit.Game;

public class Move {
    public Coordinates start;
    public Coordinates end;

    public Move() {}

    public Move(Coordinates s, Coordinates e) {
        start = s;
        end = e;
    }

    public boolean equals(Move m) {
        return (start.equals(m.start) && end.equals(m.end));
    }
}
