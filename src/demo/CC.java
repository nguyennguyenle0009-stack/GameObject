package demo;

public class CC {
	Loadmap l = new Loadmap();
	int tilenum = l.mapTileNumber[12][20];
	public static void main(String[] args) {
		Loadmap l = new Loadmap();
    	l.loadMap("/data/map/world01.txt");
		int tilenum = l.mapTileNumber[12][20];
		System.out.println(tilenum);
	}
}
