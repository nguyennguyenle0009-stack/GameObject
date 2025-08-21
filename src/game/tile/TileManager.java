package game.tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.main.GamePanel;
import game.util.CameraHelper;
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
        try {
            InputStream inputStream = getClass().getResourceAsStream(mapPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            int col = 0;
            int row = 0;
            while (col < gp.getMaxWorldCol() && row < gp.getMaxWorldRow()) {
                String line = bufferedReader.readLine();
                while (col < gp.getMaxWorldCol()) {
                    String[] numbers = line.split(" ");
                    int number = Integer.parseInt(numbers[col]);
                    mapTileNumber[col][row] = number;
                    col++;
                }
                if (col == gp.getMaxWorldCol()) {
                    col = 0;
                    row++;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
   public void draw(Graphics2D graphics2D) {
           // Thứ tự của tile trên hàng và cột trong thế giới
        int worldCol = 0;
        int worldRow = 0;
        while(worldCol < gp.getMaxWorldCol() && worldRow < gp.getMaxWorldRow()) {
                int tileNumber = mapTileNumber[worldCol][worldRow];
                // Vị trí thực tế của tile trong thế giới
                int worldX = worldCol * gp.getTileSize();
                int worldY = worldRow * gp.getTileSize();
                Point screenPos = CameraHelper.worldToScreen(worldX, worldY, gp);
                // Tối ưu hiệu suất, bản đồ không được vẽ khi ngoài màn hình
                if(UtilityTool.isInsidePlayerView(worldX, worldY, gp)) {
                    graphics2D.drawImage(tile[tileNumber].getImage(), screenPos.x, screenPos.y, null);
                    if(gp.keyH.isDrawRect() == true) {
                        graphics2D.setColor(Color.BLUE);
                        graphics2D.drawRect(screenPos.x, screenPos.y, gp.getTileSize(), gp.getTileSize());
                    }
                }
                worldCol++;
                if(worldCol == gp.getMaxWorldCol()) {
                    worldCol = 0;
                    worldRow++;
                }
        }
    }
   
   public Tile[] getTile() { return tile; }
   public int[][] getMapTileNumber() { return mapTileNumber; }
}




















