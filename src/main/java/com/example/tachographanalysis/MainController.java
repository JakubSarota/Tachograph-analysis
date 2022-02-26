package com.example.tachographanalysis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private Button btnAnalogueButton, btnDigitalButton;

    public void getAnalogueAnalysis() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("analogueAnalysis.fxml"));
        Stage scene = (Stage) btnAnalogueButton.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, 1280,720));
    }
}