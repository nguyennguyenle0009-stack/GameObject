package game.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import game.entity.Player;
import game.keyhandler.KeyHandler;
import game.mouseclick.MouseHandler;
import game.tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	
	private final int originalTileSize = 16;
	private final int scale = 3;
	
	//Độ dài tile
	private final int tileSize = originalTileSize * scale;
	//Tổng số cột hàng trong khung hình
	private final int maxScreenCol = 16;
	private final int maxScreenRow = 12;
	//Tổng số độ dài khung hình hiển thị
	private final int screenWidth = tileSize * maxScreenCol;//768
	private final int screenHeight = tileSize * maxScreenRow;//576
	
	// Tổng cột và hàng trong map
	private final int maxWorldCol = 50;
	private final int maxWorldRow = 50;
	// Tổng số chiều rộng và chiều cao trong map(pixel)
	private final int worldWidth = tileSize * maxWorldCol;//2400
	private final int worldHeight = tileSize * maxWorldRow;
	
	private Thread thread;
	
	private int FPS = 60;
	
	KeyHandler keyH = new KeyHandler(this);
	MouseHandler mounseH = new MouseHandler(this);
	private final Player player = new Player(this, keyH, mounseH);
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
	
    public int getTileSize() {
        return tileSize;
    }

	public int getMaxScreenCol() {
		return maxScreenCol;
	}

	public int getMaxScreenRow() {
		return maxScreenRow;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public int getMaxWorldCol() {
		return maxWorldCol;
	}

	public int getMaxWorldRow() {
		return maxWorldRow;
	}

	public int getWorldWidth() {
		return worldWidth;
	}

	public int getWorldHeight() {
		return worldHeight;
	}

	public Player getPlayer() {
		return player;
	}
	
	
}
















