package game.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.main.GamePanel;

public class UtilityTool {
    public static BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.drawImage(original, 0, 0, width, height, null);
        graphics2D.dispose();
        return scaledImage;
    }
    public static boolean isInsidePlayerView(int worldX, int worldY, GamePanel gp) {
        return worldX + gp.getTileSize() > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() && 
	           worldX - gp.getTileSize() < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() &&
	           worldY + gp.getTileSize() > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() && 
	           worldY - gp.getTileSize() < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();
    }
}
