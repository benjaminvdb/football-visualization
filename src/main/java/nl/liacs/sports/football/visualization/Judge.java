package nl.liacs.sports.football.visualization;

import java.util.HashMap;
import java.util.HashSet;

import processing.core.PVector;

public class Judge {
    // The actual game half time (1st, 2nd...)
    int gameHalf = 1;
    float time;
    float realTime = 0;
    float goal;
    private float GAME_DURATION = 60 * 90;
    // Set the time for the entire game (defaults to 5min)
    float duration = GAME_DURATION;
    private float ROBOT_TAKEN_OUTSIDE_TIME = GAME_DURATION;
    private float BALL_OUTSIDE_TIME_LIMIT = GAME_DURATION;
    private GameController controller;
    private GameSimulator simulator;
    // Robots that have been taken out of field for some reason
    private HashSet<Robot> takenOutRobots = new HashSet<Robot>();
    private HashMap<Robot, Timer> takenOutTimers = new HashMap<Robot, Timer>();
    private Timer ballOutsideTimer = new Timer(BALL_OUTSIDE_TIME_LIMIT, true);
    private boolean isOut = false;

    Judge(GameController controller) {
        this.controller = controller;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void judge(float time, PVector[] ballpositions, int frame, int[] sections) {
        // Integrate time, only if no glitches and simulation is running
        if (controller.isRunning())
            realTime += time - this.time;

        this.time = time;

        // Skip Judging if is paused
        if (!controller.isRunning())
            return;

        // Check if Ball is outside field for a long time
        checkBallOutside();

        // Check if scored goals
        checkGoal(ballpositions, frame, time);

        // Check end of Half
        checkEndOfHalf(sections, frame);
    }

    /*
          Returns the time to show in the UI
         */
    public float getCurrentTime() {
        return time;
    }

    /*
          Returns the current state
         */
    public String getCurrentState() {
        String running = controller.isRunning() ? "Running" : "Stopped";
        String half = gameHalf + " half";
        if (gameHalf > 2)
            half = "ENDED";

        return half + " - " + running;
    }

    private void setHalf(int half) {
        if (half == 2) {
            controller.initTeamSides(false);
            controller.restartPositions(TeamSide.LEFT);
            controller.resumeGame();
        }

        if (half > 2) {
            System.out.println("einde wedstrijd");
            System.out.println(controller.getPointsFor(TeamSide.LEFT) + "links - rechts" + controller.getPointsFor(TeamSide.RIGHT));
            System.exit(1);
            return;
        }

        gameHalf = half;
    }

    private void checkBallOutside() {
        Ball b = simulator.ball;

        if (b.colliding(simulator.fieldArea)) {
            ballOutsideTimer.reset(time);
        } else {
            if (ballOutsideTimer.triggered(time)) {
                System.out.println("uitbal");
            }
        }
    }

    //kijk of er een aftrap is in de komende minuut
    boolean actualGoal(PVector[] pos, int ball, float time) {
        return true;
        //    if (pos[ball].z < 2.44f) {
        //      for ( int i = ball; i < ball+1500; i++) {
        //
        //        if (pos[i].x > 50 && pos[i].x < 55 && pos[i].y > 32 && pos[i].y < 36 && ball+1500 > goal) {
        //          goal = ball;
        //          return true;
        //        }
        //      }
        //    }
        //    return false;
    }

    private void checkGoal(PVector[] ballpositions, int ball, float time) {
        Ball b = simulator.ball;

        if (actualGoal(ballpositions, ball, time) && b.colliding(simulator.goalLeft) && !b.colliding(simulator.fieldArea)) {
            controller.addPointsFor(TeamSide.RIGHT, 1);
        } else if (actualGoal(ballpositions, ball, time) && b.colliding(simulator.goalRight) && !b.colliding(simulator.fieldArea)) {
            controller.addPointsFor(TeamSide.LEFT, 1);
        }
    }


    private void checkEndOfHalf(int[] sections, int frame) {
        duration = (sections[0] + sections[1]) / 25;
        if (frame > sections[0] && gameHalf != 2 && frame < (sections[0] + sections[1] - 10)) {
            setHalf(2);
            System.out.println("Tweede helft begint");
        } else if (frame >= (sections[0] + sections[1] - 10)) {
            // 3 Means end;
            setHalf(3);
        }
    }

    /*
          Did reset everything
         */
    public void onGameControllerReseted() {
        simulator = controller.getSimulator();
        takenOutRobots.clear();
        setHalf(1);

        time = realTime = 0;
    }

    public void onRobotRegistered(Robot r, TeamSide side) {
        takenOutRobots.add(r);
        takenOutTimers.put(r, new Timer(ROBOT_TAKEN_OUTSIDE_TIME));
    }

    public void onRobotUnegistered(Robot r) {
        takenOutRobots.remove(r);
    }

    public void onRobotPlaced(Robot r) {
        takenOutRobots.remove(r);
    }

    public void onRobotRemoved(Robot r) {
        takenOutRobots.add(r);
        takenOutTimers.get(r).reset(time);
    }
}

