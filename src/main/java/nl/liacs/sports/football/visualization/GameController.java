package nl.liacs.sports.football.visualization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PVector;

public class GameController implements Drawable, Runnable {

    private static final Logger log = LoggerFactory.getLogger(SoccerSimulator.class);

    //indication of the amount of frames per match, edited in getmatchdetails
    public int frames = 150000;
    //first and second half, amount of frames is stored in here
    public int[] sections = new int[2];
    // Array of Robots currently registered in this game, 25, 22 players 3 referees
    public ArrayList<Robot> robots = new ArrayList<Robot>();
    //position of the ball per frame
    public PVector[] ballpositions = new PVector[frames];
    //possitions of the referees in the field. this is in meters. left upper corner = 0.0 PVectors have an x and y
    public PVector[] refposition1 = new PVector[frames];
    public PVector[] refposition2 = new PVector[frames];
    public PVector[] refposition3 = new PVector[frames];
    //possitions of the players in the field. this is in meters. left upper corner = 0.0 PVectors have an x and y
    public PVector[][] playersPos = new PVector[22][frames];
    // Instantied teams
    Team a, b, ref;
    PVector simulatorPos = new PVector(0, 150);
    private GameSimulator simulator;
    private Judge judge;
    // Threads of the Robots, mapped to each Robot
    private HashMap<Robot, Thread> robotThreads = new HashMap<Robot, Thread>();
    // Side of the robots, mapped to each Robot
    private HashMap<Robot, TeamSide> robotSides = new HashMap<Robot, TeamSide>();
    // Match configuration for this Game
    private Match match;
    //Stores Points for both Sides
    private int goalsLeft = 0;
    private int goalsRight = 0;
    //variables to get the data
    private String host = "jdbc:mysql://localhost/psv2";
    private String uName = "root";
    private String uPass = " ";
    private Connection con;
    private Statement stmt;
    //time in simulator
    private float time;
    //frame nummer in simulator
    private int frame = 0;


