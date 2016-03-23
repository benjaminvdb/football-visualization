package nl.liacs.sports.football.visualization;

/**
 * Defines the parameters of a match - both teams and the number of players
 */
public class Match {
    public final Class<? extends Team> TeamAClass;
    public final Class<? extends Team> TeamBClass;
    public final int teamAPlayers;
    public final int teamBPlayers;
    public final int referees;

    Match(
            Class<? extends Team> TeamAClass,
            Class<? extends Team> TeamBClass,
            int teamPlayers) {
        this.TeamAClass = TeamAClass;
        this.TeamBClass = TeamBClass;
        this.teamAPlayers = this.teamBPlayers = Math.max(teamPlayers, 0);
        this.referees = 3;
    }
}

