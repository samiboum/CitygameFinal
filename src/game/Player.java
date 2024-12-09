package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import java.util.Hashtable;

public class Player extends Walker{
    private static Hashtable<Body,CollisionEvent> activeCollisions = new Hashtable<>();

    public Hashtable<Body,CollisionEvent> getActiveCollisions() {
        return activeCollisions;
    }

    public void setActiveCollisions(Hashtable<Body,CollisionEvent> newCollisions) {
        activeCollisions = newCollisions;
    }
    public Player(World w) {
        super(w, new BoxShape(0.6f,1.75f));
        this.setPosition(new Vec2(5,45));
        this.setGravityScale(0);
        this.setBullet(true);
        PlayerController.setPlayerInstance(this);

        for (Fixture fixture : this.getFixtureList()) {
            fixture.setDensity(0);
        }

        this.addCollisionListener(collisionEvent -> {
            //Store active collisions. Useful for wall and floor interactions
            Body otherBody = collisionEvent.getOtherBody();
            activeCollisions.put(otherBody, collisionEvent);
            PlayerController.Collision(collisionEvent);
        });

        //dropkick hit-box
        BoxShape shape = new BoxShape(2f,1.5f);
        Sensor sensor = new Sensor(this,shape);
        sensor.addSensorListener(new SensorListener() {
            @Override
            public void beginContact(SensorEvent sensorEvent) {
                if (sensorEvent.getContactBody() instanceof Enemy && PlayerController.isDropKicking()) {
                    Enemy e = EnemyController.getEnemy(sensorEvent.getContactBody());
                    e.Hit(PlayerController.getMomentum());
                }
            }

            @Override
            public void endContact(SensorEvent sensorEvent) {

            }
        });

    }



}
