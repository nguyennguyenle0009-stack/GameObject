package game.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import game.interfaces.DrawableEntity;
import game.main.GamePanel;

/**
 * Renderer is responsible for drawing all visual elements of the game.
 * It gathers drawable entities, sorts them and renders them along with
 * UI elements. This keeps rendering code separate from game logic.
 */
public class Renderer {

    /** reference to the game panel providing game state */
    private final GamePanel gp;

    /**
     * Create a renderer for the specified {@link GamePanel}.
     *
     * @param gp game panel supplying data to render
     */
    public Renderer(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Render all game elements onto the provided graphics context.
     *
     * @param g2 graphics context used for drawing
     */
    public void render(Graphics2D g2) {
        long drawStart = 0;
        if (gp.keyH.isCheckDrawTime()) {
            drawStart = System.nanoTime();
        }

        // draw tiles
        gp.getTileManager().draw(g2);

        // gather drawable entities
        List<DrawableEntity> drawList = new ArrayList<>();
        drawList.addAll(gp.getObjects());
        drawList.addAll(gp.getNpcs());
        drawList.add(gp.getPlayer());

        // sort entities by their foot Y coordinate for proper layering
        drawList.sort(Comparator.comparingInt(DrawableEntity::getFootY));

        // draw each entity
        for (DrawableEntity entity : drawList) {
            entity.draw(g2, gp);
        }

        // draw UI
        gp.getUi().draw(g2);

        // optional draw time debug information
        if (gp.keyH.isCheckDrawTime()) {
            long drawEnd = System.nanoTime();
            long passedTime = drawEnd - drawStart;
            g2.setColor(Color.white);
            g2.drawString("Draw Time: " + passedTime, 10, 400);
            System.out.println("Draw Time: " + passedTime);
        }

        g2.dispose();
    }
}
