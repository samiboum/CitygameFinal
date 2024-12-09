package game;

import org.jbox2d.common.Timer;


public class Time {
    private static final Timer timer = new Timer();

    public static float getScoreTime() {return (float) Math.round((timer.getMilliseconds() / 1000) * 100) / 100 ; }

    public static void resetStageTime() {
        timer.reset();
    }
}
