package game.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.entity.Player;
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
        Player player = gp.getPlayer();
        int tileSize = gp.getTileSize();
        int offset = tileSize * 4;
        int playerWorldX = player.getWorldX();
        int playerWorldY = player.getWorldY();
        int playerScreenX = player.getScreenX();
        int playerScreenY = player.getScreenY();
        return worldX + offset > playerWorldX - playerScreenX &&
                   worldX - offset < playerWorldX + playerScreenX &&
                   worldY + offset > playerWorldY - playerScreenY &&
                   worldY - offset < playerWorldY + playerScreenY;
    }
    
    public static int getXForCenterOfText(String text, GamePanel gamePanel, Graphics2D graphics2D) {
        int length = (int) graphics2D.getFontMetrics().getStringBounds(text, graphics2D).getWidth();
        return gamePanel.getScreenWidth() / 2 - length / 2;
    }
}
