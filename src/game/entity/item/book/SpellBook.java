package game.entity.item.book;

import java.awt.image.BufferedImage;
import java.util.List;

import game.entity.Player;
import game.entity.item.Item;
import game.entity.item.ItemAction;

public class SpellBook extends Item {
    public SpellBook(String name) {
        super(name, "Sách dùng để học", 1, 1);
    }

    @Override
    public void use(Player p) {
        System.out.println(p.getName() + " đã học " + getName());
    }

    @Override
    public List<ItemAction> actions() {
        return List.of(ItemAction.READ, ItemAction.DROP);
    }

    @Override
    public void perform(Player p, ItemAction action) {
        if (action == ItemAction.READ) {
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
