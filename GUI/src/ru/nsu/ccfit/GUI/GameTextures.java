package ru.nsu.ccfit.GUI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GameTextures {
    public TexturePaint wp;
    public TexturePaint bp;
    public TexturePaint wq;
    public TexturePaint bq;
    public TexturePaint hl;
    public TexturePaint fieldT;

    public GameTextures() {
        try {
            // Load textures of game
            var whitePawn = ImageIO.read(new File("src/resources/whitePawn.png"));
            var blackPawn = ImageIO.read(new File("src/resources/blackPawn.png"));
            var whiteQueen = ImageIO.read(new File("src/resources/whitePawn.png"));
            var blackQueen = ImageIO.read(new File("src/resources/blackPawn.png"));
            var field = ImageIO.read(new File("src/resources/field.jpg"));
            var highlight = ImageIO.read(new File("src/resources/highlight.png"));

            wp = new TexturePaint(whitePawn, new Rectangle(25, 25, 50, 50));
            bp = new TexturePaint(blackPawn, new Rectangle(25, 25, 50, 50));
            wq = new TexturePaint(whiteQueen, new Rectangle(25, 25, 50, 50));
            bq = new TexturePaint(blackQueen, new Rectangle(25, 25, 50, 50));
            hl = new TexturePaint(highlight, new Rectangle(25, 25, 50, 50));
            fieldT = new TexturePaint(field, new Rectangle(25, 25, 500, 500));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
