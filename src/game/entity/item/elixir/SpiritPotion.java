package game.entity.item.elixir;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;
import game.enums.Attr;

/**
 * Đan dược tăng SPIRIT dùng để kiểm tra việc lên cấp.
 * Sử dụng lại hình ảnh HealthPotion có sẵn.
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
        super("Đan dược tăng SPIRIT", "Tăng SPIRIT cho người chơi", quantity, 100);
        this.spiritAmount = spiritAmount;
    }

    @Override
    public void use(Player p) {
        // Sử dụng tiện ích gainSpirit để tự kiểm tra lên cấp
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
