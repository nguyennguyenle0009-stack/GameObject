package game.entity.item;

import java.awt.image.BufferedImage;
import java.io.IOException;
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
    /** Stat bonuses provided by this equipment. */
    private final EnumMap<Attr, Integer> bonuses = new EnumMap<>(Attr.class);

    /**
     * Create equipment with an auto-generated unique id.
     */
    public EquipmentItem(String name, String desc, String iconPath, EquipType type) {
        this(type.name() + "#" + UUID.randomUUID().toString(), name, desc, iconPath, type, null);
    }

    /** Create equipment with auto-generated id and bonuses. */
    public EquipmentItem(String name, String desc, String iconPath, EquipType type,
            Map<Attr, Integer> bonusMap) {
        this(type.name() + "#" + UUID.randomUUID().toString(), name, desc, iconPath, type, bonusMap);
    }

    /**
     * Create equipment with a specified id.
     */
    public EquipmentItem(String id, String name, String desc, String iconPath, EquipType type) {
        this(id, name, desc, iconPath, type, null);
    }

    /**
     * Create equipment with explicit bonuses.
     */
    public EquipmentItem(String id, String name, String desc, String iconPath,
            EquipType type, Map<Attr, Integer> bonusMap) {
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
        if (bonusMap != null) {
            bonuses.putAll(bonusMap);
        }
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

      /** Returns stat bonuses of this equipment. */
      public EnumMap<Attr, Integer> getBonuses() { return bonuses; }
      /** Convenience method to read a single bonus. */
      public int getBonus(Attr attr) { return bonuses.getOrDefault(attr, 0); }

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
          return new EquipmentItem(getName(), getDecription(), iconPath, type, bonuses);
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
