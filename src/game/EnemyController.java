package game;

import city.cs.engine.StepEvent;
import city.cs.engine.Walker;
import city.cs.engine.Body;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;

public class EnemyController {
    private static final Dictionary<Body, Enemy> enemyDictionary = new Hashtable<>();

    public static void EnemyStep(StepEvent stepEvent) {
        for (Iterator<Enemy> it = enemyDictionary.elements().asIterator(); it.hasNext(); ) {
            Enemy e = it.next();

            e.enemyStep(stepEvent.getStep());
        }
    }

    public static void AddEnemy(Enemy e) {
        enemyDictionary.put(e,e);
    }

    public static void RemoveEnemy(Walker body) {
        enemyDictionary.remove(body);
    }

    public static void RemoveAllEnemies() {
        for (Iterator<Enemy> it = enemyDictionary.elements().asIterator(); it.hasNext(); ) {
            Enemy e = it.next();

            Walker body = e.getBody();

            e.Destroy();
            RemoveEnemy(body);
        }
    }

    public static Enemy getEnemy(Body body) {
        return enemyDictionary.get(body);
    }

}
