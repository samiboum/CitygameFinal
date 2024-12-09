package game;

import city.cs.engine.StepEvent;

import java.util.ArrayList;

public class BulletTraceController {
    private static final ArrayList<BulletTrace> bulletTraces = new ArrayList<>();

    public static void AddBulletTrace(BulletTrace b) {
        bulletTraces.add(b);
    }

    public static void RemoveBulletTrace(BulletTrace b) {
        bulletTraces.remove(b);
    }

    public static void traceStep(StepEvent event) {
        for (int i = 0; i < bulletTraces.size(); i++) {
            bulletTraces.get(i).traceStep(event);
        }
    }
}
