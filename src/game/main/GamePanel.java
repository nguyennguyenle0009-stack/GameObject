package game.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;

import game.check.CollisionChecker;
import game.entity.Entity;
import game.entity.Player;
import game.entity.item.elixir.HealthPotion;
import game.keyhandler.KeyHandler;
import game.mouseclick.MouseHandler;
import game.object.ObjectManager;
import game.object.SuperObject;
import game.render.Renderer;
import game.tile.TileManager;
import game.ui.Ui;

public class GamePanel extends JPanel implements Runnable {
        private static final long serialVersionUID = 1L;

        // --- BASIC CONFIGURATION ---
        /** original pixel size of a tile */
        private final int originalTileSize = 16;
        /** scale factor applied to each tile */
        private final int scale = 3;
        /** actual size of a tile in the game world */
        private final int tileSize = originalTileSize * scale; // 48
        /** number of columns on the visible screen */
        private final int maxScreenCol = 16;
        /** number of rows on the visible screen */
        private final int maxScreenRow = 12;
        /** total width of the visible screen */
        private final int screenWidth = tileSize * maxScreenCol; // 768
        /** total height of the visible screen */
        private final int screenHeight = tileSize * maxScreenRow; // 576
        /** total columns in the world map */
        private final int maxWorldCol = 50;
        /** total rows in the world map */
        private final int maxWorldRow = 50;
        /** width of the entire world in pixels */
        private final int worldWidth = tileSize * maxWorldCol; // 2400
        /** height of the entire world in pixels */
        private final int worldHeight = tileSize * maxWorldRow;

        /** game loop thread */
        private Thread thread;
        /** target frames per second */
        private int FPS = 60;

        // --- INPUT HANDLERS ---
        /** keyboard input handler */
        public KeyHandler keyH = new KeyHandler(this);
        /** mouse input handler */
        MouseHandler mounseH = new MouseHandler(this);

        // --- GAME ENTITIES & SYSTEMS ---
        /** the player character */
        private final Player player = new Player(this);
        /** tile manager for drawing tiles */
        private final TileManager tileManager = new TileManager(this);
        /** collision checker */
        private final CollisionChecker checkCollision = new CollisionChecker(this);
        /** list of static objects in the world */
        private final List<SuperObject> objects = new ArrayList<>();
        /** list of NPC entities */
        private final List<Entity> npcs = new ArrayList<>();
        /** helper to spawn objects and entities */
        private final ObjectManager objectManager = new ObjectManager(this);
        /** user interface manager */
        private final Ui ui = new Ui(this);
        /** renderer responsible for all drawing */
        private final Renderer renderer = new Renderer(this);

        // --- GAME STATE ---
        /** current game state */
        private int gameState;
        /** id of play state */
        private final int playState = 1;
        /** id of pause state */
        private final int pauseState = 2;
        /** id of dialogue state */
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
		player.getBag().add(new HealthPotion(30, 50));
		player.getBag().add(new HealthPotion(30, 60));  // 50+60 => 100 trong ô đầu + 10 sang ô mới
		player.getBag().add(new HealthPotion(50, 1));
		player.getBag().add(new HealthPotion(50, 1));
		player.getBag().add(new HealthPotion(50, 1));
		player.getBag().add(new HealthPotion(30, 60));
		player.getBag().add(new HealthPotion(390, 60));
		
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
	
        /**
         * Delegate drawing to the {@link Renderer} so this class focuses on game logic.
         */
        public void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderer.render((Graphics2D) g);
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
















