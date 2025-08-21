package demo;

import game.entity.Player;
import game.entity.item.Item;
import game.entity.item.elixir.HealthPotion;
import game.main.GamePanel;

public class H {
	static GamePanel gp;
	public static void main(String[] args) {
		Item hp = new HealthPotion(30, 1);
		Player p = new Player(gp);
		p.getBag().add(hp);
		p.useItem(hp);
	}
}
