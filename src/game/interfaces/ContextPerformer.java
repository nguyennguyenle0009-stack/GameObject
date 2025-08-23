package game.interfaces;

import game.entity.Player;
import game.entity.inventory.Inventory;

@FunctionalInterface
public interface ContextPerformer {
	void run(Player P, Inventory in, int index);
}
