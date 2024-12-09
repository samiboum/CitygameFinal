package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;

public class BulletTrace extends StaticBody {
    private final Body sourcebody;
    private final Body targetbody;
    private final float delay;
    private final float hangtime;
    private float timer;
    private boolean finished;
    private final Bullet storedBullet;

    public BulletTrace(World w, Body source, Body target, float Delay, float shotSpeed, float hangtime) {
        super(w);

        this.sourcebody = source;
        this.targetbody = target;
        this.delay = Delay;
        this.hangtime = hangtime;

        storedBullet = new Bullet(w, 0.2f, 0, shotSpeed);
    }

    public void ActivateTrace() {
        BoxShape outline = new BoxShape(0.25f, 50f);
        new GhostlyFixture(this, outline);
        this.setFillColor(new Color(255,0,0,120));
        this.setLineColor(new Color(255,0,0,120));
        this.setPosition(this.sourcebody.getPosition());
        this.timer = 0;
        this.finished = false;

        PointToTarget();

        BulletTraceController.AddBulletTrace(this);
    }

    public void Destroy() {
        HideTrace();
        this.destroy();
    }

    public void Disable() {
        HideTrace();
    }

    private void HideTrace() {
        for (Fixture f : this.getFixtureList()) {
            f.destroy();
            BulletTraceController.RemoveBulletTrace(this);
        }
    }

    public void traceStep(StepEvent stepEvent) {
        timer += stepEvent.getStep();

        if (timer > delay) {
            Shoot();
        } else if (!(timer > delay-hangtime)) {
            PointToTarget();
        }

    }

    private void Shoot() {
        if (!finished) {
            finished = true;
            float angle = this.getAngle();
            storedBullet.Activate(angle,sourcebody.getPosition());
            HideTrace();
            try {
                SoundHandler.PlaySound("gunshot");
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void PointToTarget() {
        Vec2 sourcepos = sourcebody.getPosition();
        Vec2 targetpos = targetbody.getPosition();

        float theta = Helper.getAngleBetweenVectors(sourcepos,targetpos);

        this.setPosition(Helper.getOffsetByAngleDistance(sourcepos,theta,50f));
        this.setAngle(theta);
    }


}
