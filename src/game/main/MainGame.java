package game.main;

/**
 * Entry point of the application. It prepares the game panel, hands it to the
 * {@link WindowManager} and starts the game loop.
 */
public class MainGame {
        public static void main(String[] args) {
                GamePanel game = new GamePanel();
                WindowManager wm = new WindowManager("Game demo", game);
                wm.show();
                game.setUpGame();
                game.startGame();
        }
}
