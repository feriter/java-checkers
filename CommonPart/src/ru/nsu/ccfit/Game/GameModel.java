package ru.nsu.ccfit.Game;

import java.util.ArrayList;

public class GameModel implements Observable {
    private final Field field;
    private CheckerColor currentTurn;
    private final ArrayList<Observer> observers;

    public GameModel() {
        field = new Field();
        observers = new ArrayList<>();
        currentTurn = CheckerColor.White;
    }

    public boolean isPossible(Move move) {
        return field.isPossible(move);
    }

    public void makeMove(Move move) throws WrongMoveException {
        field.makeMove(move);
        notifyObservers();
    }

    public ArrayList<Checker> getFigures() {
        return field.getFigures();
    }

    public void restart() {
        field.refillField();
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (var obs : observers) {
            obs.update();
        }
    }
}
