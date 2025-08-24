package game.entity.item.elixir;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;

/**
 * Đan dược tăng Spirit, dùng để kiểm tra hệ thống lên cấp.
 */
public class SpiritPotion extends Item {
    private static BufferedImage icon;
    private final int spiritAmount;

    static {
        try {
            icon = ImageIO.read(SpiritPotion.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SpiritPotion(int spiritAmount, int quantity) {
        super("Đan dược tinh thần", "Tăng SPIRIT", quantity, 100);
        this.spiritAmount = spiritAmount;
    }

    @Override
    public void use(Player p) {
        p.gainSpirit(spiritAmount);
        decreaseQuantity(1);
    }

    @Override
    public Item copyWithQuantity(int qty) {
        return new SpiritPotion(spiritAmount, qty);
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
