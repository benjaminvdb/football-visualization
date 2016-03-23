package nl.liacs.sports.football.visualization;

import processing.core.PApplet;
import processing.core.PVector;

public class Ball extends Simulatable implements ShapeCircle, Drawable {

    public float kAtrito = 17f;
    boolean isOn = true;
    int ds = 0;
    private float z = 1;

    public float getMass() {
        return 0.300f;
    }

    public float getRadius() {
        return 0.40f; //voetbal groote
    }

    public float getKFactor() {
        return 1.0f;
    }

    public boolean canCollide(Simulatable s) {
        if (s instanceof Goal)
            return false;

        return true;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    private void setZsize(float z2) {
        z = 1 + z2 / 1026; //2052 = highest value of z in database so the value will always stay inbetween 1 and 3
    }
    //#############################################################important Function##############################################################

    /*
          Sets position of Ball and reset's speed, acceleration and force
         */
    //#############################################################important Function##############################################################
    public void setPosition(PVector p) {
        force.set(0, 0);
        accel.set(0, 0);
        speed.set(0, 0);
        setZsize(p.z);
        position.set(p.x, p.y, p.z);
    }

    public PVector getForce(float dt) {
        PVector forceSum = super.getForce(dt);
        PVector atrito = getRealSpeed();
        atrito.mult(dt);
        atrito.mult(-1 * kAtrito);

        forceSum.add(atrito);
        return forceSum;
    }

    public void draw(PApplet canvas, float scale) {

        if (isOn())
            canvas.fill(255, 255, 255);
        else
            canvas.fill(100, 100, 100);

        canvas.stroke(0);
        float size = getRadius() * scale * 2 * z;
        canvas.ellipse(position.x * scale, position.y * scale, size, size);
    }
}

