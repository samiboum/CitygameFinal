package game;

import java.awt.*;

public class MenuHandler {
    private boolean Active;
    private final GameWorld world;
    private final MenuFrame StageSelectFrame;

    public MenuHandler(GameView view, GameWorld w) {
        this.world = w;
        SetActive();

        //stage menu
        StageSelectFrame = new MenuFrame(view, w);
        StageSelectFrame.AddButton(new MenuLevelButton(30, 150, 200, 200, StageSelectFrame, "Map1"));
        StageSelectFrame.AddButton(new MenuLevelButton(250, 150, 200, 200, StageSelectFrame, "Map2"));
        StageSelectFrame.AddButton(new MenuLevelButton(470, 150, 200, 200, StageSelectFrame, "Map3"));
        StageSelectFrame.AddButton(new MenuLabel(70, 400, 200, 200, StageSelectFrame, "Map1"));
        StageSelectFrame.AddButton(new MenuLabel(290, 400, 200, 200, StageSelectFrame, "Map2"));
        StageSelectFrame.AddButton(new MenuLabel(510, 400, 200, 200, StageSelectFrame, "Map3"));
    }

    public void EndStage() {
        ScoreWriter.WriteScore(world.getMapName());
        world.DestroyLevel();
        StageSelectFrame.setActive(true);
        SetActive();
    }

    private void SetActive() {
        this.Active = true;
    }

    public boolean isActive() {
        return Active;
    }

    public void MenuGUI(Graphics2D g) {
        if (StageSelectFrame.isActive()) {
            StageSelectFrame.drawFrame(g);
        }
    }

}
