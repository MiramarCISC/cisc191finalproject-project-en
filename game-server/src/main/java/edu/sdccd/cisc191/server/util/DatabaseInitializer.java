package edu.sdccd.cisc191.server.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        String sqlPlayers =
                "CREATE TABLE IF NOT EXISTS players (" +
                        "player_name VARCHAR(100) PRIMARY KEY" +
                        ")";

        String sqlMatches =
                "CREATE TABLE IF NOT EXISTS matches (" +
                        "id VARCHAR(100) PRIMARY KEY," +
                        "player_name VARCHAR(100) NOT NULL," +
                        "opponent_name VARCHAR(100) NOT NULL," +
                        "winner_name VARCHAR(100)," +
                        "difficulty VARCHAR(50)," +
                        "ranked BOOLEAN," +
                        "player_hp INT," +
                        "opponent_hp INT," +
                        "FOREIGN KEY (player_name) REFERENCES players(player_name)" +
                        ")";

        try (Connection connection = DatabaseConfig.getConnection()) {

            Statement stmt = connection.createStatement();

            stmt.addBatch(sqlPlayers);
            stmt.addBatch(sqlMatches);

            stmt.executeBatch();

            System.out.println("Database initialized.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}