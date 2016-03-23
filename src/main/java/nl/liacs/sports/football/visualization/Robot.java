package nl.liacs.sports.football.visualization;

import processing.core.PApplet;
import processing.core.PVector;

public class Robot extends Simulatable implements ShapeCircle, Drawable, Runnable {

    int teamColor = 0x000000;
    /*
          Saves the GameSimulator in order to access global stuff such as objects from field
         */
    private GameSimulator game;
    private float motorForce = 50f;
    private float maxSpeed = 50f;
    private PVector targetSpeed = new PVector(0, 0);
    // Orientation attributes
    private float maxAngularAccel = (float) Math.PI * 10;
    private float maxAngularSpeed = (float) Math.PI * 10;
    private float targetAngularSpeed = 0f;
    private float angularSpeed = 0f;
    private int jersey;

    Robot(GameSimulator g) {
        game = g;
    }
    //############################################################################################################################33

    //######################################################belangijke functie##################################################
    public void setState(PVector position, int number) {
        this.position = position.get();
        jersey = number;
    }

    /*
          Physical properties
         */
    public float getRadius() {
        return 2 * 0.40f; //2 x zo groot als de bal
    }

    public float getMass() {
        return 2.2f;
    }

    public float getKFactor() {
        return 1f;
    }

    public boolean canCollide(Simulatable s) {
        if (s instanceof Goal)
            return false;

        return true;
    }

    /*
          This is the method used by the Thread.
         */
    public void run() {
    }

    public long millis() {
        return (long) (game.getTime() * 1000);
    }

    public boolean delay(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void onStateChanged(String state) {
    }

    /*
          Perform Simulation
         */
    public void simulate(float dt) {
        // Accelerate angular speed
        float requiredSpeed = targetAngularSpeed - angularSpeed;
        float angularAccel = requiredSpeed / dt;
        angularAccel = PApplet.constrain(angularAccel, -maxAngularAccel, maxAngularAccel);

        // Limit Angular speed
        angularSpeed += angularAccel * dt;
        angularSpeed = PApplet.constrain(angularSpeed, -maxAngularSpeed, maxAngularSpeed);

        // Orientation Simulation
        //orientation += angularSpeed * dt;

        // Position simulation
        PVector worldRequiredSpeed = targetSpeed.get();
        //worldRequiredSpeed.rotate(orientation);
        worldRequiredSpeed.sub(speed);

        // PVector worldRequiredSpeed = worldTargetSpeed.get();
        float dSpeed = worldRequiredSpeed.mag();
        float dAcell = dSpeed / dt;
        float dForce = Math.min(dAcell * getMass(), motorForce);

        worldRequiredSpeed.normalize();
        worldRequiredSpeed.mult(dForce);
        force.add(worldRequiredSpeed);

        super.simulate(dt);
    }

    public void setTeamColor(int color) {
        teamColor = color;
    }

    public void draw(PApplet canvas, float scale) {
        //PVector orient = PVector.fromAngle(orientation);
        //orient.mult(scale * getRadius());
        float x = (float) position.x * scale;
        float y = (float) position.y * scale;
        float diameter = getRadius() * 2 * scale;

        canvas.fill(teamColor);
        canvas.stroke(0);
        canvas.ellipse(x, y, diameter, diameter);
        //canvas.line(x, y, x + (float)orient.x, y + (float)orient.y);

        // Delegate Decoration to Robot
        //float heading = orient.heading();
        float drawScale = 100f / scale * getRadius();

        canvas.translate(x, y);
        //canvas.rotate(heading);
        canvas.scale(drawScale);


        canvas.textSize(2);
        canvas.fill(0);
        canvas.textAlign(PApplet.LEFT);
        canvas.text(jersey, 0, 0);

        canvas.scale(1f / drawScale);
        //canvas.rotate(-heading);
        canvas.translate(-x, -y);
    }
};

