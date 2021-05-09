package ru.nsu.ccfit.GUI;

import org.w3c.dom.css.Rect;
import ru.nsu.ccfit.ClientServer.Client.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainMenuWindow extends JFrame {
    private final Client client;
    private final GameGUI gui;

    public MainMenuWindow(Client c, GameGUI g) {
        client = c;
        gui = g;
        var buttonPlay = new JButton("Play");
        var buttonAbout = new JButton("About");
        var buttonExit = new JButton("Exit");

        setBounds(500, 100, 400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var container = getContentPane();
        var layout = new SpringLayout();
        container.setLayout(layout);
        CustomPlacing.placeComponent(layout, container, buttonPlay, 130, 250, 140, 50);
        CustomPlacing.placeComponent(layout, container, buttonAbout, 130, 320, 140, 50);
        CustomPlacing.placeComponent(layout, container, buttonExit, 130, 390, 140, 50);
        CustomPlacing.placeComponent(layout, container, new Canvas() {
            @Override
            public void paint(Graphics g) {
                BufferedImage image = null;
                try {
                    image = ImageIO.read(new File("src/resources/jaba.jpg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                var g2d = (Graphics2D) g.create();
                var paint = new TexturePaint(image, new Rectangle(0, 0, 160, 120));
                g2d.setPaint(paint);
                g2d.fillRect(0, 0, 160, 120);
            }
        }, 120, 110, 160, 120);
        CustomPlacing.placeComponent(layout, container, new Canvas() {
            @Override
            public void paint(Graphics g) {
                BufferedImage image = null;
                try {
                    image = ImageIO.read(new File("src/resources/checkers.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                var g2d = (Graphics2D) g.create();
                var paint = new TexturePaint(image, new Rectangle(0, 0, 100, 100));
                g2d.setPaint(paint);
                g2d.fillRect(0, 0, 100, 100);
            }
        }, 150, 10, 100, 100);

        buttonPlay.addActionListener(e -> {
            client.launchNewGame();
            gui.getConnectWindow().setVisible(true);
            setVisible(false);
        });
        buttonExit.addActionListener(e -> {
            setVisible(false);
            System.exit(0);
        });
    }
}
