package ru.nsu.ccfit;

public interface GUIObservable {
    void registerObserver(GUIObserver o);
    void removeObserver(GUIObserver o);
    void notifyObservers(GUIAction a);
}
