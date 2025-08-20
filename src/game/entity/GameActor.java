package game.entity;

import java.awt.Graphics2D;

import game.entity.nguyen.Attributes;
import game.interfaces.HasAttributes;
import game.main.GamePanel;

public class GameActor extends Entity implements HasAttributes {
	
	private final Attributes atts = new Attributes();

	public GameActor(GamePanel gp) {
		super(gp);
	}


	@Override
	public Attributes atts() {

		return atts;
	}

	@Override
	public void update() { }
	@Override
	public void draw(Graphics2D g2) { }
	@Override
	public void draw(Graphics2D g2, GamePanel gp) { }
	public Attributes getAttr() { return atts; }
	
}
















