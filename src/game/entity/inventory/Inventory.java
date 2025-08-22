package game.entity.inventory;

import java.util.ArrayList;
import java.util.List;

import game.entity.item.Item;

public class Inventory {
	private final List<Item> items = new ArrayList<Item>();
	
    public void add(Item newItem) {
        // Thử tìm item cùng loại (so sánh class và name)
        for (Item it : items) {
            if (it.getClass() == newItem.getClass() && it.getName().equals(newItem.getName())) {
                int space = it.getMaxStack() - it.getQuantity();
                if (space > 0) {
                    int transfer = Math.min(space, newItem.getQuantity());
                    it.increaseQuantity(space);
                    newItem.decreaseQuantity(transfer);
                }
            }
        }

        // Nếu vẫn còn quantity (chưa cộng hết) → thêm như một ô mới
        if (newItem.getQuantity() > 0) {
            items.add(newItem);
        }
    }
    
	public boolean remove(Item i) {
		return items.remove(i);
	}
	public List<Item> all(){
		return List.copyOf(items);
	}
}
