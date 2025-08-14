package demo;

public class BB {

    
    public static void main(String[] args) {
    	
    	Loadmap load = new Loadmap();
    	load.loadMap("/data/map/world01.txt");

        int worldCol = 0;
        int worldRow = 0;

        while (worldRow < 50) {
            int tileNumber = load.mapTileNumber[worldCol][worldRow];
            System.out.printf("Tile at (%d,%d) = %d\n", worldCol, worldRow, tileNumber);

            worldCol++;
            if (worldCol == 50) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
    
}

