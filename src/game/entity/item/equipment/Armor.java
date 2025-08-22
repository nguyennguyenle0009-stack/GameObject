package game.entity.item.equipment;

import java.awt.image.BufferedImage;
import java.util.List;

import game.entity.Player;
import game.entity.item.Item;
import game.entity.item.ItemAction;

public class Armor extends Item {
    public Armor(String name) {
        super(name, "Giáp dùng để mặc", 1, 1);
    }

    @Override
    public void use(Player p) {
        System.out.println(p.getName() + " đã mặc " + getName());
    }

    @Override
    public List<ItemAction> actions() {
        return List.of(ItemAction.WEAR, ItemAction.DROP);
    }

    @Override
    public void perform(Player p, ItemAction action) {
        if (action == ItemAction.WEAR) {
            use(p);
            p.getBag().remove(this);
        } else {
            super.perform(p, action);
        }
    }

    @Override
    public BufferedImage getIcon() {
        return null;
    }
}
