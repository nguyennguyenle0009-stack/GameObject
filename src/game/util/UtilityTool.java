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
    
    //Objet được vẽ cách ngoài biên màn hình 2 * gp.getTileSize()
    public static boolean isInsidePlayerView(int worldX, int worldY, GamePanel gp) {
        return worldX + 4 * gp.getTileSize() > gp.getCameraX() &&
                   worldX - 4 * gp.getTileSize() < gp.getCameraX() + gp.getScreenWidth() &&
                   worldY + 4 * gp.getTileSize() > gp.getCameraY() &&
                   worldY - 4 * gp.getTileSize() < gp.getCameraY() + gp.getScreenHeight();
    }
    
    public static int getXForCenterOfText(String text, GamePanel gamePanel, Graphics2D graphics2D) {
        int length = (int) graphics2D.getFontMetrics().getStringBounds(text, graphics2D).getWidth();
        return gamePanel.getScreenWidth() / 2 - length / 2;
    }
}
