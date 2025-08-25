package game.entity.inventory;

import java.util.ArrayList;
import java.util.List;

import game.entity.item.Item;

public class Inventory {
	private final List<Item> items = new ArrayList<Item>();
	
    // Thêm item có cộng dồn + tràn ô (stacking)
    public void add(Item incoming) {
        if (incoming == null || incoming.getQuantity() <= 0) return;

        int remain = incoming.getQuantity();

        // 1) Đổ vào các stack sẵn có còn chỗ
        for (Item it : items) {
            if (!it.isSameStack(incoming)) continue;
            int space = it.getMaxStack() - it.getQuantity();
            if (space <= 0) continue;
            int moved = Math.min(space, remain);
            it.increaseQuantity(moved);
            remain -= moved;
            if (remain == 0) return;
        }

        // Tạo thêm các stack mới cho phần còn lại (chia theo maxStack)
        while (remain > 0) {
            int chunk = Math.min(incoming.getMaxStack(), remain);
            Item piece = incoming.copyWithQuantity(chunk);
            items.add(piece);
            remain -= chunk;
        }
    }
    
        public boolean remove(Item i) {
                return items.remove(i);
        }
        public List<Item> all(){
                return List.copyOf(items);
        }

    public void clear() {
        items.clear();
    }
}