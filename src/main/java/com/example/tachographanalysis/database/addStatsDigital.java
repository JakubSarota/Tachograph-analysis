package com.example.tachographanalysis.database;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class addStatsDigital {
    @FXML
    private DatePicker dataPicker;
    @FXML
    private ListView accountListView;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField breakTime;
    @FXML
    private TextField workTime;
    @FXML
    private TextField sumRoad;
    private String driver;
    @FXML
    private Text returnInfo;
    @FXML
    protected void initialize() throws Exception {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getDBConnection();

        String connectQuery = "SELECT id,first_name,second_name, last_name FROM driver";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            while (queryOutput.next()) {
                String firstname = queryOutput.getString("first_name");
                String lastname = queryOutput.getString("second_name");
                int id = queryOutput.getInt("id");
                String listOut = id + " " + firstname + " " + lastname;
                accountListView.getItems().add(listOut);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        accountListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("Aktwny kierowca zotał zmieniony z: "
                        + oldValue + " na:  " + newValue);
                driver = newValue;
            }
        });
        dataPicker.setValue(LocalDate.now());

    }

    public void addStatsDigital() throws IOException {
    }
}
