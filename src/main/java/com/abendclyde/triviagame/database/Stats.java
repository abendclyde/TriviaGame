package com.abendclyde.triviagame.database;

/**
 * The Stats class represents the statistics of a player in a trivia game.
 * It stores the player's name, score, and the total number of questions answered.
 */

public class Stats {

    private final String player;
    private final int score;
    private final int questionCount;

    public Stats(String player, int score, int questionCount) {
        this.player = player;
        this.score = score;
        this.questionCount = questionCount;
    }

    public String getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public int getQuestionCount() {
        return questionCount;
    }
}
