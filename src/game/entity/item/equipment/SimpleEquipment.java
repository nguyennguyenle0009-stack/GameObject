package game.entity.item.equipment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.enums.EquipSlot;

/**
 * Trang bị đơn giản dùng màu sắc minh họa.
 */
public class SimpleEquipment extends Equipment {
    private final BufferedImage icon;

    public SimpleEquipment(String name, String desc, EquipSlot slot,
                           int atk, int def, int soul, int bagBonus, Color color) {
        super(name, desc, slot, atk, def, soul, bagBonus);
        this.icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, 32, 32);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 31, 31);
        g.dispose();
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
