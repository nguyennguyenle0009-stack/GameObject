package game.entity.item.elixir;

import game.entity.Player;
import game.entity.item.Item;
import game.enums.Attr;

public class HealthPotion extends Item {
	
	private final int healthAmount;

	public HealthPotion(int healthAmount, int quantity) {
		super(
				"Đan dược hồi máu", 
				"Dùng để hồi máu", 
				quantity);
		this.healthAmount = healthAmount;
	}

	@Override
	public void use(Player p) {
		p.atts().add(Attr.HEALTH, healthAmount);
		decreaseQuantity(1);
		System.out.println(p.getName() + " đã sử dụng " + getName());
	}

}
