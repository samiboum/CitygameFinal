package game;

import java.awt.*;

public class MenuLabel extends MenuButton{
    private final Rectangle bounds;
    private final String stageName;
    private String text;

    public MenuLabel(int x, int y, int width, int height, MenuFrame frame, String stagename) {
        super(x, y, width, height, frame);
        this.bounds = new Rectangle(x,y,width,height);
        this.stageName = stagename;
        UpdateLabel();
    }

    public void SetVisible(boolean val) {
        UpdateLabel();
    }

    public void UpdateLabel() {
        double score = ScoreWriter.GetScore(stageName);
        if (score == -1d) {
            this.text = "";
            return;
        }
        this.text = "Record: " + score;
    }

    public void drawButton(Graphics2D g) {
        g.setPaint(new Color(241, 241, 241));
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        g.drawString(text, bounds.x, bounds.y);

    }
}
