package com.abendclyde.triviagame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.abendclyde.triviagame.database.Stats;
import com.abendclyde.triviagame.game.AnswerType;
import com.abendclyde.triviagame.game.Category;
import com.abendclyde.triviagame.game.Question;
import com.abendclyde.triviagame.game.Trivia;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TriviaController {
    private Stage stage;
    private Trivia trivia;

    private int correctButton;

    @FXML
    private ComboBox<Category> topicBox;
    String topicDefault = "Any Category";
    @FXML
    private ComboBox<String> difficultyBox;
    String difficultyDefault = "Any Difficulty";
    @FXML
    private ComboBox<Integer> countBox;
    String countDefault = "Count";
    @FXML
    private ComboBox<AnswerType> answerTypeBox;
    String answerTypeDefault = "Any Answer Type";
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab startTab;
    @FXML
    private Tab statsTab;
    @FXML
    private Tab gameTab;
    @FXML
    private Button startButton;
    @FXML
    private Text errorDisplay;
    @FXML
    private Text usernameDisplay;

    @FXML
    private ComboBox<Category> categoryStatsPickerBox;
    @FXML
    private TableView<ObservableList<?>> statsTable;
    @FXML
    private TableColumn<ObservableList<?>, String> userColumn;
    @FXML
    private TableColumn<ObservableList<?>, Integer> totalScoreColumn;
    @FXML
    private TableColumn<ObservableList<?>, Integer> totalRoundsColumn;
    @FXML
    private TableColumn<ObservableList<?>, Double> catRightColumn;

    @FXML
    private Text score;
    @FXML
    private Text promptDisplay;
    @FXML
    private Button answer1;
    @FXML
    private Button answer2;
    @FXML
    private Button answer3;
    @FXML
    private Button answer4;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Prepare all the ComboBoxes with the Data from the Trivia-Class
    public void setTrivia(Trivia trivia) {
        this.trivia = trivia;

        topicBox.getItems().addAll(trivia.getCategories());
        topicBox.setItems(topicBox.getItems().sorted());
        topicBox.setPromptText(topicDefault);

        difficultyBox.getItems().addAll(trivia.getDifficulties());
        difficultyBox.setPromptText(difficultyDefault);

        answerTypeBox.getItems().addAll(trivia.getAnswerType());
        answerTypeBox.setPromptText(answerTypeDefault);

        countBox.getItems().addAll(trivia.getQuestionCount());
        countBox.setPromptText(countDefault);

        // Set the Default Value for the ComboBoxes
        topicBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(topicBox.getPromptText());
                } else {
                    setText(item.toString());
                }
            }
        });

        difficultyBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(difficultyBox.getPromptText());
                } else {
                    setText(item);
                }
            }
        });

        answerTypeBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(AnswerType item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(answerTypeBox.getPromptText());
                } else {
                    setText(item.toString());
                }
            }
        });

        fillCatPickBox();
    }

    // Fill the Category Picker Box in the Statistics Tab
    public void fillCatPickBox() {
        categoryStatsPickerBox.getItems().addAll(trivia.getCategories());
        categoryStatsPickerBox.setItems(categoryStatsPickerBox.getItems().sorted());
        categoryStatsPickerBox.setPromptText(topicDefault);
    }

    // Function handling the switch and opening of the Switch Player Popup
    @FXML
    protected void openSwitchPlayer() throws IOException {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(stage);

        FXMLLoader loader = new FXMLLoader(TriviaGame.class.getResource("changeuser.fxml"));
        Parent parent = loader.load();

        SwitchPlayerController controller = loader.getController();
        controller.setTrivia(trivia);
        controller.setPopup(popupStage);

        Scene scene = new Scene(parent);
        popupStage.setTitle("Switch Player");
        popupStage.setScene(scene);
        popupStage.show();
    }

    /* 
    Function handling if the player selected a question count. 
    An error will be displayed if not.
    Also switches the view to the gametab and prevents misclicks back to the starttab
    */ 
    public void startGame() {
        if(countBox.getValue() == null) {
            errorDisplay.setVisible(true);
            return;
        }

        Integer categoryID = null;
        if (topicBox.getValue() != null) {
            categoryID = topicBox.getValue().getId();
        }
        String difficulty = null;
        if (difficultyBox.getValue() != null) {
            difficulty = difficultyBox.getValue();
        }
        String answerType = null;
        if (answerTypeBox.getValue() != null) {
            answerType = answerTypeBox.getValue().getId();
        }

        trivia.loadQuestions(countBox.getValue(), categoryID, difficulty, answerType);
        loadQuestion();

        gameTab.setDisable(false);
        tabPane.getSelectionModel().select(gameTab);
        errorDisplay.setVisible(false);
        startTab.setDisable(true);
    }


    private void loadQuestion() {
        Question question = trivia.nextQuestion();

        // If there are no more questions, save the data and switch back to the start tab
        if (question == null) {
            trivia.saveData();
            startTab.setDisable(false);
            tabPane.getSelectionModel().select(startTab);
            gameTab.setDisable(true);
            return;
        }

        promptDisplay.setText(question.getText()); // Set the Question Text on the Prompt Display

        String correctAnswer = question.getCorrectAnswer();
        List<String> incorrectAnswers = question.getIncorrectAnswers();

        correctButton = ThreadLocalRandom.current().nextInt(0, incorrectAnswers.size() + 1);

        // Add the correct answer to the list of answers and shuffle them
        List<String> answers = new ArrayList<>(incorrectAnswers);
        Collections.shuffle(answers);
        answers.add(correctButton, correctAnswer);
        
        // Set the answers on the buttons
        answer1.setText(answers.get(0));
        answer1.setTooltip(new Tooltip(answers.get(0)));
        answer2.setText(answers.get(1));
        answer2.setTooltip(new Tooltip(answers.get(1)));
        if (answers.size() > 2) { // If there are more than 2 answers, set them
            answer3.setVisible(true);
            answer3.setText(answers.get(2));
            answer3.setTooltip(new Tooltip(answers.get(2)));
            answer4.setVisible(true);
            answer4.setText(answers.get(3));
            answer4.setTooltip(new Tooltip(answers.get(3)));
        } else {
            answer3.setVisible(false);
            answer4.setVisible(false);
        }
    }
    // Functions for resetting each ComboBox
    public void resetTopic(){
        topicBox.getSelectionModel().clearSelection();
    }

    public void resetDifficulty(){
        difficultyBox.getSelectionModel().clearSelection();
        difficultyBox.setValue(difficultyDefault);
    }

    public void resetAnswerType(){
        answerTypeBox.getSelectionModel().clearSelection();
    }

    // Functions for clicking an answer button with a check if it's the right one
    public void answerClick1(){
        if(correctButton == 0) {
            increaseScore();
        }
        loadQuestion();
    }

    public void answerClick2(){
        if(correctButton == 1) {
            increaseScore();
        }
        loadQuestion();
    }

    public void answerClick3(){
        if(correctButton == 2) {
            increaseScore();
        }
        loadQuestion();
    }

    public void answerClick4(){
        if(correctButton == 3) {
            increaseScore();
        }
        loadQuestion();
    }

    // Function for increasing the score display
    private void increaseScore() {
        score.setText(String.format("Score: %d", trivia.increaseScore()));
    }

    // Function for preparing the TableView when opening the stats view tab
    /**
     * Opens the stats view and populates the stats table with data.
     * This method binds the data to the stats table and starts a service to load the stats asynchronously.
     * The stats table will display the player name, total score, total rounds, and category right percentage for each player.
     */
    public void onOpenStatsView() {
        userColumn.setCellValueFactory(cdf -> new SimpleStringProperty((String) cdf.getValue().get(0)));
        totalScoreColumn.setCellValueFactory(cdf -> new SimpleIntegerProperty((Integer) cdf.getValue().get(1)).asObject());
        totalRoundsColumn.setCellValueFactory(cdf -> new SimpleIntegerProperty((Integer) cdf.getValue().get(2)).asObject());
        catRightColumn.setCellValueFactory(cdf -> new SimpleDoubleProperty((Double) cdf.getValue().get(3)).asObject());

        final Service<ObservableList<ObservableList<?>>> service = new Service<>() {
            @Override
            protected Task<ObservableList<ObservableList<?>>> createTask() {
                return new Task<>() {
                    @Override
                    protected ObservableList<ObservableList<?>> call() {
                        ObservableList<ObservableList<?>> items = FXCollections.observableArrayList();
                        List<Stats> stats = trivia.loadStats();
                        for (Stats s : stats) {
                            items.add(FXCollections.observableArrayList(s.getPlayer(), s.getScore(), s.getQuestionCount(), (double) s.getScore() / s.getQuestionCount()));
                        }
                        return items;
                    }
                };
            }
        };
        // Bind the data to the stats database
        statsTable.itemsProperty().bind(service.valueProperty());
        service.start();
    }
}