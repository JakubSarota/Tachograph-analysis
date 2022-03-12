package com.example.tachographanalysis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.example.tachographanalysis.size.SizeController;
import com.example.tachographanalysis.analogueAnalysis.blackAndWhite;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class AnalogueAnalysisController {
    @FXML
    private Button btnBack;
    @FXML
    private ImageView imageView;
    @FXML
    private Image image;
    @FXML
    private Button dragOver;

    private String imageFile;
    private String text = "Choose file from memory or drag and drop here";
    List<String> lstFile;

    blackAndWhite blackAndWhite = new blackAndWhite();

    @FXML
    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
    }

    @FXML
    private void handleDragOverButton(DragEvent event) {
        if(event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        dragOver.setText("Drop here");
    }

    @FXML
    private void onDragClickedButton() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.png","*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        imageFile = selectedFile.toURI().toURL().toString();
        image = new Image(imageFile);
        if(image.getWidth() <= 1000 || image.getHeight() <= 1000) {
            dragOver.setText("File size is small to load, minimum is 1000x1000 pixels");
        } else if(selectedFile != null) {
            blackAndWhite.loadImage(image);
//            blackAndWhite.source(image);
//            blackAndWhite.imageToMat(image);
//            imageView.setImage(image);

            dragOver.setText(text);
        } else if(selectedFile == null) {
            dragOver.setText(text);
        }
    }

    @FXML
    private void handleDroppedButton(DragEvent event) throws FileNotFoundException {
        List<File> files = event.getDragboard().getFiles();
        List<String> validExtensions = Arrays.asList("jpg", "png");

        image = new Image(new FileInputStream(files.get(0)));

        if(!validExtensions.containsAll(event.getDragboard()
                .getFiles().stream().map(file -> getExtension(file.getName()))
                .collect(Collectors.toList()))) {
            dragOver.setText("It's not jpg or png file!");
        } else if(image.getWidth() <= 1000 || image.getHeight() <= 1000) {
            dragOver.setText("File size is small to load, minimum is 1000x1000 pixels");
        } else {
            imageView.setImage(image);
            dragOver.setText(text);
        }
    }

    public String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1)
            return fileName.substring(i + 1).toLowerCase();
        return extension;
    }

    public void initialize(URL url, ResourceBundle rb) {
        lstFile = new ArrayList<>();
        lstFile.add("*.png");
        lstFile.add("*.jpg");
    }


}
