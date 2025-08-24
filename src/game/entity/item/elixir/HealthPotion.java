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
        // Không giới hạn máu ở 100, chỉ cộng thêm để tránh việc reset về 100
        // Khi lên cấp tối đa máu sẽ được lưu lại trong thuộc tính
        p.atts().add(Attr.HEALTH, healthAmount);
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
