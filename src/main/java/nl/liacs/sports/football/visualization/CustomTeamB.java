package nl.liacs.sports.football.visualization;

import processing.core.PApplet;

public class CustomTeamB implements Team {

    public String getTeamName() {
        return "Visiting Team";
        //return team_name;
    }

    public void setTeamSide(TeamSide side) {
    }

    public Robot buildRobot(GameSimulator s, int index) {
        return new EmptyRobot(s);
    }

    class EmptyRobot extends RobotBasic {
        EmptyRobot(GameSimulator s) {
            super(s);
        }

        public void setup() {
        }

        public void loop() {
        }


        public void decorateRobot(PApplet canvas) {
        }


        public void onStateChanged(String state) {
        }
    }
}

