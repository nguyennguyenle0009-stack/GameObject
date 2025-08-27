package demo;

import game.enums.Attr;
import game.enums.EquipSlot;
import game.enums.EquipType;
import game.entity.Player;
import game.entity.item.EquipmentItem;
import game.entity.item.elixir.HealthPotion;
import game.main.GamePanel;

/** Simple test to verify equipping items doesn't restore health. */
public class EquipmentHealthTest {
    public static void main(String[] args) {
        GamePanel gp = new GamePanel();
        Player p = gp.getPlayer();

        int max = p.atts().getMax(Attr.HEALTH);
        p.takeDamage(40);
        int afterDamage = p.atts().get(Attr.HEALTH);

        EquipmentItem armor = new EquipmentItem("Áo giáp", "+3 DEF", "/data/item/equipment/armor.png", EquipType.ARMOR);
        p.equip(armor);
        int afterEquip = p.atts().get(Attr.HEALTH);

        p.unequip(EquipSlot.ARMOR);
        int afterUnequip = p.atts().get(Attr.HEALTH);

        HealthPotion pot = new HealthPotion(50, 1);
        pot.use(p);
        int afterPotion = p.atts().get(Attr.HEALTH);

        System.out.println("Max HP: " + max);
        System.out.println("HP after damage: " + afterDamage);
        System.out.println("HP after equip: " + afterEquip);
        System.out.println("HP after unequip: " + afterUnequip);
        System.out.println("HP after potion: " + afterPotion);
        System.out.println("Equip keeps HP: " + (afterEquip == afterDamage));
        System.out.println("Unequip keeps HP: " + (afterUnequip == afterEquip));
        System.out.println("Potion restores to max: " + (afterPotion == max));
    }
}
