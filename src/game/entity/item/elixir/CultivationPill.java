package game.entity.item.elixir;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;

/**
 * Đan dược tăng tốc độ tu luyện. Khi sử dụng sẽ tăng thêm SPIRIT mỗi giây
 * trong 10 phút.
 */
public class CultivationPill extends Item {
    private static BufferedImage icon;
    private final int spiritBonus;

    static {
        try {
            // Tạm dùng icon bình thuốc sẵn có
            icon = ImageIO.read(CultivationPill.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CultivationPill(String name, int spiritBonus, int quantity) {
        super(name, "Tăng tốc độ tu luyện", quantity, 100);
        this.spiritBonus = spiritBonus;
    }

    @Override
    public void use(Player p) {
        p.consumeCultivationPill(getName(), spiritBonus, 10 * 60 * 1000L);
        decreaseQuantity(1);
    }

    @Override
    public Item copyWithQuantity(int qty) {
        return new CultivationPill(getName(), spiritBonus, qty);
    }

    @Override
    public boolean isSameStack(Item other) {
        if (!(other instanceof CultivationPill cp)) return false;
        return getName().equals(cp.getName()) && spiritBonus == cp.spiritBonus;
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}