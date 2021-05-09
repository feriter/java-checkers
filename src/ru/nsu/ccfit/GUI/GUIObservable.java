package ru.nsu.ccfit.GUI;

public interface GUIObservable {
    void registerObserver(GUIObserver o);
    void removeObserver(GUIObserver o);
    void notifyObservers();
}
