package game.entity.item.elixir;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;
import game.entity.level.LevelState;
import game.enums.Attr;

/**
 * Đan dược tăng vĩnh viễn một chỉ số.
 */
public class StatBoostPill extends Item {
    /** Thuộc tính được tăng */
    private final Attr attr;
    /** Lượng tăng */
    private final int amount;
    /** Hình ảnh icon sử dụng lại từ tài nguyên có sẵn */
    private static BufferedImage icon;

    static {
        try {
            icon = ImageIO.read(StatBoostPill.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StatBoostPill(Attr attr, int amount, int quantity) {
        super("Đan tăng " + attr.displayerName(), "Tăng vĩnh viễn " + attr.displayerName(), quantity, 99);
        this.attr = attr;
        this.amount = amount;
    }

    @Override
    public void use(Player p) {
        LevelState lvl = p.getLevel();
        if (!lvl.consumePill(p)) return; // vượt giới hạn thì không dùng được
        p.atts().add(attr, amount);
        decreaseQuantity(1);
    }

    @Override
    public Item copyWithQuantity(int qty) {
        return new StatBoostPill(attr, amount, qty);
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
