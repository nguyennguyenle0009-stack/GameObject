package game.entity.item.elixir;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;

/**
 * Đan dược giúp tăng chỉ số SPIRIT để thử nghiệm hệ thống lên cấp.
 */
public class SpiritPotion extends Item {
    private static BufferedImage icon;
    private final int spiritAmount;

    static {
        try {
            // Tạm dùng lại hình ảnh của HealthPotion
            icon = ImageIO.read(SpiritPotion.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public SpiritPotion(int amount, int quantity) {
        super("Đan dược tinh thần", "Tăng SPIRIT", quantity, 100);
        this.spiritAmount = amount;
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
