package game.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private final List<Item> items = new ArrayList<>();
    private int capacity = 20;

    public Inventory() {}

    public Inventory(int capacity) {
        this.capacity = Math.max(0, capacity);
    }

    public boolean add(Item item) {
        if (item == null || items.size() >= capacity) return false;
        items.add(item);
        return true;
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public List<Item> view() {
        return Collections.unmodifiableList(items);
    }

    public int getCapacity() { return capacity; }
    public Inventory setCapacity(int capacity) { this.capacity = Math.max(0, capacity); return this; }
    public boolean isFull() { return items.size() >= capacity; }
}

