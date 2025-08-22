package game.entity.item.elixir;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;
import game.enums.Attr;

// Đan dược tăng vĩnh viễn một thuộc tính
public class AttributePill extends Item {
    // Hình ảnh của đan dược
    private static BufferedImage icon;
    // Thuộc tính được tăng
    private final Attr attr;
    // Lượng tăng thêm
    private final int amount;

    static {
        try {
            icon = ImageIO.read(AttributePill.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Khởi tạo
    public AttributePill(String name, Attr attr, int amount, int quantity) {
        super(name, "Tăng " + attr.displayerName(), quantity, 40);
        this.attr = attr;
        this.amount = amount;
    }

    // Sử dụng đan dược
    @Override
    public void use(Player p) {
        if(p.getLevel().usePill()) {
            p.atts().add(attr, amount);
            decreaseQuantity(1);
        }
    }

    // Trả về icon
    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
