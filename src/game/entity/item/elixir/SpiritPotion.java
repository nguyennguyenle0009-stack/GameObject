package game.entity.item.elixir;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;

/**
 * Item dùng để tăng chỉ số Spirit (kinh nghiệm) cho người chơi.
 * Sử dụng hình ảnh bình thuốc có sẵn.
 */
public class SpiritPotion extends Item {

    private static BufferedImage icon;
    private final int spiritAmount;

    static {
        try {
            // Tái sử dụng icon của HealthPotion
            icon = ImageIO.read(SpiritPotion.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SpiritPotion(int spiritAmount, int quantity) {
        super("Đan dược tinh thần", "Tăng Spirit", quantity, 100);
        this.spiritAmount = spiritAmount;
    }

    @Override
    public void use(Player p) {
        // tăng Spirit và kiểm tra lên cấp
        p.gainSpirit(spiritAmount);
        decreaseQuantity(1);
    }

    @Override
    public Item copyWithQuantity(int qty) {
        return new SpiritPotion(spiritAmount, qty);
    }

    @Override
    public boolean isSameStack(Item other) {
        if (!(other instanceof SpiritPotion sp)) return false;
        return this.getName().equals(other.getName()) && this.spiritAmount == sp.spiritAmount;
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
