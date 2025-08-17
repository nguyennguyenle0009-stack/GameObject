package game.interfaces;

import java.awt.Graphics2D;

import game.main.GamePanel;

public interface DrawableEntity {
    int getFootY();
    void draw(Graphics2D g2, GamePanel gp);
}
