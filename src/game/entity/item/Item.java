package game.entity.item;

import java.awt.image.BufferedImage;

import game.entity.Player;

public abstract class Item {
	private final String name;
	private final String decription;
	private int quantity;
	private final int maxStack;
	
	public Item(String name, String decription, int quantity, int maxStack) {
		this.name = name;
		this.decription = decription;
		this.quantity = quantity;
		this.maxStack = maxStack;
	}
	
	public void increaseQuantity(int amount) {
		quantity = Math.min(amount + quantity, maxStack);
	}
	
	public void decreaseQuantity(int amount) {
		quantity = Math.max(0, quantity - amount);
	}
	
    // So sánh xem hai item có gộp chung 1 stack được không
    public boolean isSameStack(Item other) {
        // Mặc định: cùng class + cùng tên -> gộp
        return other != null
            && this.getClass() == other.getClass()
            && this.name.equals(other.name);
    }

    // Tạo bản sao cùng loại với số lượng chỉ định (để chia stack)
    public abstract Item copyWithQuantity(int qty);
	
	// Mỗi item định nghĩa cách dùng riêng
	public abstract void use(Player p);
	
	// Mỗi item có hình ảnh riêng
	public abstract BufferedImage getIcon();
	
	public String getName() { return name; }
	public String getDecription() { return decription; }
	public int getQuantity() { return quantity; }
	public Item setQuantity(int quantity) { this.quantity = quantity; return this; }
	public int getMaxStack() { return maxStack; }
}
