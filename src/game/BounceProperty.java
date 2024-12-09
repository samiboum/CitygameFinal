package game;

public class BounceProperty {

    public BounceProperty(Tile t) {
        t.addCollisionListener(collisionEvent -> {
            if (collisionEvent.getOtherBody() instanceof Player) {
                PlayerController.Bounce();
            }
        });
    }
}
