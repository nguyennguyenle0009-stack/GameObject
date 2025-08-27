package game.entity.item;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;

import game.entity.Player;
import game.enums.Attr;
import game.enums.EquipType;

/** Basic equipment item that can be equipped in a slot. */
public class EquipmentItem extends Item {
    private final EquipType type;
    private final String iconPath;
    private final BufferedImage icon;
    /** Unique identifier for this equipment. */
    private final String id;
    /** Stat bonuses granted while equipped. */
    private final EnumMap<Attr, Integer> bonuses = new EnumMap<>(Attr.class);

    /**
     * Create equipment with an auto-generated unique id.
     */
    public EquipmentItem(String name, String desc, String iconPath, EquipType type) {
        this(type.name() + "#" + UUID.randomUUID().toString(), name, desc, iconPath, type, new EnumMap<>(Attr.class));
    }

    /**
     * Create equipment with an auto-generated id and bonuses.
     */
    public EquipmentItem(String name, String desc, String iconPath, EquipType type, EnumMap<Attr, Integer> bonusMap) {
        this(type.name() + "#" + UUID.randomUUID().toString(), name, desc, iconPath, type, bonusMap);
    }

    /**
     * Create equipment with a specified id.
     */
    public EquipmentItem(String id, String name, String desc, String iconPath, EquipType type) {
        this(id, name, desc, iconPath, type, new EnumMap<>(Attr.class));
    }

    /**
     * Full constructor allowing stat bonuses to be supplied.
     */
    public EquipmentItem(String id, String name, String desc, String iconPath, EquipType type, EnumMap<Attr, Integer> bonusMap) {
        super(name, desc, 1, 1);
        this.type = type;
        this.iconPath = iconPath;
        this.id = id;
        if (bonusMap != null) {
            bonuses.putAll(bonusMap);
        }
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

    public String getId() {
        return id;
    }

    public String getIconPath() {
        return iconPath;
    }

    /** @return immutable view of the stat bonuses. */
    public Map<Attr, Integer> getBonuses() { return Collections.unmodifiableMap(bonuses); }

    /** Add a stat bonus to this item. */
    public void addBonus(Attr attr, int value) { bonuses.put(attr, value); }

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
        // Equipment is non-stackable; return identical copy with same bonuses.
        return new EquipmentItem(getId(), getName(), getDecription(), iconPath, type, new EnumMap<>(bonuses));
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

