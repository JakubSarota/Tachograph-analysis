package com.example.tachographanalysis;

import com.example.tachographanalysis.size.SizeController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private Button btnAnalogueButton, btnDigitalButton, btnDrivers, btnReports;
    @FXML
    private ImageView btnHelp, btnInfo;

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
//        if(Desktop.isDesktopSupported()) {
//            InputStream jarpdf = getClass().getClassLoader().getResourceAsStream("TACHOFIVEHELP.pdf");
//            try {
//                File pdfTemp = new File("TACHOFIVEHELP.pdf");
//                FileOutputStream fos = new FileOutputStream(pdfTemp);
//                while (jarpdf.available() > 0) {
//                    fos.write(jarpdf.read());
//                }
//                fos.close();
//            } catch (IOException e) {
//                System.out.println(e);
//            }
//        }
    }

    public void getInfo() throws Exception {
//        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("drivers.fxml"));
//        Stage scene = (Stage) getScene().getWindow();
//        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW,SizeController.sizeH));
    }

    public void getSettings() throws Exception {
//        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("settings.fxml"));
//        Stage scene = (Stage) btnDrivers.getScene().getWindow();
//        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW,SizeController.sizeH));
    }
}