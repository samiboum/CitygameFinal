package game;

import city.cs.engine.Sensor;
import city.cs.engine.Shape;
import city.cs.engine.StaticBody;
import city.cs.engine.World;

public class Tile extends StaticBody {
    private final Sensor sensor;

    public Tile(World w, Shape shape) {
        super(w, shape);
        sensor = new Sensor(this, shape);
    }

    public void AddBounce() {
        //make tiles a class.
        //give them properties.
        //make the properties also classes?
        //bounce, opaque, nocollide, ect.
        new BounceProperty(this);
    }

    public void AddDialogue() {
        new DialogueProperty(this);
        this.setName(this.getPosition() + PlayerController.getMap());
    }

    public void AddEnemy(int type) {
        new EnemySpawnProperty(this, type);
    }

    public void AddHeart() {
        new HeartProperty(this);
    }

    public void AddStageEnd() {
        new StageEndProperty(this);
    }

    public Sensor getSensor() {
        return this.sensor;
    }

    //about dialogue...
    // it's a little bit weird, but the only unique way to identify it that i can think of is like...
    // position and map?
    // that wouldn't be fun at all to debug, but whatever man.
    // if it works, it works
}
