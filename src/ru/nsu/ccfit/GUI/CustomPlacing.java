package ru.nsu.ccfit.GUI;

import javax.swing.*;
import java.awt.*;

public class CustomPlacing {
    public static void placeComponent(SpringLayout layout, Container container, Component component, int x, int y, int width, int height) {
        container.add(component);
        layout.putConstraint(SpringLayout.WEST, component, x, SpringLayout.WEST, container);
        layout.putConstraint(SpringLayout.NORTH, component, y, SpringLayout.NORTH, container);
        layout.putConstraint(SpringLayout.EAST, component, width, SpringLayout.WEST, component);
        layout.putConstraint(SpringLayout.SOUTH, component, height, SpringLayout.NORTH, component);
    }
}
