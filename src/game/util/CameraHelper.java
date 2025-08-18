package game.util;

import java.awt.Point;

import game.main.GamePanel;

public class CameraHelper {

    /**
     * Tính vị trí trên màn hình (screenX, screenY) từ worldX/worldY.
     * Tự động kẹp biên map nếu player chạm mép.
     */
    public static Point worldToScreen(int worldX, int worldY, GamePanel gp) {
        int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
        int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

        int rightOffset  = gp.getScreenWidth()  - gp.getPlayer().getScreenX();
        int bottomOffset = gp.getScreenHeight() - gp.getPlayer().getScreenY();

        // --- KẸP THEO TRỤC X ---
        if (gp.getPlayer().getScreenX() > gp.getPlayer().getWorldX()) {
            screenX = worldX; // mép trái
        } else if (rightOffset > gp.getWorldWidth() - gp.getPlayer().getWorldX()) {
            screenX = gp.getScreenWidth() - (gp.getWorldWidth() - worldX); // mép phải
        }

        // --- KẸP THEO TRỤC Y ---
        if (gp.getPlayer().getScreenY() > gp.getPlayer().getWorldY()) {
            screenY = worldY; // mép trên
        } else if (bottomOffset > gp.getWorldHeight() - gp.getPlayer().getWorldY()) {
            screenY = gp.getScreenHeight() - (gp.getWorldHeight() - worldY); // mép dưới
        }

        return new Point(screenX, screenY);
    }
}

