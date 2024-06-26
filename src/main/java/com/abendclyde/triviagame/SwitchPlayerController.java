package com.abendclyde.triviagame;

import com.abendclyde.triviagame.game.Trivia;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/*
    This class is for the management of the Switch Player Popup.
 */

public class SwitchPlayerController {
    private Trivia trivia;
    private Stage popup;

    public void setTrivia(Trivia trivia) {
        this.trivia = trivia;
    }

    public void setPopup(Stage popup) {
        this.popup = popup;
    }

    @FXML
    private TextField inputField;

    // Give the Trivia-Class the Player Name
    @FXML
    public void sendPlayerName(){
        trivia.switchPlayer(inputField.getText());
        popup.hide();
    }
}
