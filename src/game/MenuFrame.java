package game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuFrame {
    private final GameView view;
    private final GameWorld world;
    private final List<MenuButton> buttonList = new ArrayList<>();
    private boolean Active;

    public MenuFrame(GameView view, GameWorld w) {
        this.view = view;
        this.world = w;
        setActive(true);
    }

    public GameWorld getWorld() {
        return world;
    }

    public void AddButton(MenuButton button) {
        buttonList.add(button);
        view.addMouseListener(button);
    }

    public boolean isActive() {
        return Active;
    }

    public void drawFrame(Graphics2D g) {
        if (Active) {
            for (MenuButton button: buttonList) {
                button.drawButton(g);
            }
        }
    }

    public void setActive(boolean active) {
        Active = active;

        for (MenuButton button : buttonList) {
            button.SetVisible(active);
        }
    }
}
