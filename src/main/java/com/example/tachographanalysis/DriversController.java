package com.example.tachographanalysis;

import com.example.tachographanalysis.database.DatabaseConnection;
import com.example.tachographanalysis.size.SizeController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;


public class DriversController implements Initializable {

    @FXML
    private Button btnBack, btnAddDrivers;
    @FXML
    private Pane boxDrivers;
    @FXML
    private ListView<String> accountListView;

    @FXML
    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
    }

    public void initialize(URL location, ResourceBundle resources){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getDBConnection();

        String connectQuery = "SELECT first_name, last_name FROM driver";
        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            while (queryOutput.next()){
                String firstname = queryOutput.getString("first_name");
                String lastname = queryOutput.getString("last_name");
                String listOut = firstname + "\t" + lastname;
                accountListView.getItems().add(listOut);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getAddDrivers() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("addDrivers.fxml"));
        StackPane stackPane = new StackPane();
        Scene secondScene = new Scene(stackPane, 950,420);
        stackPane.getChildren().add(fxmlLoader);
        Stage secondStage = new Stage();
        secondStage.getIcons().add(new Image(getClass().getResourceAsStream("DRIVER.png")));
        secondStage.setTitle("Dodaj kierowce");
        secondStage.setScene(secondScene);

        secondStage.show();
    }

}


