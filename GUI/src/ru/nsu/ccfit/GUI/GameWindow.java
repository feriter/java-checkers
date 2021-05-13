package ru.nsu.ccfit.GUI;

import com.google.gson.Gson;
import ru.nsu.ccfit.Client.ChatMessagesDisplay;
import ru.nsu.ccfit.Client.Client;
import ru.nsu.ccfit.GUIAction;
import ru.nsu.ccfit.GUIObservable;
import ru.nsu.ccfit.GUIObserver;
import ru.nsu.ccfit.Messages.ChatText;
import ru.nsu.ccfit.Messages.CommandMessage;
import ru.nsu.ccfit.Messages.CommandType;
import ru.nsu.ccfit.Messages.MessageType;
import ru.nsu.ccfit.Game.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

public class GameWindow extends JFrame implements Observer, GUIObservable {
    private final Client client;
    private final CheckersPanel checkersPanel;
    private final ArrayList<GUIObserver> observers;

    private final JTextField fieldToEnterMessage = new JTextField();
    private final JTextPane chatMessages = new JTextPane();

    private class CheckersPanel extends Canvas implements MouseListener {
        private final ArrayList<Checker> figures;
        private final GameTextures textures;
        private Coordinates selectedCoords = null;

        @Override
        public void paint(Graphics g) {
            var g2d = (Graphics2D) g.create();

            g2d.setPaint(textures.fieldT);
            g2d.fillRect(25, 25, 400, 400);

            if (selectedCoords != null) {
                for (var c : figures) {
                    if (selectedCoords.equals(new Coordinates(c.x, c.y))) {
                        c.selected = true;
                        break;
                    }
                }
            }
            for (var f : figures) {
                if (f.selected) {
                    g2d.setPaint(textures.hl);
                    g2d.fillRect(50 * f.x - 25, 50 * f.y - 25, 50, 50);
                }
                g2d.setPaint((f.color == CheckerColor.White) ?
                        ((f.type == CheckerType.Pawn) ? (textures.wp) : (textures.wq)) :
                        ((f.type == CheckerType.Pawn) ? (textures.bp) : (textures.bq)));
                g2d.fillRect(50 * f.x - 25, 50 * f.y - 25, 50, 50);
            }
        }

        public CheckersPanel(ArrayList<Checker> f) {
            figures = client.getFigures();
            addMouseListener(this);
            textures = new GameTextures();
        }

        @Override
        public void mouseClicked(MouseEvent event) {
            var pressedX = (event.getX() + 25) / 50;
            var pressedY = (event.getY() + 25) / 50;
            Checker pressedFigure = null;
            for (var c : figures) {
                if (pressedX == c.x && pressedY == c.y) {
                    pressedFigure = c;
                    break;
                }
            }
            if (pressedFigure != null) {
                if (selectedCoords == null) {
                    pressedFigure.selected = true;
                } else {
                    for (var f : figures) {
                        if (selectedCoords.equals(new Coordinates(f.x, f.y))) {
                            f.selected = false;
                        }
                    }
                }
                selectedCoords = new Coordinates(pressedX, pressedY);
                repaint();
            } else {
                if (selectedCoords != null) {
                    for (var f : figures) {
                        if (selectedCoords.equals(new Coordinates(f.x, f.y))) {
                            f.selected = false;
                        }
                    }
                    var move = new Move(selectedCoords, new Coordinates(pressedX, pressedY));
                    if (client.getGameModel().isPossible(move)) {
                        var msg = new CommandMessage(CommandType.MakeMove,
                                new Gson().toJson(move));
                        try {
                            client.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                selectedCoords = null;
            }
            GameWindow.this.update();
        }
        @Override
        public void mousePressed(MouseEvent e) {

        }
        @Override
        public void mouseReleased(MouseEvent e) {

        }
        @Override
        public void mouseEntered(MouseEvent e) {

        }
        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public GameWindow(Client c) {
        super("Checkers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(200, 50, 800, 500);
        client = c;
        var figures = client.getFigures();
        checkersPanel = new CheckersPanel(figures);
        observers = new ArrayList<>();

        client.getGameModel().registerObserver(this);
        client.getMessagesDisplay().registerObserver(this);

        chatMessages.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        chatMessages.setMargin(new Insets(5, 5, 5, 5));
        chatMessages.setSelectionColor(Color.BLUE);

        var container = getContentPane();
        var layout = new SpringLayout();
        container.setLayout(layout);
        CustomPlacing.placeComponent(layout, container, checkersPanel, 0, 0, 450, 450);
        CustomPlacing.placeComponent(layout, container, chatMessages, 450, 25, 320, 365);
        CustomPlacing.placeComponent(layout, container, fieldToEnterMessage, 450, 395, 320, 30);

        fieldToEnterMessage.addActionListener((ActionEvent e) -> {
            try {
                var text = e.getActionCommand().trim();
                if (!text.equals("")) {
                    var words = text.split(" ");
                    CommandMessage msg;
                    if (words[0].toLowerCase().charAt(0) == '/') {
                        switch (words[0].toLowerCase()) {
                            case "/setusername":
                                msg = new CommandMessage(CommandType.SetUserName, words[1]);
                                break;
                            case "/userlist":
                                msg = new CommandMessage(CommandType.GetUserList, "");
                                break;
                            default:
                                msg = new CommandMessage();
                                break;
                        }

                        client.sendMessage(msg);
                    } else {
                        client.sendMessage(new ChatText(text));
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                fieldToEnterMessage.setText("");
            }

        });
        fieldToEnterMessage.setFont(Fonts.Input);
    }

    private void appendToPane(String msg, Font font, Color color) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, font.getFamily());
        aset = sc.addAttribute(aset, StyleConstants.Size, font.getSize());

        int len = chatMessages.getDocument().getLength();
        chatMessages.setCaretPosition(len);
        chatMessages.setCharacterAttributes(aset, false);
        chatMessages.replaceSelection(msg);
    }

    @Override
    public void update() {
        var messages = client.getMessagesDisplay().getMessages();
        chatMessages.setText("");
        for (var m : messages) {
            var font = m.type == MessageType.ChatText ? Fonts.Default :
                    (m.type == MessageType.Notification ? Fonts.Notification : Fonts.Special);
            var color = m.type == MessageType.ChatText ? Color.BLACK :
                    (m.type == MessageType.Notification ? new Color(18, 199, 7) : Color.magenta);
            appendToPane(m.message + "\n", font, color);
        }
        checkersPanel.repaint();
    }

    @Override
    public void registerObserver(GUIObserver o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(GUIObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(GUIAction a) {
        for (var o : observers) {
            o.update(a);
        }
    }
}
