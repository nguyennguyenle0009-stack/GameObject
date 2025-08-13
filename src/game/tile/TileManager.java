package game.tile;

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
	private final int [][] mapTileNumber;
	
	public TileManager(GamePanel gp) {
		this.gp = gp;
		this.tile = new Tile[10];
		this.mapTileNumber = new int[gp.maxScreenCol][gp.maxScreenRow];
		
		getTileImage();
		loadMap("/data/map/world01.txt");
	}
	
	public void getTileImage() {
		try {
			tile[0] = new Tile();
			tile[0].setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/earth.png"))));
			
			tile[1] = new Tile();
			tile[1].setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/grass00.png"))));
			
			tile[2] = new Tile();
			tile[2].setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/water00.png"))));
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

            while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = bufferedReader.readLine();

                while (col < gp.maxScreenCol) {
                    String[] numbers = line.split(" ");
                    int number = Integer.parseInt(numbers[col]);

                    mapTileNumber[col][row] = number;
                    col++;
                }
                if (col == gp.maxScreenCol) {
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
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;
        
        while(col < gp.maxScreenCol && row < gp.maxScreenRow) {
        	int tileNumber = mapTileNumber[col][row];
            graphics2D.drawImage(tile[tileNumber].getImage(), x, y, gp.tileSize, gp.tileSize, null);
            col++;
            x += gp.tileSize;
            if(col == gp.maxScreenCol) {
            	col = 0;
            	x = 0;
            	row++;
            	y += gp.tileSize;
            }
        }
    }
}




















