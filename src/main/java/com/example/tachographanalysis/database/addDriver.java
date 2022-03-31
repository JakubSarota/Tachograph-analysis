package com.example.tachographanalysis.database;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

import java.sql.*;
import java.time.format.DateTimeFormatter;


public class addDriver {

    @FXML
    private TextField tf_first_name, tf_last_name, tf_second_name, tf_email, tf_pesel, tf_city, tf_country, tf_id_card, tf_category;

    @FXML
    private DatePicker tf_born;

    @FXML
    private Label hint;


    public TextField getAddDriver() {
        try {
            tf_pesel.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if(!t1.matches("\\d*")) {
                        tf_pesel.setText(t1.replaceAll("[^\\d]", ""));
                    }
                }
            });
            return tf_pesel;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public void addDriver() throws SQLException {
        String last_name = tf_last_name.getText();
        String first_name = tf_first_name.getText();
        String second_name = tf_second_name.getText();
        String email = tf_email.getText();
        String pesel = tf_pesel.getText();
        String city = tf_city.getText();
        String born = tf_born.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String country = tf_country.getText();
        String id_card = tf_id_card.getText();
        String license_drive = tf_category.getText();

        if(tf_pesel.getText().length() < 10) {
            hint.setText("Nieprawidłowy PESEL");
        } else if(tf_id_card.getText().length() < 15) {
            hint.setText("Nieprawidłowy numer kierowcy");
        } else {
            if((checkIsAllFill(last_name, first_name, pesel, city, born, country, id_card, license_drive)==true && CheckIsExist(pesel, id_card, license_drive) == true)==true) {
                insertToDatabase(last_name, first_name, second_name, email, pesel, city, born, country, id_card, license_drive);
                System.out.println("true");
            } else {
                System.out.println("false");
            }
        }
    }

    public Boolean checkIsAllFill(String last_name,
                                  String first_name,
                                  String pesel,
                                  String city,
                                  String born,
                                  String country,
                                  String id_card,
                                  String license_drive) throws SQLException {
        if(first_name == "" || last_name == "" || pesel == "" || city == "" || born == "" || license_drive == "" || id_card == "" || country == "") {
            hint.setText("Uzupełnij wszystkie pola oznaczone *");
            return false;
        } else {
            hint.setTextFill(Color.web("#ffffff"));
            hint.setText("Dodano kierowce");
        }
        return true;
    }

    public boolean CheckIsExist(String pesel,
                                String id_card,
                                String license_drive) throws SQLException {
        boolean status = true;
        String check = "SELECT * FROM driver WHERE pesel='"+pesel+"' AND id_card='"+id_card+"' AND license_drive='"+license_drive+"'";
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getDBConnection();
        Statement stmt;
        System.err.println(check);
        ResultSet rs;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(check);
            if(rs.next()) {
                hint.setText("Kierowca istnieje");
                status = false;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return status;
    }

    public void insertToDatabase(String last_name,
                                 String first_name,
                                 String second_name,
                                 String email,
                                 String pesel,
                                 String city,
                                 String born,
                                 String country,
                                 String id_card,
                                 String license_drive) throws SQLException {
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            Connection connection = databaseConnection.getDBConnection();
            Statement st = connection.createStatement();
            String insert = "INSERT INTO driver (first_name, second_name, last_name, email, pesel, city, born_date, country, id_card, license_drive) VALUES('"+first_name+"','"+last_name+"','"+second_name+"','"+email+"','"+pesel+"','"+city+"','"+born+"','"+country+"','"+id_card+"','"+license_drive+"')";
            st.executeQuery(insert);
            connection.close();
        } catch (Exception e) {
//            System.err.println(e.getMessage());
        }

    }
}
