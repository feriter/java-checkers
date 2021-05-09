package ru.nsu.ccfit.Game;

public class Checker {
    public final CheckerColor color;
    public final CheckerType type;
    public boolean selected;
    public int x;
    public int y;

    public Checker(CheckerColor c, CheckerType t, Coordinates coords) {
        type = t;
        color = c;
        x = coords.x;
        y = coords.y;
        selected = false;
    }
}
