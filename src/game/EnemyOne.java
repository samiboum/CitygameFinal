package game;

import city.cs.engine.World;
import org.jbox2d.common.Vec2;

public class EnemyOne extends Enemy {

    public EnemyOne(World w, Vec2 location) {
        super(w, location, 1);
    }
}
