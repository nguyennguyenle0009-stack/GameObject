package game.tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.main.GamePanel;

public class TileManager {
	
	private final GamePanel gp;
	private final Tile[] tile;
	
	// Mảng lưu số hiệu của từng tile trong bản đồ
	private final int [][] mapTileNumber;
	
	// Contructor
	public TileManager(GamePanel gp) {
		this.gp = gp;
		this.tile = new Tile[10];
		this.mapTileNumber = new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
		
		getTileImage();
		loadMap("/data/map/world01.txt");
	}
	
	public void getTileImage() {
		try {
			tile[0] = new Tile();
			tile[0].setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/aa6.png"))));
			tile[0].setCollision(true);
			
			tile[1] = new Tile();
			tile[1].setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/aa.png"))));
			
			tile[2] = new Tile();
			tile[2].setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/aa1.png"))));
			
			tile[3] = new Tile();
			tile[3].setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/aa2.png"))));
			
			tile[4] = new Tile();
			tile[4].setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/aa3.png"))));
			
			tile[5] = new Tile();
			tile[5].setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/aa5.png"))));
			tile[5].setCollision(true);
			
			
		}catch (Exception e) {
			e.getStackTrace();
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
        	// Vị trí tile trên màn hình, tính theo vị trí người chơi
            int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
            int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();
   
            int rightOffset = gp.getScreenWidth() - gp.getPlayer().getScreenX();
            screenX = checkIfAtEdgeOfXAxis(worldX, screenX, rightOffset);

            int bottomOffset = gp.getScreenHeight() - gp.getPlayer().getScreenY();
            screenY = checkIfAtEdgeOfYAxis(worldY, screenY, bottomOffset);

            // Tối ưu hiệu suất, bản đồ không được vẽ khi ngoài màn hình
            if (worldX + gp.getTileSize() > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() &&
                worldX - gp.getTileSize() < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() &&
                worldY + gp.getTileSize() > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() &&
                worldY - gp.getTileSize() < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) {
                graphics2D.drawImage(tile[tileNumber].getImage(), screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
                graphics2D.setColor(Color.BLUE);
                graphics2D.drawRect(screenX, screenY, gp.getTileSize(), gp.getTileSize());
            } 
            else if (gp.getPlayer().getScreenX() > gp.getPlayer().getWorldX() || 
            		 gp.getPlayer().getScreenY() > gp.getPlayer().getWorldY() || 
            		 rightOffset > gp.getWorldWidth() - gp.getPlayer().getWorldX() || 
                     bottomOffset > gp.getWorldHeight() - gp.getPlayer().getWorldY()) {
            	graphics2D.drawImage(tile[tileNumber].getImage(), screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
                graphics2D.setColor(Color.BLUE);
                graphics2D.drawRect(screenX, screenY, gp.getTileSize(), gp.getTileSize());
            }
            worldCol++;
            if(worldCol == gp.getMaxWorldCol()) {
            	worldCol = 0;
            	worldRow++;
            }
        }
    }
   
   private int checkIfAtEdgeOfXAxis(int worldX, int screenX, int rightOffSet) {
	   if(gp.getPlayer().getScreenX() > gp.getPlayer().getWorldX()) {
		   return worldX;
	   }
	   if(rightOffSet > gp.getWorldWidth() - gp.getPlayer().getWorldX()) {
		   return gp.getScreenWidth() - (gp.getWorldWidth() - worldX);
	   }
	   return screenX;
   }
   
   private int checkIfAtEdgeOfYAxis(int worldY, int screenY, int botOffSet) {
	   if(gp.getPlayer().getScreenY() > gp.getPlayer().getWorldY()) {
		   return worldY;
	   }
	   if(botOffSet > gp.getWorldHeight() - gp.getPlayer().getWorldY()) {
		   return gp.getScreenHeight() - (gp.getWorldHeight() - worldY);
	   }
	   return screenY;
   }
   
   public Tile[] getTile() {
       return tile;
   }

   public int[][] getMapTileNumber() {
	return mapTileNumber;
   }
   
}




















