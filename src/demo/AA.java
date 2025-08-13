package demo;

public class AA {
	public static void main(String[] args) {
		int x = 0;
		int y = 0;
		int col = 0;
		int row = 0;
		int[][] mapTileNumber = new int[3][3];
		while(col < 3 && row < 3) {
			int tileNum = mapTileNumber[col][row];
			System.out.println("tileNum: " + tileNum);
			System.out.println("col: " + col);
			System.out.println("row: " + row);
			System.out.println("x: " + x);
			System.out.println("y: " + y);
			System.out.println();
			col++;
			x += 48;
			if(col == 3) {
				col = 0;
				x = 0;
				row++;
				y += 48;
			}
		}
	}
}
