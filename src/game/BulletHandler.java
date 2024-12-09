package game;

import city.cs.engine.StepEvent;

import java.util.ArrayList;

public class BulletHandler {
    private static final ArrayList<Bullet> bullets = new ArrayList<>();

    public static void AddBullet(Bullet b) {
        bullets.add(b);
    }

    public static void RemoveBullet(Bullet b) {
        bullets.remove(b);
    }

    public static void bulletStep(StepEvent event) {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).bulletStep(event);
        }
    }
}
