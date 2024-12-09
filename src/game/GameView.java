package game;

import city.cs.engine.UserView;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;

public class GameView extends UserView{
    private final Dictionary<String, GuiImage> guiImages = new Hashtable<>();
    private final ImageObserver observer = (img, infoFlags, x, y, width, height) -> false;
    private final MenuHandler menuHandler;

    public GameView(GameWorld w, int width, int height) {
        super(w, width, height);

        CreateGui();
        menuHandler = new MenuHandler(this, w);
    }

    public MenuHandler getMenuHandler() {
        return menuHandler;
    }

    private void CreateGui() {
        //health
        for (int i = 1; i < 6; i++) {
            GuiImage image = new GuiImage("Health"+i,"health",(42*i), 30);
            guiImages.put("Health"+i, image);
        }
    }

    private void HealthGUI() {
        int health = PlayerController.getHealth();
        for (Iterator<GuiImage> it = guiImages.elements().asIterator(); it.hasNext(); ) {
            GuiImage image = it.next();

            String name = image.getName();
            int num = Integer.parseInt(name.substring(name.length() - 1));

            if (num > health) {
                image.setIcon("healthlost");
            } else {
                image.setIcon("health");
            }
        }
    }

    private void MomentumGUI(Graphics2D g) {
        //full bar
        g.setPaint(new Color(80,80,80));
        g.fillRect(42,65,(42*8),10);

        //content bar
        float momentum = PlayerController.getMomentum();
        float momentumMax = PlayerController.getMomentumMax();
        int width = (int) Math.ceil((momentum/momentumMax) * (42*8));

        g.setPaint(new Color(111, 166, 255));
        if (momentum >= 3) {
            g.setPaint(new Color(255,85,0));
        }
        g.fillRect(42,65, width, 10);
    }

    private void OverMomentumGUI(Graphics2D g) {
        //content bar
        float overmomentum = PlayerController.getOvermomentum();
        float overmomentumMax = PlayerController.getOvermomentumMax();
        int width = (int) Math.ceil((overmomentum/overmomentumMax) * (100));

        g.setPaint(new Color(230, 0, 255));
        g.fillRect(378,65, width, 10);
    }

    private void DialogueGUI(Graphics2D g) {
        DialogueHandler.HandleGUI(g);
    }

    private void InteractGUI(Graphics2D g) {
        if (Objects.equals(PlayerController.GetActiveDialogue(), "")) {return;}
        g.setPaint(new Color(255,255,255));
        g.setFont(new Font("Dialog", Font.PLAIN,16));
        g.setStroke(new BasicStroke(10));
        g.drawString("Interact (↑)", 325, 350);
    }

    private void TimerGUI(Graphics2D g) {
        g.setFont(new Font("Dialog", Font.PLAIN, 16));
        g.setPaint(new Color(255,255,255));
        g.drawString("Time : " + (Time.getScoreTime()), 50, 100);
    }

    @Override
    protected void paintForeground(Graphics2D g) {
        //Handle momentum bar
        if (menuHandler.isActive()) {
            menuHandler.MenuGUI(g);
        }
        DialogueGUI(g);
        if (PlayerController.getPlayerInstance() == null) {return;}
        MomentumGUI(g);
        HealthGUI();
        OverMomentumGUI(g);
        InteractGUI(g);
        TimerGUI(g);

        for (Iterator<GuiImage> it = guiImages.elements().asIterator(); it.hasNext(); ) {
            GuiImage image = it.next();

            g.drawImage(image.getIcon().getImage(), image.getX(),image.getY(), observer);
        }
    }

    @Override
    protected void paintBackground(Graphics2D g) {

        if (PlayerController.getPlayerInstance() == null) {
            g.setPaint(new Color(0, 9, 47));
            if (DialogueHandler.isInDialogue()) {
                g.setPaint(new Color(0,0,0));
            }
            g.fillRect(0,0,800,800);
            return;
        }
        g.setPaint(new Color(0,0,0));
        if (Objects.equals(PlayerController.getMap(), "Map1")) {
            g.setPaint(new Color(81, 42, 128));
        }
        if (Objects.equals(PlayerController.getMap(), "Map2")) {
            g.setPaint(new Color(26, 39, 82));
        }
        g.fillRect(0,0,800,800);
        ImageIcon icon = new ImageIcon("data/clouds.png");


        int x = (int) PlayerController.getPlayerInstance().getPosition().x *3;
        while (x > 800) {
            x -= 800;
        }
        int y = (int) PlayerController.getPlayerInstance().getPosition().y / 2;


        g.drawImage(icon.getImage(), -x, y, 800, 400, observer);
        g.drawImage(icon.getImage(), -(x-800), y, 800, 400, observer);
    }

}
