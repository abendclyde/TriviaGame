/**
 * The Database class is responsible for managing the game's database.
 * It provides methods for saving player data, loading player statistics, and initializing the database if necessary.
 */
package com.abendclyde.triviagame.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Database class is responsible for managing the game's database.
 * It provides methods for saving player data, loading player statistics, and initializing the database if necessary.
 */
public class Database {
    private final Connection connection;

    /**
     * Constructs a new Database object and establishes a connection to the database.
     * If the database does not exist, it creates the necessary table to store player statistics.
     */
    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:./data");
            PreparedStatement statement = connection.prepareStatement(
                                "CREATE TABLE IF NOT EXISTS stats (" +
                                "Playername VARCHAR(255), " +  // Spielernamen als VARCHAR mit maximaler Länge 255
                                "Score INT, " +                // Score als Integer"
                                "QuestionCount INT) "          // Anzahl der Fragen als Integer
            );
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the player's data to the database.
     * If the player does not exist in the database, a new entry is created.
     * If the player already exists, their score and question count are updated.
     */
    public void saveData(String player, int score, int questionCount) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM stats WHERE Playername = ?");
            statement.setString(1, player);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                statement = connection.prepareStatement("INSERT INTO stats VALUES (?, ?, ?)");
                statement.setString(1, player);
                statement.setInt(2, score);
                statement.setInt(3, questionCount);
                statement.execute();
                return;
            }

            int oldScore = resultSet.getInt("Score");
            int oldQuestionCount = resultSet.getInt("QuestionCount");

            statement = connection.prepareStatement("UPDATE stats SET Score = ?, QuestionCount = ? WHERE Playername = ?");
            statement.setInt(1, score + oldScore);
            statement.setInt(2, questionCount + oldQuestionCount);
            statement.setString(3, player);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the player statistics from the database and returns them as a list of Stats objects.
     */
    public List<Stats> loadStats() {
        List<Stats> stats = new ArrayList<>();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM stats");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String player = resultSet.getString("Playername");
                int score = resultSet.getInt("Score");
                int questionCount = resultSet.getInt("QuestionCount");
                stats.add(new Stats(player, score, questionCount));
            }
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
