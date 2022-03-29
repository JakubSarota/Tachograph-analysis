package com.example.tachographanalysis.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


public class addDriver {
    @FXML
    private TextField tf_first_name;

    @FXML
    private TextField tf_last_name;

    @FXML
    private TextField tf_second_name;

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_pesel;

    @FXML
    private TextField tf_city;

    @FXML
    private DatePicker tf_born;

    @FXML
    private TextField tf_country;

    @FXML
    private TextField tf_id_card;

    @FXML
    private TextField tf_category;


    public void addDriver() {
        insertToDatabase();
    }

    public void insertToDatabase() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connectDB = databaseConnection.getDBConnection();

        try{
            Statement statement = connectDB.createStatement();

            String first_name = tf_first_name.getText();
            String last_name = tf_last_name.getText();
            String second_name = tf_second_name.getText();
            String email = tf_email.getText();
            String pesel = tf_pesel.getText();
            String city = tf_city.getText();
            String born = tf_born.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String country = tf_country.getText();
            String id_card = tf_id_card.getText();
            String license_drive = tf_category.getText();

            int status = statement.executeUpdate("INSERT INTO driver (first_name, second_name, last_name, email, pesel, city, born_date, country, id_card, license_drive) VALUES('"+first_name+"','"+last_name+"','"+second_name+"','"+email+"','"+pesel+"','"+city+"','"+born+"','"+country+"','"+id_card+"','"+license_drive+"')");

            if(status>0) {
                System.out.println("User exists");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
