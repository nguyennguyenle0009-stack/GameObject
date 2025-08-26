package game.main;

import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainGame {
	public static void main(String[] args) {
		JFrame window = new JFrame("Game");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setResizable(true);
                window.setTitle("Game demo");
                GamePanel game = new GamePanel();
                window.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                                game.getPlayer().saveProgress();
                        }
                });
                window.add(game);
                window.pack(); // Use the JPanel component to determine window configuration
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            game.setUpGame();
                game.startGame();
        }
}
