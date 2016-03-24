package nl.liacs.sports.football.visualization;

import java.util.Iterator;
import java.util.Vector;

import processing.core.PApplet;
import processing.core.PVector;

public class GameSimulator implements Drawable {

    private static final float WALL_TICK = 1f;
    private static float FIELD_W = 2.44f;
    private static float FIELD_H = 1.82f;
    public Vector<Simulatable> simulatables = new Vector<Simulatable>();
    public Field field;
    public Ball ball;
    public Goal goalLeft, goalRight;
    public PVector fieldCenter;
    float lastT = 0;
    private Vector<Drawable> drawables = new Vector<Drawable>();

    GameSimulator() {
        reset();
    }

    public void setFieldSize(float w, float h) {
        FIELD_W = w;
        FIELD_H = h;
        reset();
    }

    public float getFieldWidth() {
        return FIELD_W - field.space * 2;
    }

    public float getFieldHeight() {
        return FIELD_H - field.space * 2;
    }

    public void reset() {
        // Erase everything
        simulatables.clear();
        drawables.clear();

        // Create Field
        field = new Field(FIELD_W, FIELD_H);
        drawables.add(field);

        // Save field center
        fieldCenter = new PVector(field.width / 2, field.height / 2);

        // Create Ball
        ball = new Ball();
        ball.position = fieldCenter.get();
        simulatables.add(ball);
        drawables.add(ball);

        // Make Neutral Spots
        float xZero = FIELD_W / 2;
        float yZero = FIELD_H / 2;
        float xDelta = (FIELD_W - field.space * 2) / 2;
        float yDelta = field.y_goal / 2;

        // Make Goals
        xDelta = (FIELD_W - field.space * 2 - field.line_width * 2 + field.x_goal) / 2;
        goalLeft = new Goal(xZero - xDelta, yZero, field.x_goal, field.y_goal);
        goalRight = new Goal(xZero + xDelta, yZero, field.x_goal, field.y_goal);
        simulatables.add(goalLeft);
        // drawables.add(goalLeft);
        simulatables.add(goalRight);
        // drawables.add(goalRight);

    }

    public float getTime() {
        return lastT;
    }

    public void simulate(float t) {
        float dt = t - lastT;
        lastT = t;
        if (dt > 0.5 || dt <= 0f) {
            return;
        }

        // Simulate Physics
        for (Simulatable s : simulatables) {
            s.simulate(dt);

            // Simulate Collisions
            for (Simulatable b : simulatables) {
                if (b == s || !s.canCollide(b))
                    continue;

                if (s.colliding(b))
                    s.resolveCollision(b);
            }
        }
    }
   
   	/*
   		Return neares neutral spot to this position
   	*/

    public PVector getNearestNeutralSpot(PVector point) {
        PVector nearest = new PVector((FIELD_W / 2), (FIELD_H / 2));
        return nearest;
    }

    /*
          Returns if an Simulatable will hit anot simulatable
         */
    public boolean isColliding(Simulatable test) {
        for (Simulatable s : simulatables) {
            if (test == s) continue;
            if (s.colliding(test))
                return true;
        }
        return false;
    }

    public float closestSimulatableInRay(Robot robot, PVector origin, float direction) {
        float dist = Float.POSITIVE_INFINITY;
        synchronized (simulatables) {
            Iterator i = simulatables.iterator();
            while (i.hasNext()) {
                Simulatable sim = (Simulatable) i.next();

                if (sim == robot || sim instanceof Ball)
                    continue;
            }
        }

        return dist;
    }

    public void addToSimulation(Robot s) {
        if (simulatables.contains(s))
            return;

        simulatables.add(s);
        drawables.add(s);
    }

    public boolean inSimulation(Simulatable s) {
        return simulatables.contains(s);
    }


    public void removeFromSimulation(Robot s) {
        simulatables.remove(s);
        drawables.remove(s);
    }

    public void draw(PApplet canvas, float scale) {
        for (Drawable d : drawables)
            d.draw(canvas, scale);
    }
}

