package game.entity.item.elixir;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;

/**
 * Simple elixir that increases the SPIRIT (experience) of the player.
 * Reuses the health potion icon for testing purposes.
 */
public class SpiritElixir extends Item {

    private static BufferedImage icon;
    private final int spiritAmount;

    static {
        try {
            icon = ImageIO.read(SpiritElixir.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SpiritElixir(int spiritAmount, int quantity) {
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
        return new SpiritElixir(spiritAmount, qty);
    }

    @Override
    public boolean isSameStack(Item other) {
        if (!(other instanceof SpiritElixir se)) return false;
        return this.getName().equals(other.getName()) && this.spiritAmount == se.spiritAmount;
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }
}
