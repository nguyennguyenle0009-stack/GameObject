package game.entity.item;

import game.entity.Player;

public abstract class Item {
	private final String name;
	private final String decription;
	private int quantity;
	
	public Item(String name, String decription, int quantity) {
		this.name = name;
		this.decription = decription;
		this.quantity = quantity;
	}
	
	public void decreaseQuantity(int amount) {
		quantity = Math.max(0,quantity - amount);
	}
	
	// Mỗi item định nghĩa cách dùng riêng
	public abstract void use(Player p);
	
	public String getName() { return name; }
	public String getDecription() { return decription; }
	public int getQuantity() { return quantity; }
}
