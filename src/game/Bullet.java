package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

public class Bullet {
    private float angle;
    private final float shotSpeed;

    private final float size;

    private final DynamicBody bullet;
    private float lifetime = 0;

    public Bullet(World world, float size, float angle, float shotSpeed) {
        this.angle = angle;
        this.shotSpeed = shotSpeed;
        this.size = size;

        CircleShape bulletShape = new CircleShape(size);
        bullet = new DynamicBody(world);
        Sensor sensor = new Sensor(bullet, bulletShape);

        sensor.addSensorListener(new SensorListener() {
            @Override
            public void beginContact(SensorEvent sensorEvent) {
                if (sensorEvent.getContactBody() == PlayerController.getPlayerInstance()) {
                    PlayerController.TakeDamage(1);
                }
            }

            @Override
            public void endContact(SensorEvent sensorEvent) {

            }
        });
    }

    public void Activate(float angle, Vec2 spawnpoint) {
        this.angle = angle;
        CircleShape bulletShape = new CircleShape(this.size);
        new GhostlyFixture(bullet, bulletShape);
        bullet.isBullet();
        bullet.setGravityScale(0);
        bullet.setPosition(spawnpoint);
        this.lifetime = 0;

        BulletHandler.AddBullet(this);
    }

    public void bulletStep(StepEvent stepEvent) {
        velocityStep();

        lifetime+= stepEvent.getStep();
        if (lifetime > 5) {
            BulletHandler.RemoveBullet(this);
        }
    }

    private void velocityStep() {
        Vec2 velocity = Helper.getOffsetByAngleDistance(bullet.getPosition(), angle, 10000f);
        velocity = velocity.mul(shotSpeed);
        bullet.setLinearVelocity(velocity);
    }
}
