package edu.sdccd.cisc191.server.repository;

import edu.sdccd.cisc191.server.util.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerRepository {

    public void savePlayer(
            String playerName
    ) {

        String sql =
                "MERGE INTO players (player_name) KEY(player_name) VALUES (?)";


        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, playerName);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}