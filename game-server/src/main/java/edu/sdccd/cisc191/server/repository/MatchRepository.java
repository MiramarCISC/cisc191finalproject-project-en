package edu.sdccd.cisc191.server.repository;

import edu.sdccd.cisc191.server.util.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MatchRepository {

    public void saveMatch(
            String matchId,
            String playerName,
            String opponentName,
            String winnerName,
            String difficulty,
            boolean ranked,
            int playerHp,
            int opponentHp
    ) {

        String sql =
                "INSERT INTO matches " +
                        "(id, player_name, opponent_name, winner_name, difficulty, ranked, player_hp, opponent_hp) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, matchId);
            stmt.setString(2, playerName);
            stmt.setString(3, opponentName);
            stmt.setString(4, winnerName);
            stmt.setString(5, difficulty);
            stmt.setBoolean(6, ranked);
            stmt.setInt(7, playerHp);
            stmt.setInt(8, opponentHp);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(); // consider replacing with logger so database error is easier to track
        }
    }

    public List<String> getMatchHistory(String playerName) {

        List<String> history = new ArrayList<>();

        String sql =
                "SELECT opponent_name, winner_name, difficulty, ranked " +
                        "FROM matches " +
                        "WHERE player_name = ? " +
                        "ORDER BY id DESC";

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {

            stmt.setString(1, playerName);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                String opponent = rs.getString("opponent_name");
                String winner = rs.getString("winner_name");
                String difficulty = rs.getString("difficulty");
                boolean ranked = rs.getBoolean("ranked");

                String result;

                if (playerName.equals(winner)) {
                    result = "Win";
                } else {
                    result = "Loss";
                }

                String matchType = ranked ? "Ranked" : "Casual";

                history.add(
                        playerName + " vs " + opponent +
                                " | " + result +
                                " | " + difficulty +
                                " | " + matchType
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }
}