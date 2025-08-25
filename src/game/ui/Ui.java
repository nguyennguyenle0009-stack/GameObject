package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.List;

import game.entity.Entity;
import game.main.GamePanel;
import game.util.UtilityTool;

/**
 * Root UI controller for the game. Delegates inventory rendering to
 * {@link InventoryUi} and keeps other HUD elements here.
 */
public class Ui {
    private final GamePanel gp;
    private Graphics2D g2;
    private final Font arial_40, arial_80B;
    private boolean messageOn = false;
    private String message;
    private int messageCouter;
    private String currentDialogue = "";

    private final InventoryUi inventory;
    private final GameHUD hud;
    private final SkillUi skillUi;
    
    private int damageCounter = 0;

    public Ui(GamePanel gp) {
        this.gp = gp;
        this.arial_40 = new Font("Arial", Font.PLAIN, 40);
        this.arial_80B = new Font("Arial", Font.BOLD, 80);
        this.inventory = new InventoryUi(gp);
        this.hud = new GameHUD(gp);
        this.skillUi = new SkillUi(gp);
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setColor(Color.white);
        if (gp.getGameState() == gp.getPlayState()) {
            drawInteractionHint(g2);
        }
        if (gp.getGameState() == gp.getPauseState()) {
            drawPauseScreen();
        }
        if (gp.getGameState() == gp.getDialogueState()) {
            drawDialogueScreen();
        }
        if (gp.keyH.isiPressed()) {
            inventory.draw(g2);
            if (skillUi.isVisible()) {
                skillUi.draw(g2);
            }
        }
        hud.draw(g2);
        if (damageCounter > 0) {
            g2.setColor(new Color(255, 0, 0, 100));
            g2.fillRect(0, 0, gp.getScreenWidth(), gp.getScreenHeight());
            damageCounter--;
        }
    }

    public boolean handleInventoryMousePress(int mx, int my, int button) {
        if (inventory.handleMousePress(mx, my, button)) return true;
        if (skillUi.handleMousePress(mx, my, button)) return true;
        return hud.handleMousePress(mx, my, button);
    }

    private void drawInteractionHint(Graphics2D g2) {
        List<Entity> nearbyNpcs = gp.getCheckCollision().getEntitiesInRange(gp.getPlayer(), gp.getNpcs(), 48);
        if (!nearbyNpcs.isEmpty()) {
            // Lưu lại font và stroke gốc để tránh ảnh hưởng tới HUD
            Font oldFont = g2.getFont();
            Stroke oldStroke = g2.getStroke();

            g2.setFont(oldFont.deriveFont(Font.PLAIN, 20F));

            int padding = 10;
            int lineHeight = 25;
            int boxWidth = 200;
            int boxHeight = nearbyNpcs.size() * lineHeight + padding * 2;

            int boxX = gp.getScreenWidth() - boxWidth - 20;
            int boxY = 20;

            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

            g2.setColor(Color.WHITE);
            int startY = boxY + padding + 20;
            for (Entity npc : nearbyNpcs) {
                g2.drawString(npc.getName(), boxX + padding, startY);
                startY += lineHeight;
            }

            // Khôi phục lại font và stroke ban đầu
            g2.setFont(oldFont);
            g2.setStroke(oldStroke);
        }
    }

    private void drawPauseScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
        String text = "PAUSED";
        int x = UtilityTool.getXForCenterOfText(text, gp, g2);
        int y = gp.getScreenHeight() / 2;
        g2.drawString(text, x, y);
    }

    private void drawDialogueScreen() {
        int x = gp.getTileSize() * 2;
        int y = gp.getTileSize() / 2;
        int width = gp.getScreenWidth() - (gp.getTileSize() * 4);
        int height = gp.getTileSize() * 4;

        drawSubWindow(x, y, width, height, g2);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        x += gp.getTileSize();
        y += gp.getTileSize();

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawSubWindow(int x, int y, int width, int height, Graphics2D g2) {
        Color color = new Color(0, 0, 0, 210);
        g2.setColor(color);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        color = new Color(255, 255, 255);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public boolean isMessageOn() { return messageOn; }
    public Ui setMessageOn(boolean messageOn) { this.messageOn = messageOn; return this; }
    public Font getArial_40() { return arial_40; }
    public Font getArial_80B() { return arial_80B; }
    public String getMessage() { return message; }
    public Ui setMessage(String message) { this.message = message; return this; }
    public int getMessageCouter() { return messageCouter; }
    public Ui setMessageCouter(int messageCouter) { this.messageCouter = messageCouter; return this; }
    public String getCurrentDialogue() { return currentDialogue; }
    public Ui setCurrentDialogue(String currentDialogue) { this.currentDialogue = currentDialogue; return this; }

    public InventoryUi getInventory() { return inventory; }
    public SkillUi getSkillUi() { return skillUi; }
    
    public void triggerDamageEffect() { damageCounter = 10; }
}