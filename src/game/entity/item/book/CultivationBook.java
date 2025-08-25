package game.entity.item.book;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;
import game.skill.CultivationTechnique;

/**
 * Sách dùng để học công pháp tu luyện.
 */
public class CultivationBook extends Item {
    private static BufferedImage icon;
    private final CultivationTechnique technique;

    static {
        try {
            // Tạm dùng icon bình thuốc làm placeholder
            icon = ImageIO.read(CultivationBook.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CultivationBook(CultivationTechnique tech) {
        super("Sách " + tech.getName(), "Học " + tech.getName(), 1, 1);
        this.technique = tech;
    }

    @Override
    public void use(Player p) {
        p.learnSkill(technique);
    }

    @Override
    public Item copyWithQuantity(int qty) {
        CultivationBook b = new CultivationBook(technique);
        b.setQuantity(qty);
        return b;
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
