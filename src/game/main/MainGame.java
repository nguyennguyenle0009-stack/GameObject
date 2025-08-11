package game.main;

import javax.swing.JFrame;

public class MainGame {
	public static void main(String[] args) {
		JFrame window = new JFrame("Game");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setTitle("Game demo");
		GamePanel game = new GamePanel();
		window.add(game);
		window.pack(); // Use the JPanel component to determine window configuration
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	    //game.setUpGame();
		game.startGame();
	}
}
