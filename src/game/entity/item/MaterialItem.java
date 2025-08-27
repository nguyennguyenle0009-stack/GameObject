package game.entity.item;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.entity.Player;

/** Simple material item dropped by monsters. */
public class MaterialItem extends Item {
    private final String iconPath;
    private final BufferedImage icon;

    public MaterialItem(String name, String desc, String iconPath, int quantity, int maxStack) {
        super(name, desc, quantity, maxStack);
        this.iconPath = iconPath;
        BufferedImage img;
        try {
            img = ImageIO.read(getClass().getResourceAsStream(iconPath));
        } catch (IOException | IllegalArgumentException e) {
            img = null;
        }
        this.icon = img;
    }

    private MaterialItem(MaterialItem other, int qty) {
        this(other.getName(), other.getDecription(), other.iconPath, qty, other.getMaxStack());
    }

    @Override
    public Item copyWithQuantity(int qty) {
        return new MaterialItem(this, qty);
    }

    @Override
    public void use(Player p) {
        // Materials have no use yet
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
