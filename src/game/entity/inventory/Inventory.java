package game.entity.inventory;

import java.util.ArrayList;
import java.util.List;

import game.entity.item.Item;

public class Inventory {
	private final List<Item> items = new ArrayList<Item>();
	
	public void add(Item i) {
		items.add(i);
	}
	public boolean remove(Item i) {
		return items.remove(i);
	}
	public List<Item> all(){
		return List.copyOf(items);
	}
}
