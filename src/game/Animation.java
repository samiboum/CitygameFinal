package game;

import city.cs.engine.BodyImage;

public class Animation {
    private String name;
    private int fps;
    private int numberOfFrames;
    private int currentFrame = 0;
    private final boolean loop;
    private float lastUpdated;
    private final BodyImage[] imageSet;
    private final boolean reversedAnim;

    public Animation(String name, int fps, int numberOfFrames, float sizeMultiplier, boolean reversedAnim, boolean looped) {
        setName(name);
        setFps(fps);
        setNumberOfFrames(numberOfFrames);
        imageSet = new BodyImage[numberOfFrames];
        for (int i = 0; i < numberOfFrames; i++) {
            imageSet[i] = new BodyImage("data/Animations/" + this.name + i + ".png",sizeMultiplier);
        }
        this.reversedAnim = reversedAnim;
        this.loop = looped;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void setNumberOfFrames(int numberOfFrames) {
        this.numberOfFrames = numberOfFrames;
    }

    private void incrementFrame() {
        currentFrame++;
        if (currentFrame > (numberOfFrames-1)) {
            currentFrame = numberOfFrames-1;
            if (loop) {
                currentFrame = 0;
            }
        }
    }

    public BodyImage getImage(boolean increment) {
        int savedFrame = currentFrame;
        if (increment) {
            incrementFrame();
        }
        return imageSet[savedFrame];
    }

    public float getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(float lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isReversedAnim() {
        return reversedAnim;
    }
}
