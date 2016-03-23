package nl.liacs.sports.football.visualization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import processing.core.PApplet;


public class CustomEmptyTeam implements Team {

    public String getTeamName() {
        return "Home Team";
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

