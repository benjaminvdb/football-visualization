package nl.liacs.sports.football.visualization;

class RobotBasic extends Robot {

    RobotBasic(GameSimulator g) {
        super(g);
    }

    /*
          Arduino-like flux
         */
    public void run() {
        setup();

        while (true) {
            loop();
            delay(1);
        }
    }

    public void setup() {
    }

    ;

    public void loop() {
    }

    ;
}

