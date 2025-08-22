package demo;

public class H {

	public static void main(String[] args) {
		int c = 8;
		int r = 3;
		for (int row = 0; row < r; row++ ) {
			for (int col = 0; col < c; col++) {
				System.out.print("* ");
			}
			System.out.println();
		}
	}
}
