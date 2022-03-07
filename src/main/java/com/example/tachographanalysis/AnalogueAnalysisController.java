package com.example.tachographanalysis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.example.tachographanalysis.size.SizeController;

public class AnalogueAnalysisController {
    @FXML
    private Button btnBack, btnUpload;
    @FXML
    private Label uploadText;
    @FXML
    private ImageView dragOver, imageView;
    @FXML
    private Image image;

    @FXML
    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
    }

    @FXML
    private void handleDragOver(DragEvent event) {
        if(event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    public String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1)
            return fileName.substring(i + 1).toLowerCase();

        return extension;
    }

    @FXML
    private void handleDropped(DragEvent event) throws FileNotFoundException {
        List<File> files = event.getDragboard().getFiles();
        List<String> validExtensions  = Arrays.asList("jpg", "png");

        image = new Image(new FileInputStream(files.get(0)));

        if(!validExtensions.containsAll(event.getDragboard().getFiles().stream().map(file -> getExtension(file.getName())).collect(Collectors.toList()))) {
            uploadText.setText("It's not jpg or png file!");
        } else if(image.getWidth() <= 550 || image.getHeight() <= 550) {
            uploadText.setText("Size is to small, to load file");
        } else {
            imageView.setImage(image);
            uploadText.setText("");
        }

    }

    public void initialize(URL url, ResourceBundle rb) {

    }

}
