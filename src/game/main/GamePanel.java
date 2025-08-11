package game.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import game.enums.MoveMode;
import game.keyhandler.KeyHandler;
import game.mouseclick.MouseHandler;

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
	
	//Đặt vị trí mặc định người chơi
	private int playerX = 100;
	private int playerY = 100;
	private int speed = 4;

	
	KeyHandler keyH = new KeyHandler(this);
	MouseHandler mounseH = new MouseHandler(this);
	
	public MoveMode moveMode = MoveMode.PRESSED;
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mounseH);
		this.setFocusable(true);
	}
	
	public void setUpGame() {
		
	}
	
	public void startGame() {
		thread = new Thread(this);
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
		if(moveMode == MoveMode.KEYBOARD) {
			if(keyH.upPressed == true) { playerY -= speed; }
			if(keyH.downPressed == true) { playerY += speed; }
			if(keyH.leftPressed == true) { playerX -= speed; }
			if(keyH.rightPressed == true) { playerX += speed; }
		}
		else if(moveMode == MoveMode.PRESSED) {
			if(mounseH.moving == true) {
				float dx = mounseH.targetX - playerX;
				float dy = mounseH.targetY - playerY;
				float dist = (float)Math.sqrt(dx * dx + dy * dy);
				
		        if (dist > speed) {
		        	playerX += dx / dist * speed;
		        	playerY += dy / dist * speed;
		        } else {
		        	playerX = mounseH.targetX;
		        	playerY = mounseH.targetY;
		            mounseH.moving = false;
		        }
			}
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

//public void update() {
//    switch (moveMode) {
//        case KEYBOARD -> updateKeyboard();
//        case CLICK -> updateClickMove();
//    }
//}
//
//private void updateKeyboard() {
//    if (keyH.upPressed) playerY -= speed;
//    if (keyH.downPressed) playerY += speed;
//    if (keyH.leftPressed) playerX -= speed;
//    if (keyH.rightPressed) playerX += speed;
//}
//
//private void updateClickMove() {
//    if (!mouseH.moving) return;
//
//    float dx = mouseH.targetX - playerX;
//    float dy = mouseH.targetY - playerY;
//    float dist = (float) Math.sqrt(dx * dx + dy * dy);
//
//    if (dist > speed) {
//        playerX += dx / dist * speed;
//        playerY += dy / dist * speed;
//    } else {
//        playerX = mouseH.targetX;
//        playerY = mouseH.targetY;
//        mouseH.moving = false;
//    }
//}
