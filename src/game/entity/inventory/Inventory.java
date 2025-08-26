package game.entity.inventory;

import java.util.ArrayList;
import java.util.List;

import game.entity.item.Item;

public class Inventory {
        private final List<Item> items = new ArrayList<Item>();
    private int capacity = 30;

    /**
     * Thêm item có cộng dồn + kiểm tra giới hạn ô.
     */
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

        // 2) Tạo thêm các stack mới cho phần còn lại (chia theo maxStack)
        while (remain > 0 && items.size() < capacity) {
            int chunk = Math.min(incoming.getMaxStack(), remain);
            Item piece = incoming.copyWithQuantity(chunk);
            items.add(piece);
            remain -= chunk;
        }

        // Nếu vẫn còn dư thì kho đã đầy -> bỏ phần dư
    }

    /**
     * Kiểm tra xem item có thể thêm hoàn toàn vào kho hay không.
     *
     * @param incoming item cần kiểm tra
     * @return true nếu toàn bộ item có thể thêm, ngược lại false
     */
    public boolean canAdd(Item incoming) {
        if (incoming == null || incoming.getQuantity() <= 0) return true;

        int remain = incoming.getQuantity();

        // 1) Tính phần có thể gộp vào các stack hiện có
        for (Item it : items) {
            if (!it.isSameStack(incoming)) continue;
            int space = it.getMaxStack() - it.getQuantity();
            if (space <= 0) continue;
            remain -= Math.min(space, remain);
            if (remain == 0) return true;
        }

        // 2) Kiểm tra số ô trống còn lại đủ chứa phần dư hay không
        int freeSlots = capacity - items.size();
        int neededSlots = (int) Math.ceil(remain / (double) incoming.getMaxStack());
        return freeSlots >= neededSlots;
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

    public int capacity() { return capacity; }
    public void increaseCapacity(int d) { capacity += d; }
    public void decreaseCapacity(int d) { capacity = Math.max(0, capacity - d); }
}