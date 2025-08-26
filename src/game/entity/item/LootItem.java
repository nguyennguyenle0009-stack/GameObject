package game.entity.item;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import game.entity.Player;

/** Simple collectible item dropped by monsters. */
public class LootItem extends Item {
    private final String iconPath;
    private final BufferedImage icon;

    public LootItem(String name, String desc, String iconPath, int quantity, int maxStack) {
        super(name, desc, quantity, maxStack);
        this.iconPath = iconPath;
        BufferedImage img = null;
        if (iconPath != null) {
            try {
                img = ImageIO.read(getClass().getResourceAsStream(iconPath));
            } catch (IOException | IllegalArgumentException e) {
                img = null;
            }
        }
        this.icon = img;
    }

    @Override
    public void use(Player p) {
        // Materials currently have no use
    }

    @Override
    public Item copyWithQuantity(int qty) {
        return new LootItem(getName(), getDecription(), iconPath, qty, getMaxStack());
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
