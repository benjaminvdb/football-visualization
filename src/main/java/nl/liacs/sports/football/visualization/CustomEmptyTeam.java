package nl.liacs.sports.football.visualization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import processing.core.PApplet;


public class CustomEmptyTeam implements Team {
    private String host = "jdbc:mysql://localhost/psv2";
    private String uName = "root";
    private String uPass = " ";

    private Connection con;

    private Statement stmt;

    public String getTeamName() {
        String team_name = "Custom Team";
        try {

            con = SoccerSimulator.getConnection();
            stmt = con.createStatement();
            String sql = "SELECT name FROM teams WHERE team_id = 1";

            ResultSet rs = stmt.executeQuery(sql);
            int i = 0;

            if (rs.next()) {
                team_name = rs.getString("name");
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
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

