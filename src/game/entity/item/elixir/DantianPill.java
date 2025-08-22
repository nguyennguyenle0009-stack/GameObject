package game.entity.item.elixir;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;

// Đan dược dùng để khai mở đan điền
public class DantianPill extends Item {
    // Hình ảnh của đan dược
    private static BufferedImage icon;

    static {
        try {
            icon = ImageIO.read(DantianPill.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Khởi tạo với số lượng ban đầu
    public DantianPill(int quantity) {
        super("Đan khai mở đan điền", "Dùng để khai mở đan điền", quantity, 10);
    }

    // Sử dụng đan dược
    @Override
    public void use(Player p) {
        p.getLevel().openDantian();
        // BUG: quên giảm số lượng nên có thể dùng vô hạn
    }

    // Trả về icon
    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
