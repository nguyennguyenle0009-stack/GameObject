package game.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import game.keyhandler.KeyHandler;

public class GamePanel extends JPanel implements Runnable {

	public final int originalTileSize = 16;
	public final int scale = 3;
	
	public final int tileSize = originalTileSize * scale;
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 12;
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;
	
	private Thread thread;
	
	private int FPS = 60;
	
	//Đặt vị trí mặc định người chơi
	private int playerX = 100;
	private int playerY = 100;
	private int speed = 4;
	
	KeyHandler keyH = new KeyHandler();
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
	}
	
	public void startGame() {
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		while(thread != null) {
            // 1: Update information, such as character position
            update();

            // 2: Draw the screen with updated information
            repaint(); // Calls the paintComponent() method
		}
	}
	
	public void update() {
		if(keyH.upPressed == true) {
			playerY -= speed;
		}
		if(keyH.downPressed == true) {
			playerY += speed;
		}
		if(keyH.leftPressed == true) {
			playerX -= speed;
		}
		if(keyH.rightPressed == true) {
			playerX += speed;
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		g2.fillRect(playerX, playerY, tileSize, tileSize);
		g2.dispose();
	}
	
}
