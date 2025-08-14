package demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Loadmap {
	
    public int[][] mapTileNumber;
	
	public Loadmap() {
		
		this.mapTileNumber = new int[50][50];
	}
    
    public void loadMap(String mapPath) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(mapPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            int col = 0;
            int row = 0;

            while (col < 50 && row < 50) {
                String line = bufferedReader.readLine();

                while (col < 50) {
                    String[] numbers = line.split(" ");
                    int number = Integer.parseInt(numbers[col]);

                    mapTileNumber[col][row] = number;
                    col++;
                }
                if (col == 50) {
                    col = 0;
                    row++;
                }
            }

            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
