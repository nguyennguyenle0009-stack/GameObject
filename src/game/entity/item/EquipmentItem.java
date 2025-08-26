package game.entity.item;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;

import game.entity.Player;
import game.enums.EquipType;

/** Basic equipment item that can be equipped in a slot. */
public class EquipmentItem extends Item {
    private final EquipType type;
    private final String iconPath;
    private final BufferedImage icon;
    /** Unique identifier used when saving/loading equipment. */
    private final String id;

    /**
     * Create new equipment with a random unique identifier.
     */
    public EquipmentItem(String name, String desc, String iconPath, EquipType type) {
        this(UUID.randomUUID().toString(), name, desc, iconPath, type);
    }

    /**
     * Create equipment with explicit identifier (used when loading from file).
     */
    public EquipmentItem(String id, String name, String desc, String iconPath, EquipType type) {
        super(name, desc, 1, 1);
        this.type = type;
        this.iconPath = iconPath;
        this.id = id;
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

    public EquipType getType() {
        return type;
    }

    /**
     * @return unique identifier of this equipment instance.
     */
    public String getId() {
        return id;
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
        // Equipment is non-stackable; return a new copy with a new identifier.
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
