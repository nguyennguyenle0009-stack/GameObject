package game.entity;

import java.awt.Graphics2D;

import game.main.GamePanel;

public abstract class Entity {
	
	public int x, y;
	public int speed;

	GamePanel gp;
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public Entity(GamePanel gp) {
		this.gp = gp;
	}
	
	
	public abstract  void  update();
	public abstract  void draw(Graphics2D g2);
}
