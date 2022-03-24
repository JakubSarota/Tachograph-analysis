package com.example.tachographanalysis.database;

import javafx.fxml.FXML;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.TextField;

public class addDriver {
    @FXML
    private TextField tf_first_name;

    @FXML
    private TextField tf_last_name;

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_phone;

    public void addDriver(javafx.scene.input.MouseEvent mouseEvent) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connectDB = databaseConnection.getDBConnection();

        try{
            Statement statement = connectDB.createStatement();

            String first_name = tf_first_name.getText();
            String last_name = tf_last_name.getText();
            String email = tf_email.getText();
            String phone = tf_phone.getText();

            int status = statement.executeUpdate("INSERT INTO kierowcy (first_name, last_name, email, phone) VALUES('"+first_name+"','"+last_name+"','"+email+"','"+phone+"')");

            if(status>0) {
                System.out.println("User exists");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
