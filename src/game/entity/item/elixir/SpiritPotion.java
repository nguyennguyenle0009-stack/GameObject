package game.entity.item.elixir;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;

/**
 * Item dùng để tăng Spirit cho nhân vật, nhằm kiểm tra lên cấp.
 */
public class SpiritPotion extends Item {
    private static BufferedImage icon;
    private final int spiritAmount;

    static {
        try {
            // Tạm dùng chung icon với bình máu
            icon = ImageIO.read(SpiritPotion.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SpiritPotion(int spiritAmount, int quantity) {
        super("Tinh linh đan", "Tăng Spirit", quantity, 100);
        this.spiritAmount = spiritAmount;
    }

    @Override
    public void use(Player p) {
        // cộng Spirit và kiểm tra lên cấp
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
