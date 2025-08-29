package game.tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.main.GamePanel;
import game.util.UtilityTool;
import game.db.MapDao;

public class TileManager {
	private final GamePanel gp;
        private final Tile[] tile;
        // Mảng lưu bản đồ theo id
        private final Map<String, GameMap> maps = new HashMap<>();
        private GameMap currentMap;
        private int [][] mapTileNumber;
	
	// Contructor
	public TileManager(GamePanel gp) {
		this.gp = gp;
                this.tile = new Tile[30];

                getTileImage();
                loadMap("world01", "World 01", "", "/data/map/world01.txt");
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
	
    public void loadMap(String mapId, String name, String info, String mapPath) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(mapPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
            bufferedReader.close();

            int rows = lines.size();
            int cols = lines.get(0).split(" ").length;
            mapTileNumber = new int[cols][rows];
            for (int row = 0; row < rows; row++) {
                String[] numbers = lines.get(row).split(" ");
                for (int col = 0; col < cols; col++) {
                    mapTileNumber[col][row] = Integer.parseInt(numbers[col]);
                }
            }

            GameMap map = new GameMap(mapId, name, info, mapTileNumber);
            maps.put(mapId, map);
            currentMap = map;
            gp.setWorldSize(cols, rows);

            // ensure map exists in DB
            try {
                MapDao dao = new MapDao();
                dao.ensureExists(mapId, name, info);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Switch to an already loaded map by its identifier.
     */
    public void setCurrentMap(String mapId) {
        GameMap map = maps.get(mapId);
        if (map != null) {
            currentMap = map;
            mapTileNumber = map.getTileNumbers();
            gp.setWorldSize(map.getCols(), map.getRows());
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
            if(UtilityTool.isInsidePlayerView(worldX, worldY, gp)) {
                graphics2D.drawImage(tile[tileNumber].getImage(), screenX, screenY, null);
            } 
            else if (gp.getPlayer().getScreenX() > gp.getPlayer().getWorldX() || 
            		 gp.getPlayer().getScreenY() > gp.getPlayer().getWorldY() || 
            		 rightOffset > gp.getWorldWidth() - gp.getPlayer().getWorldX() || 
                     bottomOffset > gp.getWorldHeight() - gp.getPlayer().getWorldY()) {
            	graphics2D.drawImage(tile[tileNumber].getImage(), screenX, screenY, null);
            }
			if(gp.keyH.isDrawRect() == true) {
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
   
   public Tile[] getTile() { return tile; }
   public int[][] getMapTileNumber() { return mapTileNumber; }
   public GameMap getCurrentMap() { return currentMap; }
}




















