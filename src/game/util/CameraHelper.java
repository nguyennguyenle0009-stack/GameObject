package game.util;

import java.awt.Point;

import game.main.GamePanel;

public class CameraHelper {

    /**
     * Tính vị trí trên màn hình (screenX, screenY) từ worldX/worldY.
     */
    public static Point worldToScreen(int worldX, int worldY, GamePanel gp) {
        int screenX = worldX - gp.getCameraX();
        int screenY = worldY - gp.getCameraY();
        return new Point(screenX, screenY);
    }
}

