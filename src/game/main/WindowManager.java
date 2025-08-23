package game.main;

import javax.swing.JFrame;

/**
 * WindowManager is responsible for creating and showing the main window.
 * It configures the {@link JFrame} and attaches the game's panel.
 */
public class WindowManager {

    /** underlying swing window */
    private final JFrame window;

    /**
     * Create a window manager.
     *
     * @param title title of the window
     * @param panel game panel to display
     */
    public WindowManager(String title, GamePanel panel) {
        window = new JFrame(title);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setTitle(title);
        window.add(panel);
        window.pack();              // size to fit panel
        window.setLocationRelativeTo(null);
    }

    /**
     * Display the configured window to the user.
     */
    public void show() {
        window.setVisible(true);
    }
}
