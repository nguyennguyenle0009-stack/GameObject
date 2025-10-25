package game.tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.main.GamePanel;
import game.util.UtilityTool;

public class TileManager {
	private final GamePanel gp;
	private final Tile[] tile;
	// Mảng lưu số hiệu của từng tile trong bản đồ
	private final int [][] mapTileNumber;
	
	// Contructor
	public TileManager(GamePanel gp) {
		this.gp = gp;
		this.tile = new Tile[30];
		this.mapTileNumber = new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
		
		getTileImage();
		loadMap("/data/map/world01.txt");
	}
	
	public void getTileImage() {
		
		setup(0, "aa6", true);
		setup(1, "aa", false);
		setup(2, "aa1", false);
		setup(3, "aa2", false);
		setup(4, "aa3", false);
		setup(5, "aa5", true);
		setup(6, "aaa", false);
		setup(7, "aaa1", false);
		setup(8, "aaa2", false);
		setup(9, "aaa6", false);
		
		setup(10, "aa6", true);
		setup(11, "aa", false);
		setup(12, "aa1", false);
		setup(13, "aa2", false);
		setup(14, "aa3", false);
		setup(15, "aaa5", false);
		setup(16, "aaa", false);
		setup(17, "aaa1", false);
		setup(18, "aaa2", false);
		setup(19, "aaa6", false);
		setup(20, "aaa4", false);
		setup(21, "aaa3", false);
	}
	
    public void setup(int index, String imageName, boolean collision) {
        try {
            tile[index] = new Tile();
            tile[index].setImage(
        		ImageIO.read(
        				Objects.requireNonNull(
    						getClass()
    						.getResourceAsStream("/data/tile/" + imageName + ".png"))));
            tile[index].setImage(
            		UtilityTool.scaleImage(
            				tile[index].getImage(), gp.getTileSize(), gp.getTileSize()));
            tile[index].setCollision(collision);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    public void loadMap(String mapPath) {
        try (InputStream inputStream = getClass().getResourceAsStream(mapPath);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            int col = 0;
            int row = 0;
            while (col < gp.getMaxWorldCol() && row < gp.getMaxWorldRow()) {
                String line = bufferedReader.readLine();
                String[] numbers = line.split(" ");
                while (col < gp.getMaxWorldCol()) {
                    int number = Integer.parseInt(numbers[col]);
                    mapTileNumber[col][row] = number;
                    col++;
                }
                if (col == gp.getMaxWorldCol()) {
                    col = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
   public void draw(Graphics2D graphics2D) {
           // Thứ tự của tile trên hàng và cột trong thế giới
        int worldCol = 0;
        int worldRow = 0;
        int tileSize = gp.getTileSize();
        int screenWidth = gp.getScreenWidth();
        int screenHeight = gp.getScreenHeight();
        int worldWidth = gp.getWorldWidth();
        int worldHeight = gp.getWorldHeight();
        var player = gp.getPlayer();
        int playerWorldX = player.getWorldX();
        int playerWorldY = player.getWorldY();
        int playerScreenX = player.getScreenX();
        int playerScreenY = player.getScreenY();
        while(worldCol < gp.getMaxWorldCol() && worldRow < gp.getMaxWorldRow()) {
                int tileNumber = mapTileNumber[worldCol][worldRow];
                // Vị trí thực tế của tile trong thế giới
                int worldX = worldCol * tileSize;
                int worldY = worldRow * tileSize;
                // Vị trí tile trên màn hình, tính theo vị trí người chơi
            int screenX = worldX - playerWorldX + playerScreenX;
            int screenY = worldY - playerWorldY + playerScreenY;
            int rightOffset = screenWidth - playerScreenX;
            screenX = checkIfAtEdgeOfXAxis(worldX, screenX, rightOffset, playerWorldX, playerScreenX, worldWidth, screenWidth);
            int bottomOffset = screenHeight - playerScreenY;
            screenY = checkIfAtEdgeOfYAxis(worldY, screenY, bottomOffset, playerWorldY, playerScreenY, worldHeight, screenHeight);
            // Tối ưu hiệu suất, bản đồ không được vẽ khi ngoài màn hình
            if(UtilityTool.isInsidePlayerView(worldX, worldY, gp)) {
                graphics2D.drawImage(tile[tileNumber].getImage(), screenX, screenY, null);
            }
            else if (playerScreenX > playerWorldX ||
                         playerScreenY > playerWorldY ||
                         rightOffset > worldWidth - playerWorldX ||
                     bottomOffset > worldHeight - playerWorldY) {
                graphics2D.drawImage(tile[tileNumber].getImage(), screenX, screenY, null);
            }
                        if(gp.keyH.isDrawRect()) {
                graphics2D.setColor(Color.BLUE);
                graphics2D.drawRect(screenX, screenY, tileSize, tileSize);
                        }
            worldCol++;
            if(worldCol == gp.getMaxWorldCol()) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
   
   private int checkIfAtEdgeOfXAxis(int worldX, int screenX, int rightOffSet,
                                   int playerWorldX, int playerScreenX,
                                   int worldWidth, int screenWidth) {
           if(playerScreenX > playerWorldX) {
                   return worldX;
           }
           if(rightOffSet > worldWidth - playerWorldX) {
                   return screenWidth - (worldWidth - worldX);
           }
           return screenX;
   }

   private int checkIfAtEdgeOfYAxis(int worldY, int screenY, int botOffSet,
                                   int playerWorldY, int playerScreenY,
                                   int worldHeight, int screenHeight) {
           if(playerScreenY > playerWorldY) {
                   return worldY;
           }
           if(botOffSet > worldHeight - playerWorldY) {
                   return screenHeight - (worldHeight - worldY);
           }
           return screenY;
   }
   
   public Tile[] getTile() { return tile; }
   public int[][] getMapTileNumber() { return mapTileNumber; }
}




















