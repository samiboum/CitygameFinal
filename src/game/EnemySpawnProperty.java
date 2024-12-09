package game;

import org.jbox2d.common.Vec2;

public class EnemySpawnProperty {
    public EnemySpawnProperty (Tile t, int type) {
        Vec2 location = t.getPosition();
        if (type == 1) {
            new EnemyOne(t.getWorld(), location);
        }
        if (type == 2) {
            new EnemyTwo(t.getWorld(), location);
        }
        if (type == 3) {
            new EnemyThree(t.getWorld(), location);
        }
        t.destroy();
    }
}
