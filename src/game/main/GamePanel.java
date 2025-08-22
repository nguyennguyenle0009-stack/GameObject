package game.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JPanel;

import game.check.CollisionChecker;
import game.entity.Entity;
import game.entity.Player;
import game.entity.item.elixir.HealthPotion;
import game.interfaces.DrawableEntity;
import game.keyhandler.KeyHandler;
import game.mouseclick.MouseHandler;
import game.object.ObjectManager;
import game.object.SuperObject;
import game.tile.TileManager;
import game.ui.Ui;

public class GamePanel extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	private final int originalTileSize = 16;
	private final int scale = 3;
	//Độ dài tile
	private final int tileSize = originalTileSize * scale;//48
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
	public KeyHandler keyH = new KeyHandler(this);
	MouseHandler mounseH = new MouseHandler(this);
	private final Player player = new Player(this);
	private final TileManager tileManager = new TileManager(this);
	private final CollisionChecker checkCollision = new CollisionChecker(this);
	private final List<SuperObject> objects = new ArrayList<>();
	private final List<Entity> npcs = new ArrayList<>();
    private final ObjectManager objectManager = new ObjectManager(this);
    private final Ui ui = new Ui(this);
    
    // GAME STATE
    private int gameState;
    private final int playState = 1;
    private final int pauseState = 2;
    private final int dialogueState = 3;
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.white);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mounseH);
		this.setFocusable(true);
	}
	
	public void setUpGame() { 
		player.getBag().add(new HealthPotion(30, 3));
		player.getBag().add(new HealthPotion(30, 3));
		player.getBag().add(new HealthPotion(50, 1));
		player.getBag().add(new HealthPotion(30, 3));
		player.getBag().add(new HealthPotion(30, 3));
		player.getBag().add(new HealthPotion(50, 1));
		player.getBag().add(new HealthPotion(30, 3));
		player.getBag().add(new HealthPotion(30, 3));
		player.getBag().add(new HealthPotion(50, 1));
		player.getBag().add(new HealthPotion(30, 3));
		player.getBag().add(new HealthPotion(30, 3));
		player.getBag().add(new HealthPotion(50, 1));
		objectManager.setObject();
		objectManager.setEntity();
		gameState = playState;
	}
	
	public void startGame() { this.thread = new Thread(this); thread.start(); }
	
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
		if(gameState == playState) {
		    player.update();
		    for (Entity npc : npcs) {
		        npc.update();
		    }
		}
		if(gameState == pauseState) {}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		long drawStart = 0;
		if(keyH.isCheckDrawTime() == true) {
			drawStart = System.nanoTime();
		}
		Graphics2D g2 = (Graphics2D)g;
		tileManager.draw(g2);
		//Check object
		List<DrawableEntity> drawList = new ArrayList<>();

		drawList.addAll(objects);
		drawList.addAll(npcs);
		drawList.add(player);

		drawList.sort(Comparator.comparingInt(DrawableEntity::getFootY));

		for (DrawableEntity entity : drawList) {
		    entity.draw(g2, this);
		}
		getUi().draw(g2);
		if(keyH.isCheckDrawTime() == true) {
			long drawEnd = System.nanoTime();
			long passedTime = drawEnd - drawStart;
			g2.setColor(Color.white);
			g2.drawString("Draw Timn: " + passedTime, 10, 400);
			System.out.println("Draw Timn: " + passedTime);
		}
		g2.dispose();
	}
	
    public int getTileSize() { return tileSize; }
	public int getMaxScreenCol() { return maxScreenCol; }
	public int getMaxScreenRow() { return maxScreenRow; }
	public int getScreenWidth() { return screenWidth; }
	public int getScreenHeight() { return screenHeight; }
	public int getMaxWorldCol() { return maxWorldCol; }
	public int getMaxWorldRow() { return maxWorldRow; }
	public int getWorldWidth() { return worldWidth; }
	public int getWorldHeight() { return worldHeight; }
	public Player getPlayer() { return player; }
	public TileManager getTileManager() { return tileManager; }
	public CollisionChecker getCheckCollision() { return checkCollision; }
	public ObjectManager getObjectManager() { return objectManager; } 
	public List<SuperObject> getObjects() { return objects; }
	public List<Entity> getNpcs() { return npcs; }

	public int getGameState() { return gameState; }
	public GamePanel setGameState(int gameState) { this.gameState = gameState; return this; }
	public int getPlayState() { return playState; }
	public int getPauseState() { return pauseState; }
	public int getDialogueState() { return dialogueState; }
	public Ui getUi() { return ui; }
	
}
















