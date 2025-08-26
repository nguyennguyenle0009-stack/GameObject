package game.entity.item.equipment;

import java.awt.image.BufferedImage;

import game.entity.Player;
import game.entity.item.Item;
import game.enums.Attr;
import game.enums.EquipSlot;

/**
 * Item trang bị cơ bản với các thuộc tính cộng thêm.
 */
public abstract class Equipment extends Item {
    private final EquipSlot slot;
    private final int atkBonus;
    private final int defBonus;
    private final int soulBonus;
    private final int bagBonus;

    public Equipment(String name, String desc, EquipSlot slot,
                     int atk, int def, int soul, int bagBonus) {
        super(name, desc, 1, 1); // trang bị không cộng dồn
        this.slot = slot;
        this.atkBonus = atk;
        this.defBonus = def;
        this.soulBonus = soul;
        this.bagBonus = bagBonus;
    }

    public EquipSlot getSlot() { return slot; }

    public void onEquip(Player p) {
        if (atkBonus != 0) p.atts().add(Attr.ATTACK, atkBonus);
        if (defBonus != 0) p.atts().add(Attr.DEF, defBonus);
        if (soulBonus != 0) p.atts().add(Attr.SOULD, soulBonus);
        if (bagBonus != 0) p.getBag().setCapacity(p.getBag().getCapacity() + bagBonus);
    }

    public void onUnequip(Player p) {
        if (atkBonus != 0) p.atts().add(Attr.ATTACK, -atkBonus);
        if (defBonus != 0) p.atts().add(Attr.DEF, -defBonus);
        if (soulBonus != 0) p.atts().add(Attr.SOULD, -soulBonus);
        if (bagBonus != 0) p.getBag().setCapacity(Math.max(30, p.getBag().getCapacity() - bagBonus));
    }

    @Override
    public void use(Player p) {
        p.getBag().remove(this);
        p.equip(this);
    }

    @Override
    public String[] getActions() {
        return new String[] {"Use", "Drop"};
    }

    @Override
    public void performAction(Player p, String action) {
        if ("Use".equalsIgnoreCase(action)) {
            use(p);
        } else if ("Drop".equalsIgnoreCase(action)) {
            p.dropItem(this);
        }
    }

    @Override
    public abstract BufferedImage getIcon();

    @Override
    public Equipment copyWithQuantity(int qty) {
        return this; // không cộng dồn nên trả về chính nó
    }
}
