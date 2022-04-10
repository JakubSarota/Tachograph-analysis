package com.example.tachographanalysis.database;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InfoDriver {
    @FXML
    public Label firstname;
    @FXML
    private Label lastname;

    int driverId;

    public void initialize() throws SQLException {
        load();
    }

    public void load() throws SQLException {
        String query = "SELECT * FROM driver";
        ResultSet queryOutput = DatabaseConnection.exQuery(query);
        firstname.setText(queryOutput.getString("first_name"));
        lastname.setText(queryOutput.getString("last_name"));
    }

    public void setTextField(int id) {
        driverId = id;
    }
}
