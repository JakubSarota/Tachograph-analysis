package com.example.tachographanalysis;

import com.example.tachographanalysis.analogueAnalysis.HoughCirclesRun;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.tachographanalysis.size.SizeController;
import com.example.tachographanalysis.analogueAnalysis.analysisCircle;
import javafx.scene.control.TextArea;
import org.json.JSONArray;
import org.json.JSONObject;

public class AnalogueAnalysisController {
    @FXML
    private Button btnBack;
    @FXML
    private ImageView imageView;
    @FXML
    private ImageView imageView2;
    @FXML
    private Image image;
    @FXML
    private Button dragOver;
    @FXML
    private ScrollPane scroll;
    @FXML
    private TextArea textArea;

    private String imageFile;
    private String text = "Wybierz plik albo upuść go tutaj";

    analysisCircle analysisCircle = new analysisCircle();

    @FXML
    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
//        scene.setMaximized(true);
    }

    @FXML
    private void handleDragOverButton(DragEvent event) {
        if(event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        dragOver.setText("Upuść tutaj plik");
    }

    @FXML
    private void onDragClickedButton() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.png","*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        imageFile = selectedFile.toURI().toURL().toString();
        image = new Image(imageFile);
        if(image.getWidth() <= 1000 || image.getHeight() <= 1000) {
            dragOver.setText("Rozmiar pliku zamały, wymagane 1000x1000 pikseli");
        } else if(selectedFile != null) {
            getImageOnClick(imageFile);
            dragOver.setText(text);
        } else if(selectedFile == null) {
            dragOver.setText(text);
        }
    }

    @FXML
    private void handleDroppedButton(DragEvent event) throws IOException {
        List<File> files = event.getDragboard().getFiles();
        List<String> validExtensions = Arrays.asList("jpg", "png");
        image = new Image(new FileInputStream(files.get(0)));
        if(!validExtensions.containsAll(event.getDragboard()
                .getFiles().stream().map(file -> getExtension(file.getName()))
                .collect(Collectors.toList()))) {
            dragOver.setText("To nie jest plik graficzny!");
        } else if(image.getWidth() <= 1000 || image.getHeight() <= 1000) {
            dragOver.setText("Rozmiar pliku zamały, wymagane 1000x1000 pikseli");
        } else {
            getImageDragAndDrop(files);
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

    private void getImageOnClick(String image) throws IOException {
        scroll.pannableProperty().set(true);
        scroll.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        BufferedImage writableImage[] = analysisCircle.getHuanByCircle(image);
        WritableImage wi=SwingFXUtils.toFXImage(writableImage[0],null);
        WritableImage wi2=SwingFXUtils.toFXImage(writableImage[1],null);
        imageView.setImage(wi);
        imageView2.setImage(wi2);
//        textArea.setText("Udało się");
        writeWork(analysisCircle.blackImage.czas_pracy());
    }

    private void getImageDragAndDrop(List<File> files) throws IOException {
        scroll.pannableProperty().set(true);
        scroll.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        BufferedImage writableImage[] = analysisCircle.getHuanByCircle(String.valueOf(files.get(0)));
        WritableImage wi= SwingFXUtils.toFXImage(writableImage[0],null);
        imageView.setImage(wi);
        WritableImage wi2=SwingFXUtils.toFXImage(writableImage[1],null);
        imageView2.setImage(wi2);
//        textArea.setText("Udało się");
       writeWork(analysisCircle.blackImage.czas_pracy());
    }

    private void writeWork(JSONObject json){
        JSONArray jarr=json.getJSONArray("praca");
        String text="";
        for (int i=0;i<jarr.length();i++){
            boolean pracowal=false;
            boolean przerwa=false;
            int tmp=Integer.parseInt((String) jarr.get(i));
            if(i>0) {
                if (Integer.parseInt((String) jarr.get(i)) - 1 == Integer.parseInt((String) jarr.get(i - 1))) {

                    pracowal = true;
                }
                if(Integer.parseInt((String) jarr.get(i))-15  >= Integer.parseInt((String) jarr.get(i - 1))){
                    przerwa=true;
                    text+="Break "+analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(i - 1)))+"\n";
                }else{
                    pracowal = true;
                    if(przerwa){
                        przerwa=false;
                    }
                }
            }
            if(!pracowal)
            text+="Work "+analysisCircle.blackImage.ktoraGodzina(tmp)+"\n";
        }
        text+="Break "+analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(jarr.length() - 1)))+"\n";

        textArea.setText(text);
    }

}
