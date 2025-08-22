package game.entity.item;

import java.awt.image.BufferedImage;
import java.util.List;

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
	
        // Mỗi item định nghĩa cách dùng riêng
        public abstract void use(Player p);

        // Danh sách hành động có thể thực hiện (mặc định: dùng hoặc vứt)
        public List<ItemAction> actions() {
                return List.of(ItemAction.USE, ItemAction.DROP);
        }

        // Thực thi hành động
        public void perform(Player p, ItemAction action) {
                switch (action) {
                case USE:
                        use(p);
                        if (getQuantity() <= 0) {
                                p.getBag().remove(this);
                        }
                        break;
                case DROP:
                        p.getBag().remove(this);
                        break;
                default:
                        break;
                }
        }

        // Mỗi item có hình ảnh riêng
        public abstract BufferedImage getIcon();
	
	public String getName() { return name; }
	public String getDecription() { return decription; }
	public int getQuantity() { return quantity; }
	public Item setQuantity(int quantity) { this.quantity = quantity; return this; }
	public int getMaxStack() { return maxStack; }
}
