package com.abendclyde.triviagame.game;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.abendclyde.triviagame.database.Database;
import com.abendclyde.triviagame.database.Stats;

/*
 * The Trivia class is responsible for managing the game's trivia questions and player data.
 * It provides methods for loading trivia questions, saving player data, and switching players. 
 */
public class Trivia {
    private final Database db;

    private String player;
    private int score;
    private int questionCount;
    private Queue<Question> questions;

    public Trivia(Database db, String player) {
        this.db = db;
        this.player = player;
    }

    // Save the player's data to the database and reset the score and question count
    public void saveData() {
        db.saveData(player, score, questionCount);
        score = 0;
        questionCount = 0;
    }

    // Load the player's statistics from the database
    public List<Stats> loadStats() {
        return db.loadStats();
    }

    public int increaseScore() {
        score++;
        return score;
    }


    public Question nextQuestion() {
        if (!questions.isEmpty()) {
            questionCount++;
        }
        return questions.poll(); // Get the next question
    }

    // Switch the player and reset the score and question count
    public void switchPlayer(String player) {
        this.player = player;
        score = 0;
        questionCount = 0;
    }

    public String getPlayer() {
        return player;
    }

    // Get the OpenTDBs categories through a HTTP request
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();

        try (HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://opentdb.com/api_category.php")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            JSONArray jsonCategories = json.getJSONArray("trivia_categories");
            for (int i = 0; i < jsonCategories.length(); i++) {
                JSONObject jsonCategory = jsonCategories.getJSONObject(i);
                categories.add(new Category(jsonCategory.getInt("id"), jsonCategory.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }
    // Set the combo boxes for the difficulty, question count and answer type
    public List<String> getDifficulties() {
        List<String> difficulties = new ArrayList<>();

        difficulties.add("Any Difficulty");
        difficulties.add("Easy");
        difficulties.add("Medium");
        difficulties.add("Hard");

        return difficulties;
    }

    public List<Integer> getQuestionCount() {
        List<Integer> questionCount = new ArrayList<>();

        questionCount.add(5);
        questionCount.add(10);
        questionCount.add(25);
        questionCount.add(50);

        return questionCount;
    }

    public List<AnswerType> getAnswerType() {
        List<AnswerType> answerTypes = new ArrayList<>();

        answerTypes.add(new AnswerType("", "Any"));
        answerTypes.add(new AnswerType("multiple", "Multiple Choice"));
        answerTypes.add(new AnswerType("boolean", "True or False"));

        return answerTypes;
    }

    // Request questions from the OpenTDB API
    public boolean loadQuestions(int questionCount, Integer categoryID, String difficulty, String answerType) {
        Queue<Question> questions = new LinkedList<>();
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("https://opentdb.com/api.php")
                .append("?amount=").append(questionCount);
        // Append optional parameters
        if(categoryID != null) {
            urlBuilder.append("&category=").append(categoryID);
        }
        if(difficulty != null) {
            urlBuilder.append("&difficulty=").append(difficulty);
        }
        if(answerType != null) {
            urlBuilder.append("&type=").append(answerType);
        }

        // Send the HTTP request and parse the JSON response
        try (HttpClient client = HttpClient.newHttpClient()){
            HttpRequest request = HttpRequest.newBuilder(URI.create(urlBuilder.toString())).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());

            int responseCode = json.getInt("response_code");
            if (responseCode != 0) {
                System.out.println(responseCode);
                return false;
            }

            JSONArray jsonQuestions = json.getJSONArray("results");

            for (int i = 0; i < jsonQuestions.length(); i++) {
                JSONObject jsonQuestion = jsonQuestions.getJSONObject(i);

                JSONArray jsonIncorrectAnswers = jsonQuestion.getJSONArray("incorrect_answers");
                List<String> incorrectAnswers = new ArrayList<>();
                for (int j = 0; j < jsonIncorrectAnswers.length(); j++) {
                    String incorrectAnswer = decodeString(jsonIncorrectAnswers.getString(j));
                    incorrectAnswers.add(incorrectAnswer);
                }
                String text = decodeString(jsonQuestion.getString("question"));
                String correctAnswer = decodeString(jsonQuestion.getString("correct_answer"));
                questions.add(new Question(text, correctAnswer, incorrectAnswers));
            }
            this.questions = questions;
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    // Decode the HTML encoded strings to allow for special characters
    private String decodeString(String s) {
        return StringEscapeUtils.unescapeHtml4(URLDecoder.decode(s, StandardCharsets.UTF_8));
    }
}
