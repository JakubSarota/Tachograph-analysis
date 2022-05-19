package com.example.tachographanalysis;

import com.example.tachographanalysis.database.trash.Trash;
import com.example.tachographanalysis.workinfo.WorkInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import com.example.tachographanalysis.size.SizeController;
import org.json.JSONArray;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, ParseException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), SizeController.sizeW, SizeController.sizeH);
        stage.setTitle("Tachfive 1.4");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("images/ICON.png")));
//        stage.setMaximized(true);
        stage.show();
        Trash.deleteWhenTimeOut();
    }

    public static void main(String[] args) {
        launch();
    }
}