  /*
  //########################################################################################################################################### variabeles used to calculate distances in the field and direct opponents ect Roy's research 
   //posession per frame, team 1 or team 2
   public int[] ballpossession = new int[frames];
   //frames where the ball changes team posession
   public int[] recaptureFrames = new int[frames];
   
   //distance from ball to team 1, and team 2
   public double[][] ballDistanceToTeam = new double[2][frames];
   
   //Total distance between 10 players and all their opponents per frame
   public double[] PlayersTotalDistance = new double[frames];
   
   //Total distance between 10 players and their direct opponent per frame
   public double[] directOpponentDistance = new double[frames];
   
   //Direct opponent per frame for 10 players, this is visa versa
   public int[][] DirectOpponent = new int[10][frames];
   
   //sum of the total meters of the individual distances between their direct opponent wich will be minimized 
   private double smallestSum = 10000000; 
   
   //############################################################################################################################################# end of variables used for direct opponents
   
   //############################################################################################################################################## functions used to calculate direct opponent
   
   
   //make a matrix of the 10 players with their distance between the opponent team.
   //m[0][0] = team 1 player 2 to team 2 player 2
   //m[0][1] = team 1 player 2 to team 2 player 3
   //m[1][2] = team 1 player 3 to team 2 player 4
   //ect
   //keepers are not in this matrix
   private double[][] makeDistanceMatrix(int frame) {
   double[][] distanceMatrix = new double[10][10];
   for (int i = 1; i < 11; i++) {
   for (int j = 12; j < 22; j++) { 
   double distance = calculateDistance(playersPos[i][frame], playersPos[j][frame]);
   distanceMatrix[i-1][j-12] = distance;
   PlayersTotalDistance[frame] = PlayersTotalDistance[frame] + distance;
   }
   }
   return distanceMatrix;
   }
   
   //make a copy of a 2d double array
   private double[][] copy2dArray(double[][] m) {
   double m2[][] = new double[10][10];
   for (int i = 0; i < 10; i++) {
   for (int j = 0; j < 10; j++) {
   m2[i][j] = m[i][j];
   }
   }
   return m2;
   }
   
   //Assign a player to an direct opponent
   private double assignPlayers(double m[][], int row, int colum) {
   double g = m[row][colum];
   for (int i = 0; i < 10; i++) {
   m[row][i] = -1;
   m[i][colum] = -1;
   }
   return g;
   }
   
   private double minimumDistance(double m[][], int player) {
   double sum2 = 0;
   for (int player1 = player; player<10; player++) {
   double smallest = 1000000;
   for (int opponent = 0; opponent<10; opponent++) {
   if (m[player][opponent]!=-1 && smallest > m[player][opponent]) {
   smallest = m[player][opponent];
   }
   }
   sum2 += smallest;
   }
   return sum2;
   }
   
   //calculates the minimum of the individual distance between the players and their direct opponent given an distance matrix m, 
   //recursive, so try to assign player 1 till 10, and check wich is best
   //opponents is the opponents in these particular assigns
   //frame = framenumer of the distance matrix
   private void minimumsum(double m[][], int player, double sum, int opponents[], int frame) {
   if (smallestSum < sum + minimumDistance(m, player+1)) { //backtracking
   return;
   }
   if (player == 9) { //stopvoorwaarde
   if (smallestSum > sum) {
   smallestSum = sum;
   for (int p = 0; p<10; p++) {
   DirectOpponent[p][frame] = opponents[p];
   }
   }
   } else {
   player++;
   for (int opponent = 0; opponent < 10; opponent++) {
   if (m[player][opponent]!=-1) {
   double m2[][] = copy2dArray(m);
   double meters = assignPlayers(m2, player, opponent);
   opponents[player] = opponent;
   minimumsum(m2, player, sum+meters, opponents, frame);
   }
   }
   }
   }
   
   //print the distance matrix
   private void matrixPrint(double m[][]) {
   for (int i = 0; i < 10; i++) {
   for (int j = 0; j < 10; j++) {
   System.out.print(m[i][j]+" ");
   }
   }
   }
   
   //write distance matrix to file per frame so they can be analyzed.
   private void writeDistanceToFile(double m[][], int frame) {
   try {
   File file = new File("C:/Users/Roy de Winter/Documents/uni/I en E/bep/JavaDistanceMatrix.txt");
   // if file doesnt exists, then create it
   if (!file.exists()) {
   file.createNewFile();
   }
   
   FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
   BufferedWriter bw = new BufferedWriter(fw);
   
   String content = ""+frame+"\n";
   for (int i = 0; i < 10; i++) {
   for (int j = 0; j < 10; j++) {
   content += ""+m[i][j];
   content += " ";
   }
   content += "\n";
   }
   content += "\n";
   bw.write(content);
   bw.close();
   } 
   catch (IOException e) {
   e.printStackTrace();
   }
   }
   
   //write opponents to file so it can be memorized and you do not have to calculate it again
   //framenumer
   //player=opponent1
   //player2=opponent2
   //................
   //player10=opponent10
   private void writeOpponentsToFile(int frame) {
   try {
   File file = new File("C:/Users/Roy de Winter/Documents/uni/I en E/bep/JavaOpponents.txt");
   // if file doesnt exists, then create it
   if (!file.exists()) {
   file.createNewFile();
   }
   
   FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
   BufferedWriter bw = new BufferedWriter(fw);
   
   String content = ""+frame+"\n";
   for (int p = 0; p < 10; p++) {
   content += ""+(p)+"="+(DirectOpponent[p][frame])+"\n";
   }
   content += "\n";
   bw.write(content);
   bw.close();
   } 
   catch (IOException e) {
   e.printStackTrace();
   }
   }
   
   //write minimumsum of the direct opponents per frame to file
   private void writeMetersToFile(double meters, int frame) {
   try {
   File file = new File("C:/Users/Roy de Winter/Documents/uni/I en E/bep/JavaDistanceFrames.txt");
   
   // if file doesnt exists, then create it
   if (!file.exists()) {
   file.createNewFile();
   }
   
   FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
   BufferedWriter bw = new BufferedWriter(fw);
   String content = "";
   content += ""+frame+" "+meters +" "+PlayersTotalDistance[frame]+"\n"; 
   bw.write(content);
   bw.close();
   } 
   catch (IOException e) {
   e.printStackTrace();
   }
   }
   
   //set individual total distance to the opponents, and total distance, this calculations takes a lot of time!
   //started 12:36
   public void setIndividualDistance() {
   for (int frame = 0; frame < frames; frame++) {
   if (frame%100==0)
   System.out.println("distance matrix " + frame);
   smallestSum = 10000000;
   double distanceMatrix[][] = makeDistanceMatrix(frame);
   //matrixPrint(distanceMatrix);
   writeDistanceToFile(distanceMatrix, frame);
   int[] opponents = new int[10];
   minimumsum(distanceMatrix, -1, 0, opponents, frame);
   writeOpponentsToFile(frame);
   directOpponentDistance[frame] = smallestSum;
   writeMetersToFile(smallestSum, frame);
   }
   }
   
   //public double[][] ballDistanceToTeam = new int[2][frames];
   public void setBallTeamDistance(){
   for(int frame = 0; frame<frames; frame++){
   ballDistanceToTeam[0][frame] = 0;
   ballDistanceToTeam[1][frame] = 0;
   for(int player = 0; player<22; player++){
   int team = player / 11; 
   ballDistanceToTeam[team][frame] += calculateDistance(playersPos[player][frame], ballpositions[frame]);
   }
   }
   }
   
   //when is the ball recaptured. and wirte it to a file.
   public void getballPossessionSwitch() {
   int recaptures = 0;
   for (int frame = 1; frame < frames; frame++) {
   if (ballpossession[frame] != ballpossession[frame-1]) {
   recaptureFrames[recaptures] = frame;
   recaptures++;
   }
   }
   writeRecaptureFramesToFile(recaptures);
   }
   
   //use previous calculated direct opponents which can be read from file which looks like this:
   //framenumer
   //player=opponent1
   //player2=opponent2
   //................
   //player10=opponent10
   public void setDirectOpponent() {
   System.out.println("setDirectOpponent");
   try {
   InputStream fis = new FileInputStream("C:/Users/Roy de Winter/Documents/uni/I en E/bep/JavaOpponents.txt");
   InputStreamReader isr = new InputStreamReader(fis);
   BufferedReader reader = new BufferedReader(isr);
   String line;
   for (int frame = 0; frame < frames; frame++) {
   line = reader.readLine();
   frame = Integer.parseInt(line);
   //System.out.println(frame);
   for (int player = 0; player < 10; player++) {
   line = reader.readLine();
   String[] s = line.split("=");
   DirectOpponent[Integer.parseInt(s[0])][frame] = Integer.parseInt(s[1]); // zodat de eerste twee plaatsen in de array items ook gebruikt worden
   //System.out.println(Integer.parseInt(s[0])+"="+DirectOpponent[Integer.parseInt(s[0])][frame]);
   }
   line = reader.readLine();
   }
   }
   catch (Exception e) {
   e.printStackTrace();
   }
   }
   
   //write recapture frames to file so you dont have to calculate it again and it can be reused
   private void writeRecaptureFramesToFile(int recaptures) {
   try {
   File file = new File("C:/Users/Roy de Winter/Documents/uni/I en E/bep/JavaRecaptureFrames.txt");
   
   // if file doesnt exists, then create it
   if (!file.exists()) {
   file.createNewFile();
   }
   
   FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
   BufferedWriter bw = new BufferedWriter(fw);
   
   String content = "";
   for (int i = 0; i < recaptures; i++) {
   int frame = recaptureFrames[i];
   content += ""+frame+" "; 
   double meters = directOpponentDistance[frame];
   content += ""+meters+" ";
   meters = PlayersTotalDistance[frame];
   content += ""+meters +"\n";
   }
   bw.write(content);
   bw.close();
   } 
   catch (IOException e) {
   e.printStackTrace();
   }
   }
   
   public void getOpponents() {
   System.out.println("Get opponents from DB");
   try {
   con = SoccerSimulator.getConnection();
   stmt = con.createStatement();
   for (int section = 0; section<sections.length; section++) {
   String sql = "SELECT player_id as player1, opponent_player_id as player2, frame_id "+
   "FROM players_opponents";
   ResultSet rs = stmt.executeQuery(sql);
   
   while (rs.next ()) {
   int player = rs.getInt("player1")-2;
   int player2 = rs.getInt("player2")-13;
   int frame = rs.getInt("frame_id")-1;
   
   DirectOpponent[player][frame] = player2;
   }
   rs.close();
   }
   stmt.close();
   con.close();
   }
   catch (SQLException err) {
   System.out.println( err.getMessage());
   }
   }
   
   //insert opponents in the database
   public void insertOpponentsInDatabase() {
   System.out.println("Insert players into db");
   try {
   con = SoccerSimulator.getConnection();
   stmt = con.createStatement();
   
   for (int frame = 0; frame<frames; frame++) {
   for (int player = 0; player < 10; player++) {
   String sql = "INSERT INTO players_opponents "+
   "VALUES("+(player+2)+","+(DirectOpponent[player][frame]+13)+","+(frame+1)+")";
   stmt.executeUpdate(sql);
   }
   }
   stmt.close();
   con.close();
   }
   catch (SQLException err) {
   System.out.println( err.getMessage());
   }
   }
   
   public void readDistanceFromFile() {
   System.out.println("setDistance");
   try {
   InputStream fis = new FileInputStream("C:/Users/Roy de Winter/Documents/uni/I en E/bep/JavaDistanceFrames.txt");
   InputStreamReader isr = new InputStreamReader(fis);
   BufferedReader reader = new BufferedReader(isr);
   String line;
   for (int frame = 0; frame < frames; frame++) {
   line = reader.readLine();
   String[] s = line.split(" ");
   frame = Integer.parseInt(s[0]);
   directOpponentDistance[frame] = Double.parseDouble(s[1]);
   PlayersTotalDistance[frame] = Double.parseDouble(s[2]);
   }
   }
   catch (Exception e) {
   e.printStackTrace();
   }
   }
   
   //insert players_distances into database 
   public void insertPressureMeasurements() {
   readDistanceFromFile();
   System.out.println("Insert distances into db");
   try {
   con = SoccerSimulator.getConnection();
   stmt = con.createStatement();
   
   for (int frame = 0; frame<frames; frame++) {
   String sql = "INSERT INTO pressure_measurements "+
   "VALUES("+(frame+1)+","+directOpponentDistance[frame]+","+PlayersTotalDistance[frame]+","+ballDistanceToTeam[0][frame]+","+ballDistanceToTeam[1][frame]+")";
   //System.out.println(sql);                
   stmt.executeUpdate(sql);
   }
   stmt.close();
   con.close();
   }
   catch (SQLException err) {
   System.out.println( err.getMessage());
   }
   }
   
   //get the pressure measurements woohoooee
   public void getPressureMeasurements() {
   System.out.println("getPressureMeasurements from DB");
   try {
   con = SoccerSimulator.getConnection();
   stmt = con.createStatement();
   String sql = "SELECT frame_id as frame, direct_opponent_distance as direct, "+
   "total_opponents_distance as total, ball_team1_distance as bal1, ball_team2_distance as bal2 "+
   "FROM pressure_measurements";
   ResultSet rs = stmt.executeQuery(sql);
   
   while (rs.next ()) {
   int frame = rs.getInt("frame")-1;
   directOpponentDistance[frame] = rs.getDouble("direct");
   PlayersTotalDistance[frame] = rs.getDouble("total");
   ballDistanceToTeam[0][frame] = rs.getDouble("bal1");
   ballDistanceToTeam[1][frame] = rs.getDouble("bal2");
   }
   rs.close();
   stmt.close();
   con.close();
   }
   catch (SQLException err) {
   System.out.println( err.getMessage());
   }
   }
   
   //draw line between the direct opponents.
   private void drawline(PApplet canvas, float scale){
   canvas.translate(simulatorPos.x, simulatorPos.y);
   for (int player = 0; player < 10; player++) {
   int player2 = DirectOpponent[player][frame];
   
   PVector player1location = playersPos[player+1][frame];//keeper heeft geen tegenstander, keeper = 0;
   PVector player2location = playersPos[player2+12][frame];//keeper en eerste team overslaan
   
   float x1 = player1location.x*scale;
   float y1 = player1location.y*scale;
   float x2 = player2location.x*scale;
   float y2 = player2location.y*scale;
   if (!redcard(player1location, player2location)) {
   canvas.line(x1, y1, x2, y2);
   }
   }
   canvas.translate(-simulatorPos.x, -simulatorPos.y);
   }
   
   
   //############################################################################################################################################## end of calculation functions for the direct opponent
   */
    //total time past 45 minutes so 2nd half will start at 45:00
    private float till45min = 0;
    private boolean lastside = true;
    // If simulation started, or not
    private boolean started = false;
    // If game is running or not
    private boolean running = false;

