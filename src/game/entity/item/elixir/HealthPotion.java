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
		p.atts().add(Attr.HEALTH, healthAmount);
		decreaseQuantity(1);
		System.out.println(p.getName() + " đã sử dụng " + getName());
	}

	@Override
	public BufferedImage getIcon() {
		return icon;
	}

}
