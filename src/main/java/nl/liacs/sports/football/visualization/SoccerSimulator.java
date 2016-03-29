package nl.liacs.sports.football.visualization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import processing.core.PApplet;
import processing.core.PVector;

public class SoccerSimulator extends PApplet {

    private static final Logger log = LoggerFactory.getLogger(SoccerSimulator.class);

    public static Connection con;

    public static void main(String args[]) {
        PApplet.main("nl.liacs.sports.football.visualization.SoccerSimulator");
    }

    GameController controller;

    float SCALE = 10f;


    public static Connection getConnection() {
        String username = "root";
        String password = "";
        String host = "127.0.0.1";
        String port = "3306";

        log.debug("connecting to database using {}:{}@{}:{}", username, password, host, port);

        Properties props = new Properties();
        props.put("user", username);
        props.put("password", password);

        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/psv?autoReconnect=true&useSSL=false",
                    props);
            log.info("connected to database");
            return conn;
        } catch (ClassNotFoundException|SQLException e) {
            log.error("couldn't connect to database. exiting...");
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    @Override
    public void settings() {
    /*
    * Define the teams for the current match here
    */
        controller = new GameController(new Match(
                // Team A Class
                CustomEmptyTeam.class,
                // Team B Class
                CustomTeamB.class,
                // Number of robots on each side
                11
        ));
        controller.getSimulator().setFieldSize(105f, 68f);

        try {
            controller.getMatchDetails();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        controller.getball();
        controller.getPlayers();
        controller.getReferees();

        /* We don't need this on every start. */
//        controller.validateData();

        //######### do some calculations
        //controller.getPressureMeasurements();
        //controller.setBallTeamDistance();
        //controller.getOpponents();
        //controller.setIndividualDistance();
        //controller.getballPossessionSwitch();
        //controller.inserDistanceInDatabase();
        //controller.setDirectOpponent();
        //controller.getballPossessionSwitch();
        size((int)controller.getWidth(SCALE) + 200, (int)controller.getHeight(SCALE)+100);
    }

    @Override
    public void draw() {

        background(255);
        controller.run();

        translate(100, 0);
        controller.draw(this, SCALE);
        translate(-100, 0);
    }

    /* Finds out what is closer to the ball that can be moved, and then move to that position */
    @Override
    public void mouseDragged() {
        // Checks what is closest to the mouse cursos (Robots and Ball)
        PVector mousePoint = new PVector((mouseX - 100) / SCALE, (mouseY - 150) / SCALE);

        float closestDist = 0.1f;
        Simulatable closest = controller.getSimulator().ball;

        for (Simulatable s : controller.getSimulator ().simulatables) {
            // Skip if not Ball or Robot
            if (!(s instanceof Ball || s instanceof Robot))
                continue;

            float dist = PVector.sub(s.getRealPosition(), mousePoint).mag();
            if (closestDist > dist) {
                closestDist = dist;
                closest = s;
            }
        }

        if (closest != null) {
            closest.position.set(mousePoint);
            closest.speed = new PVector();
            closest.accel = new PVector();
        }
    }

    @Override
    public void keyPressed() {
        if (key == ' ') {
            if (!controller.hasStarted()) {
                System.out.println("Start game");
                controller.resetGame();
                controller.resumeGame();
            } else if (controller.isRunning()) {
                System.out.println("Pause game");
                controller.pauseGame();
            } else {
                System.out.println("Resume game");
                controller.resumeGame();
            }
        } else if (key == 'd') {
            String debug = "DEBUG:";
            debug += "\nisRunning:"+controller.isRunning();
            debug += "\nController Robots:"+controller.robots.size();
            for (Robot r : controller.robots)
                debug += "\n\t"+r+" ["+r.position.x+","+r.position.y+"]";

            debug += "\nSimulatables:"+controller.getSimulator().simulatables.size();
            for (Simulatable r : controller.getSimulator ().simulatables)
                debug += "\n\t"+r;

            System.out.println(debug);
        } else if (key == 'r') {
			System.out.println("Speed of time is toggled");
			controller.toggleSpeed();
		} else if (key == 's') {
            System.out.println("Skip 1 minute");
            controller.skipMinute();
        } else if (key == 'p') {
            System.out.println("go back 1 minute");
            controller.PreviousMinute();
        } else if (key == 'v') {
            System.out.println("Toggle Voronoi Cells");
            controller.toggleComputeVoronoiCells();
        }
    }
}