package game.entity.item.elixir;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;
import game.enums.Attr;

public class HealthPotion extends Item {
	
	private static BufferedImage icon;
	private final int healthAmount;
	
	static {
		try {
			icon = ImageIO.read(HealthPotion.class.getResourceAsStream("/data/item/elixir/HealthPotion.png"));
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
	
	public HealthPotion(int healthAmount, int quantity) {
		super(
				"Đan dược hồi máu", 
				"Dùng để hồi máu", 
				quantity,
				100);
		this.healthAmount = healthAmount;
	}

	@Override
    public void use(Player p) {
        int current = p.atts().get(Attr.HEALTH);
        int max = p.atts().getMax(Attr.HEALTH);
        int newHealth = Math.min(current + healthAmount, max);
        p.atts().set(Attr.HEALTH, newHealth);
        decreaseQuantity(1);
}
	
   @Override
    public Item copyWithQuantity(int qty) {
        return new HealthPotion(healthAmount, qty);
    }
	
   @Override
    public boolean isSameStack(Item other) {
        if (!(other instanceof HealthPotion hp)) return false;
        return this.getName().equals(other.getName()) && this.healthAmount == hp.healthAmount;
    }

	@Override
	public BufferedImage getIcon() {
		return icon;
	}

}
