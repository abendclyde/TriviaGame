package com.abendclyde.triviagame.game;

import java.util.List;

/**
 * Represents a question in a trivia game, saving the prompt text, the correct answer and x incorrect answers.
 */

public class Question {

    private final String text;
    private final String correctAnswer;
    private final List<String> incorrectAnswers;

    public Question(String text, String correctAnswer, List<String> incorrectAnswers) {
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    public String getText() {
        return text;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }
}
