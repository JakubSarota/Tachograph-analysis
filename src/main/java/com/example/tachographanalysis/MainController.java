package com.example.tachographanalysis;

import com.example.tachographanalysis.size.SizeController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainController {
    @FXML
    private Button btnAnalogueButton, btnDigitalButton, btnDrivers, btnReports, btnSettings;


    public void getAnalogueAnalysis() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("analogueAnalysis.fxml"));
        Stage scene = (Stage) btnAnalogueButton.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW,SizeController.sizeH));
//        scene.setMaximized(true);
    }

    public void getDigitalAnalysis() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("digitalAnalysis.fxml"));
        Stage scene = (Stage) btnDigitalButton.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW,SizeController.sizeH));
//        scene.setMaximized(true);
    }

    public void getDrivers() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("drivers.fxml"));
        Stage scene = (Stage) btnDrivers.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW,SizeController.sizeH));
    }

    public void getReports() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("reports.fxml"));
        Stage scene = (Stage) btnReports.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW,SizeController.sizeH));
    }

    public void getHelp() throws Exception {
        File fileChooser = new File("src/main/resources/com/example/tachographanalysis/TACHOFIVEHELP.pdf");
        Desktop.getDesktop().open(fileChooser);
    }
    Stage secondStage = new Stage();
    StackPane stackPane = new StackPane();
    Scene secondScene = new Scene(stackPane, 200,100);

    public void aboutProgram() throws Exception {
        if(secondStage==null || !secondStage.isShowing()) {
            Parent fxmlLoader = FXMLLoader.load(getClass().getResource("aboutProgram.fxml"));
            secondStage.getIcons().add(new Image(getClass().getResourceAsStream("images/INFO.png")));
            stackPane.getChildren().add(fxmlLoader);
            secondStage.setTitle("");
            secondStage.setScene(secondScene);
            secondStage.show();
        } else {
            secondStage.toFront();
        }
    }

    public void getSettings() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("settings.fxml"));
        Stage scene = (Stage) btnSettings.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW,SizeController.sizeH));
    }
}