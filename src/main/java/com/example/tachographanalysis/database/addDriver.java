package com.example.tachographanalysis.database;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;


public class addDriver {

    @FXML
    private TextField tf_first_name, tf_last_name, tf_second_name, tf_email, tf_pesel, tf_city, tf_country, tf_id_card, tf_category;

    @FXML
    private DatePicker tf_born;

    @FXML
    private Label hint;


//    public TextField getAddDriver() {
//        tf_pesel.textProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
//                if(!t1.matches("\\d*")) {
//                    tf_pesel.setText(t1.replaceAll("[^\\d]", ""));
//                }
//            }
//        });
//        return tf_pesel;
//    }


    public void addDriver() throws SQLException {
        insertToDatabase();
    }

    public void insertToDatabase() throws SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connectDB = databaseConnection.getDBConnection();

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

        String checkIsExists = "SELECT COUNT(*) FROM driver WHERE first_name LIKE '"+first_name+"' AND last_name LIKE '"+last_name+"' AND second_name LIKE '"+second_name+"' ";
        PreparedStatement isExist = connectDB.prepareStatement(checkIsExists);

        if(first_name == "" || last_name == "" || pesel == "" || city == "" || born == "" || license_drive == "" || id_card == "" || country == "") {
            hint.setText("Uzupełnij wszystkie pola oznaczone *");
        } else if(isExist.execute() == true) {
            hint.setText("Użytkownik istnieje");
        } else {
            try {
                Statement statement = connectDB.createStatement();
                int status = statement.executeUpdate("INSERT INTO driver (first_name, second_name, last_name, email, pesel, city, born_date, country, id_card, license_drive) VALUES('"+first_name+"','"+last_name+"','"+second_name+"','"+email+"','"+pesel+"','"+city+"','"+born+"','"+country+"','"+id_card+"','"+license_drive+"')");

                if(status>0) {
                    hint.setTextFill(Color.web("#ffffff"));
                    hint.setText("Kierowca dodany");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
