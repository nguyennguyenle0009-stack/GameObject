package game.entity.item.elixir;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;
import game.entity.level.Physique;

/**
 * Đan dược khai mở đan điền.
 */
public class DantianPill extends Item {
    /** icon của đan dược (tái sử dụng hình có sẵn) */
    private static BufferedImage icon;

    static {
        try {
            icon = ImageIO.read(DantianPill.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DantianPill(int quantity) {
        super("Đan khai mở đan điền", "Mở đan điền và biết thể chất", quantity, 10);
    }

    @Override
    public void use(Player p) {
        // mặc định cho thể chất bình thường
        p.setPhysique(Physique.NORMAL);
        p.getLevel().openDantian(p);
        decreaseQuantity(1);
    }

    @Override
    public Item copyWithQuantity(int qty) {
        return new DantianPill(qty);
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
