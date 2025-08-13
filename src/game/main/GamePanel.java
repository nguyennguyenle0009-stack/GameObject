package game.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import game.entity.Entity;
import game.entity.Player;
import game.keyhandler.KeyHandler;
import game.mouseclick.MouseHandler;
import game.tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	
	public final int originalTileSize = 16;
	public final int scale = 3;
	
	public final int tileSize = originalTileSize * scale;
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 12;
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;
	
	private Thread thread;
	
	private int FPS = 60;
	
	KeyHandler keyH = new KeyHandler(this);
	MouseHandler mounseH = new MouseHandler(this);
	Entity player = new Player(this, keyH, mounseH);
	TileManager tile = new TileManager(this);
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.white);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mounseH);
		this.setFocusable(true);
	}
	
	public void setUpGame() {
		
	}
	
	public void startGame() {
		this.thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		double drawInterval = 1000000000/FPS;
		double detal = 0;
		double lastTime = System.nanoTime();
		long currenTime;
		long timer = 0;
		int drawCount = 0;
		while(thread != null) {
			currenTime = System.nanoTime();
			detal += (currenTime - lastTime)/drawInterval;
			timer += (currenTime - lastTime);
			lastTime = currenTime;
			if(detal >= 1) {
	            update();
	            repaint(); 
	            detal--;
	            drawCount++;
			}
			if(timer >= 1000000000) {
				System.out.println("FPS: " + drawCount);
				timer = 0;
				drawCount = 0;
			}
		}
	}
	
	public void update() {
		player.update();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		tile.draw(g2);
		player.draw(g2);
		g2.dispose();
	}
	
}
