    GameController(Match match) {
        this.match = match;

        // Create Judge
        judge = new Judge(this);

        // Create game Simulator
        simulator = new GameSimulator();

        // Reset everything
        resetGame();
    }

    public GameSimulator getSimulator() {
        return simulator;
    }

    /*
          Get points for one side
         */
    public int getPointsFor(TeamSide side) {
        if (side == TeamSide.LEFT)
            return goalsLeft;
        else
            return goalsRight;
    }

    /*
          Add points to one side
         */
    public void addPointsFor(TeamSide side, int points) {
        System.out.println("Add points for " + side + ":" + points);
        if (side == TeamSide.LEFT)
            goalsLeft += points;
        else
            goalsRight += points;
    }

    private void setSideInvertion(boolean inverted) {
        if (lastside != inverted) {
            int lastGoalsLeft = goalsLeft;
            int lastGoalsRight = goalsRight;
            goalsLeft = lastGoalsRight;
            goalsRight = lastGoalsLeft;
        }

        simulator.field.setColorInvertion(inverted);
    }

    //To translate the data to real meters in the field. -1<~data_x<~1 & -1<~data_y<~1
    //Returns a pvector which indicates a position on the field.
    private PVector convert(float data_x, float data_y) {
        float x = 0f;
        float y = 0f;
        if (data_x > 0) {
            x = 52.5f + 52.5f * data_x;
        } else {
            x = 52.5f + 52.5f * data_x;
        }
        if (data_y > 0) {
            y = 34f + 34f * data_y;
        } else {
            y = 34f + 34f * data_y;
        }
        return new PVector(x, y);
    }

