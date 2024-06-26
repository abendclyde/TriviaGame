package com.abendclyde.triviagame;

import com.abendclyde.triviagame.database.Database;
import com.abendclyde.triviagame.game.Trivia;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Main Class responsible for First Time Initializing the App

public class TriviaGame extends Application {
    private final Trivia trivia = new Trivia(new Database(), "Unknown");;

    @Override
    public void start(Stage stage) throws IOException {
        trivia.getCategories();

        FXMLLoader loader = new FXMLLoader(TriviaGame.class.getResource("triviagame.fxml"));
        Parent parent = loader.load();

        TriviaController controller = loader.getController();
        controller.setStage(stage);
        controller.setTrivia(trivia);

        Scene scene = new Scene(parent, 800, 600);
        stage.setTitle("Awesome Trivia Game!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop(){
        trivia.saveData();
    }

    public static void main(String[] args) {
        launch();
    }
}