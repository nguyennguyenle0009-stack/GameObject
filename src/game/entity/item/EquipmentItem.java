package game.entity.item;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import game.entity.Player;
import game.enums.EquipType;

/** Basic equipment item that can be equipped in a slot. */
public class EquipmentItem extends Item {
    private static final AtomicInteger ID_GEN = new AtomicInteger();

    private final EquipType type;
    private final String iconPath;
    private final BufferedImage icon;
    private final String id;

    public EquipmentItem(String name, String desc, String iconPath, EquipType type) {
        this(null, name, desc, iconPath, type);
    }

    public EquipmentItem(String id, String name, String desc, String iconPath, EquipType type) {
        super(name, desc, 1, 1);
        this.type = type;
        this.iconPath = iconPath;
        this.id = (id != null) ? id : generateId(type);
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

    private static String generateId(EquipType type) {
        return type.name() + String.format("#%07d", ID_GEN.incrementAndGet());
    }

    public EquipType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getIconPath() {
        return iconPath;
    }

    @Override
    public void use(Player p) {
        var prev = p.equip(this);
        if (prev != null) {
            p.getBag().add(prev);
        }
        p.getBag().remove(this);
    }

    @Override
    public Item copyWithQuantity(int qty) {
        // Equipment is non-stackable; return identical copy.
        return new EquipmentItem(getName(), getDecription(), iconPath, type);
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }

    @Override
    public String[] getActions() {
        return new String[] {"Equip", "Drop"};
    }

    @Override
    public void performAction(Player p, String action) {
        if ("Equip".equalsIgnoreCase(action)) {
            use(p);
        } else {
            super.performAction(p, action);
        }
    }
}