    //To translate the data to real meters in the field. -1<~data_x<~1 & -1<~data_y<~1
    //Returns a pvector which indicates a position on the field. ball can have a height.
    private PVector convert(float data_x, float data_y, float data_z) {
        float x = 0f;
        float y = 0f;
        if (data_x > 0) {
            x = 52.5f + 52.5f * data_x;
        } else {
            x = 52.5f + 52.5f * data_x;
        }
        if (data_y > 0) {
            y = 34f + 34f * data_y;
        } else {
            y = 34f + 34f * data_y;
        }
        return new PVector(x, y, data_z);
    }

    //checks if the player got a red card.
    private boolean redcard(PVector pos1, PVector pos2) {
        PVector rodeKaart = convert(-10, -10);
        if ((rodeKaart.x == pos1.x && rodeKaart.y == pos1.y) || (rodeKaart.x == pos2.x && rodeKaart.y == pos2.y)) {
            return true;
        }
        return false;
    }

    //calculate the distance between two positions.
    //if distance between two frames from a player or ball it is also meters per 1/25th second
    //if someone has a red card the distance is 0 because he is out of the game
    private double calculateDistance(PVector pos1, PVector pos2) {
        //only needed for players....
//        if (redcard(pos1, pos2)) {
//            return 0;
//        }
        float x = pos1.x;
        float y = pos1.y;
        float x2 = pos2.x;
        float y2 = pos2.y;
        return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2)); //also meters per 1/25 second;
    }

    //max ball speed is about 144 kmph = 40 meter per second
    //max running speed is about 36 kmph = 10 meter per second
    public void validateData() {
        log.info("validating data...");
        int walkerrors = 0;
        int ballerrors = 0;
        for (int frame = 0; frame < frames - 1; frame++) {
            log.trace("calculateDistance({}, {})", ballpositions[frame], ballpositions[frame + 1]);
            if (ballpositions[frame] == null || ballpositions[frame+1] == null) {
                log.warn("skipped frame {}", frame);
                continue;
            }

            double ballSpeedMPS = calculateDistance(ballpositions[frame], ballpositions[frame + 1]) * 25;//mps
            if (ballSpeedMPS >= 40) {
                //System.out.println("wrong ball data at frame: "+frame + " With ball speed in MPS: " + ballSpeedMPS);
                ballerrors++;
            }
            for (int player = 0; player < 22; player++) {
                double walkingSpeedMPS = calculateDistance(playersPos[player][frame], playersPos[player][frame + 1]) * 25;//mps
                if (walkingSpeedMPS >= 10) {
                    //System.out.println("player " +player+ " has wrong data at frame: "+frame + " With walking speed in MPS: " + walkingSpeedMPS);
                    walkerrors++;
                }
            }
        }
        System.out.println(walkerrors + " player data fouten, " + ballerrors + " ball data errors");
    }

    //get match details like frames, and how manny sections
    public void getMatchDetails() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        System.out.println("match details");
        try {
//            con = SoccerSimulator.getConnection();
            Connection con = SoccerSimulator.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute("SELECT * FROM players LIMIT 1");
            ResultSet rs2 = stmt.getResultSet();
            System.out.println(rs2.getString(2));

            stmt = con.createStatement();
            String sql = "select max(frame_number) as framess, frames.section from frames group by frames.section;";
            ResultSet rs = stmt.executeQuery(sql);
            frames = 0;
            while (rs.next()) {
                frames += rs.getInt("framess");
                sections[rs.getInt("frames.section") - 1] = rs.getInt("framess");
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    //get referee data from the database
    public void getReferees() {
        System.out.println("Get referees");
        try {
            con = SoccerSimulator.getConnection();
            stmt = con.createStatement();
            ResultSet rs;
            String sql = "SELECT referee_measurement.x, referee_measurement.y, referee_measurement.referee_id, frames.frame_id " +
                    "FROM referee_measurement, frames " +
                    "WHERE referee_measurement.frame_id = frames.frame_id " +
                    "ORDER BY frames.frame_id";

            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                float data_x = rs.getFloat("x");
                float data_y = rs.getFloat("y");
                int data_ref_id = rs.getInt("referee_id");
                int i = rs.getInt("frame_id") - 1;

                switch (data_ref_id) {
                    case 1:
                        refposition1[i] = convert(data_x, data_y);
                        break;
                    case 2:
                        refposition2[i] = convert(data_x, data_y);
                        break;
                    case 3:
                        refposition3[i] = convert(data_x, data_y);
                        break;
                }
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    //get players data from the database
    public void getPlayers() {
        System.out.println("Get players");
        try {
            con = SoccerSimulator.getConnection();
            stmt = con.createStatement();
            String sql = "SELECT player_measurements.player_id, player_measurements.x, player_measurements.y, frames.frame_id " +
                    "FROM player_measurements, frames " +
                    "WHERE player_measurements.frame_id = frames.frame_id " +
                    "ORDER BY frames.frame_id";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                float data_x = rs.getFloat("x");
                float data_y = rs.getFloat("y");
                int data_player_id = rs.getInt("player_id");
                int i = rs.getInt("frame_id") - 1;
                playersPos[data_player_id - 1][i] = convert(data_x, data_y);
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    //get ball data from the database
    public void getball() {
        System.out.println("Get ball");
        try {
            con = SoccerSimulator.getConnection();
            stmt = con.createStatement();

            String sql = "SELECT ball_measurements.x, ball_measurements.y, ball_measurements.z, ball_measurements.possession, frames.frame_id " +
                    "FROM ball_measurements, frames " +
                    "WHERE ball_measurements.frame_id = frames.frame_id " +
                    "ORDER BY frame_id";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                float data_x = rs.getFloat("x");
                float data_y = rs.getFloat("y");
                float data_z = rs.getFloat("z");
                int i = rs.getInt("frame_id") - 1;

                ballpositions[i] = convert(data_x, data_y, data_z);
                //ballpossession[i] = rs.getInt("possession");   //is not needed for the visualisation, can be left out
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    //set the players state during the game, stop at 22 because the last 3 are referees
    public void setPlayersState(int frame) {
        int i = 0;
        for (Robot r : robots) {
            if (i == 22) {
                break; //last 3 are referees
            }
            r.setState(playersPos[i][frame], i + 1);
            i++;
        }
    }

    //set referees state during the game
    public void setRefsState(int frame) {
        Robot ref1 = robots.get(22);
        Robot ref2 = robots.get(23);
        Robot ref3 = robots.get(24);
        ref1.setState(refposition1[frame], 22);
        ref2.setState(refposition2[frame], 23);
        ref3.setState(refposition3[frame], 24);
    }

    //#######################################################################WORDT CONTINUE UITGEVOERD##########################
    //sets the positions of the ball and players ect per frame
    public void run() {
        // Skip simulation if not started
        if (!hasStarted())
            return;

        //zodat de 2e helft op 45:00 begint
        if (frame > sections[0]) {
            till45min = sections[0] - (45 * 60 * 25);
        }

        simulator.simulate(time);

        judge.judge(time, ballpositions, frame, sections);

        if (isRunning()) {
            time = frame / 25 - till45min / 25;//2e helft begint op 45 minuten... als de data uit de database klopt teminste
            setPlayersState(frame);
            setRefsState(frame);
            simulator.ball.setPosition(ballpositions[frame]);
            if (frame < frames)
                frame++;
        }
    }

    //###########################################################################################################################
    //pres s to skip a minute
    public void skipMinute() {
        if (frame + 1500 < frames) {
            frame = frame + 1500;
        }
    }

    //pres p to go back a minute
    public void PreviousMinute() {
        if (frame - 1500 > 0) {
            frame = frame - 1500;
            if (sections[0] < frames) {
                till45min = 0;
            }
        }
    }

    /*
          ***** State controllers *****
         */
    public void resetGame() {
        // Restart start time
        //startTime = System.currentTimeMillis();

        // Reset Points
        goalsLeft = goalsRight = 0;

        // Reset game Simulator
        simulator.reset();

        // Setup Team Sides and instantiate robots
        initTeamSides(true);

        // Restart positions
        restartPositions(TeamSide.LEFT);

        // Delegate reset to judge
        judge.onGameControllerReseted();

        started = false;
    }

    public void initTeamSides(boolean invertSide) {

        // Remove Robots
        while (robots.size() > 0) {
            unRegisterRobot(robots.get(0));
        }
        try {
            a = (Team) (invertSide ? match.TeamBClass : match.TeamAClass).newInstance();
            b = (Team) (invertSide ? match.TeamAClass : match.TeamBClass).newInstance();
            ref = (Team) (invertSide ? match.TeamAClass : match.TeamBClass).newInstance();
        } catch (Exception e) {
            System.out.println(e.toString());
            System.exit(1);
            return;
        }

        a.setTeamSide(TeamSide.LEFT);
        b.setTeamSide(TeamSide.RIGHT);
        ref.setTeamSide(TeamSide.REF);

        // Build Robots for Team A
        for (int i = 0; i < (invertSide ? match.teamBPlayers : match.teamAPlayers); i++) {
            Robot ar = a.buildRobot(simulator, i);
            if (ar != null) {
                ar.setTeamColor(invertSide ? 0xFFFFFF00 : 0xFF0000FF);
                registerRobot(ar, TeamSide.LEFT);
            }
        }

        // Build Robots for Team B
        for (int i = 0; i < (invertSide ? match.teamAPlayers : match.teamBPlayers); i++) {
            Robot br = b.buildRobot(simulator, i);
            if (br != null) {
                br.setTeamColor(invertSide ? 0xFF0000FF : 0xFFFFFF00);
                registerRobot(br, TeamSide.RIGHT);
            }
        }
        for (int i = 0; i < 3; i++) {
            Robot refr = ref.buildRobot(simulator, i);
            if (refr != null) {
                refr.setTeamColor(0);
                registerRobot(refr, TeamSide.REF);
            }
        }


        // Fix points, by inverting them if needed
        setSideInvertion(invertSide);
    }

    public boolean hasStarted() {
        return started;
    }

    public boolean isRunning() {
        return running;
    }

    public void restartPositions(TeamSide vantage) {
        restartTeamPosition(TeamSide.LEFT, vantage == TeamSide.LEFT);
        restartTeamPosition(TeamSide.RIGHT, vantage == TeamSide.RIGHT);
        restartTeamPosition(TeamSide.REF, vantage == TeamSide.REF);
    }

    public void restartTeamPosition(TeamSide side, boolean vantage) {

        // Count robots
        int count = 0;
        for (Robot r : robots) {
            // Skip robots that are not from this team side
            if (robotSides.get(r) == side)
                count++;
        }

        // Get center of the field
        PVector start = simulator.fieldCenter.get();

        float fieldW = getSimulator().getFieldWidth();
        float fieldH = getSimulator().getFieldHeight();

        float offsetX = fieldW / (vantage ? 8 : 4);
        float offsetY = fieldH / (count + 1);

        start.x += (side == TeamSide.LEFT ? -1 : 1) * offsetX;
        start.y -= fieldH / 2;

        for (Robot r : robots) {
            if (robotSides.get(r) != side)
                continue;

            start.y += offsetY;
            placeRobot(r, start);
        }
    }

    public void resumeGame() {
        // First place robots and add them to simulatables, then start threads,
        // otherwise we can get concurrency exceptions when reading the
        // collection of simulatables
        // for(Robot r:robots)
        // placeRobot(r);

        for (Robot r : robots)
            startRobot(r);

        simulator.ball.setOn(true);

        running = true;
        started = true;
    }

    public void pauseGame() {
        for (Robot r : robots) {
            pauseRobot(r);

            try {
                Thread.sleep(2);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        simulator.ball.setOn(false);

        running = false;
    }

    public void draw(PApplet canvas, float scale) {

        canvas.translate(simulatorPos.x, simulatorPos.y);
        simulator.draw(canvas, scale);
        canvas.translate(-simulatorPos.x, -simulatorPos.y);

        canvas.fill(255);

        // Draw Score
        canvas.textSize(48);

        String text = getPointsFor(TeamSide.LEFT) + " x " + getPointsFor(TeamSide.RIGHT);
        canvas.text(text, getWidth(scale) / 2, 50);

        // Draw Time
        canvas.textSize(34);
        canvas.fill(0);
        canvas.textAlign(PApplet.CENTER);
        float time = judge.getCurrentTime();
        String mins = (int) (time / 60) + "";
        String secs = ((int) time) % 60 + "";
        if (secs.length() < 2)
            secs = "0" + secs;
        canvas.text(mins + ":" + secs, getWidth(scale) / 2, 90);

        // Draw State
        canvas.textSize(24);
        canvas.fill(200);
        canvas.textAlign(PApplet.CENTER);
        text = judge.getCurrentState();
        canvas.text(text, getWidth(scale) / 2, 120);


        // Print Team Name on both sides
        canvas.fill(255);
        if (a != null) {
            canvas.textSize(28);
            canvas.textAlign(PApplet.LEFT);
            canvas.text(a.getTeamName(), 20, 50);
            canvas.textSize(16);
            //canvas.text(a.getClass().getSimpleName(), 20, 70);
        }

        if (b != null) {
            canvas.textSize(28);
            canvas.textAlign(PApplet.RIGHT);
            canvas.text(b.getTeamName(), getWidth(scale) - 20, 50);
            canvas.textSize(16);
            //canvas.text(b.getClass().getSimpleName(), getWidth(scale) - 20, 70);
        }

        //draw line to future him so you know his direction and speed
        canvas.translate(simulatorPos.x, simulatorPos.y);
        for (int player = 0; player < 22; player++) {
            PVector currentPosition = playersPos[player][frame];
            PVector futurePosition = playersPos[player][frame + 25]; //position in 1 second will cause cause an nullpointer exception 1 second before the end

            float x1 = currentPosition.x * scale;
            float y1 = currentPosition.y * scale;
            float x2 = futurePosition.x * scale;
            float y2 = futurePosition.y * scale;
//            if (!redcard(currentPosition, futurePosition)) {
                canvas.line(x1, y1, x2, y2);
//            }
        }
        canvas.translate(-simulatorPos.x, -simulatorPos.y);

        //draw line between the direct opponents.
        //drawline(canvas, scale); //comment this out if you dont load players opponents
    }

    /*
          Returns robot list
         */
    public ArrayList<Robot> getRobots() {
        return robots;
    }

    /*
          Only Places robot in the field, but don't run it
         */
    protected void placeRobot(Robot r, PVector position, float orientation) {
        simulator.addToSimulation(r);
        r.setState(position.get(), 0);
        judge.onRobotPlaced(r);
        r.onStateChanged("PLACED");
    }

    /*
          Only Places robot in the field, but don't run it
         */
    protected void placeRobot(Robot r, PVector position) {
        TeamSide side = robotSides.get(r);
        float orientation = (float) (side == TeamSide.LEFT ? 0 : Math.PI);

        placeRobot(r, position, orientation);
    }


    protected void startRobot(Robot r) {

        Thread t = robotThreads.get(r);
        if (!t.isAlive())
            t.start();
        else
            t.resume();

        r.onStateChanged("STARTED");
    }

    /*
          Pauses a robot, but it remains in the field
             What happens:
                 Thread is paused
                 Motors are stopped
         */
    protected void pauseRobot(Robot r) {
        Thread t = robotThreads.get(r);
        t.suspend();
        r.onStateChanged("PAUSED");
    }

    /*
          Stop robot Thread
             What happens:
                 Robot is Paused
                 Robot is removed from simulation
         */
    protected void removeRobot(Robot r) {
        pauseRobot(r);
        simulator.removeFromSimulation(r);
        judge.onRobotRemoved(r);
        r.onStateChanged("REMOVED");
    }

    /*
          Add a new robot
         */
    protected void registerRobot(Robot r, TeamSide side) {


        robots.add(r);

        // Creates a thread for this robot
        Thread robotThread = new Thread(r);
        robotThreads.put(r, robotThread);
        robotSides.put(r, side);

        judge.onRobotRegistered(r, side);

        r.onStateChanged("ADDED");
    }

    /*
          Stops robot and removes it
         */
    protected void unRegisterRobot(Robot r) {
        //System.out.println("unRegisterRobot: "+this);
        //Remove robot from simulation
        removeRobot(r);

        // Remove robot from everywhere
        robotThreads.remove(r);
        robotSides.remove(r);
        robots.remove(r);

        judge.onRobotUnegistered(r);
    }

    /*
          Calculate width of view
         */
    public float getWidth(float scale) {
        return simulatorPos.x + simulator.field.width * scale;
    }

    /*
          Calculate height of view
         */
    public float getHeight(float scale) {
        return simulatorPos.y + simulator.field.height * scale;
    }
}

