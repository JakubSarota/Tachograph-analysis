package com.example.tachographanalysis.database.driver;

import com.example.tachographanalysis.DriversController;
import com.example.tachographanalysis.database.DatabaseConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddDriver implements Initializable {

    @FXML
    private TextField tf_first_name, tf_last_name, tf_second_name, tf_email, tf_pesel, tf_city, tf_country, tf_id_card;

    @FXML
    private ChoiceBox<String> tf_category;

    @FXML
    private DatePicker tf_born;

    @FXML
    private Label hint;

    @FXML
    public void datepicker() {
        if(tf_born.getValue()==null) {
            tf_born.setValue(LocalDate.of(1990,01,01));
        }
    }
    @FXML
    private ComboBox<String> tf_CountryBox;

    @FXML
    public void selectCountry(ActionEvent event) {
        tf_CountryBox.getItems();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb){
        ObservableList<String> list = FXCollections.observableArrayList("Polska", "Niemcy","Belgia");
        tf_CountryBox.setItems(list);
        tf_CountryBox.getSelectionModel().select(0);
    }

    public void addDriver() throws SQLException {
        String last_name = tf_last_name.getText();
        String first_name = tf_first_name.getText();
        String second_name = tf_second_name.getText();
        String email = tf_email.getText();
        String pesel = tf_pesel.getText();
        String city = tf_city.getText();
        String born = tf_born.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String country = tf_CountryBox.getValue();
        String id_card = tf_id_card.getText();

        try {
            if(!checkIsAllFill(last_name, first_name, pesel, city, born, country)==true) {
                hint.setTextFill(Color.web("#ff0000"));
                hint.setText("Uzupełnij wszystkie pola oznaczone *");
            } else if(!CheckIsExist(pesel, id_card) == true) {
                hint.setTextFill(Color.web("#ff0000"));
                hint.setText("Użytkownik istnieje w bazie");
            } else if(tf_pesel.getText().length() != 11) {
                hint.setTextFill(Color.web("#ff0000"));
                hint.setText("Nieprawidłowy PESEL");
            } else {
                insertToDatabase(last_name, first_name, second_name, email, pesel, city, born, country, id_card);

            }
        } catch (Exception e) {
//            System.err.println(e.getMessage());
        }
    }

    public Boolean checkIsAllFill(String last_name,
                                  String first_name,
                                  String pesel,
                                  String city,
                                  String born,
                                  String country) throws SQLException {
        if(first_name == "" || last_name == "" || pesel == "" || city == "" || born == "" || country == "") {
            return false;
        } else {
            hint.setTextFill(Color.web("#ffffff"));
            hint.setText("Dodano kierowce");
        }
        return true;
    }

    public boolean CheckIsExist(String pesel,
                                String id_card) throws SQLException {
        boolean status = true;
        ResultSet rs = null;
        String check = "SELECT * FROM driver WHERE pesel='"+pesel+"' AND id_card='"+id_card+"'";
        try {
            rs = DatabaseConnection.exQuery(check);
            if(rs.next()) {
                status = false;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            try{
                if(rs!=null) {
                    rs.close();
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        return status;
    }

    public static void insertToDatabase(String last_name,
                                        String first_name,
                                        String second_name,
                                        String email,
                                        String pesel,
                                        String city,
                                        String born,
                                        String country,
                                        String id_card) throws SQLException {
        try {
            String insert = "INSERT INTO driver (first_name, second_name, last_name, email, " +
                    "pesel, city, born_date, country, id_card) " +
                    "VALUES('"+first_name+"','"+second_name+"','"+last_name+"','"+email+"','"
                    +pesel+"','"+city+"','"+born+"','"+country+"','"+id_card+"')";
            ResultSet rs = DatabaseConnection.exQuery(insert);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void digitOnlyPesel() {
        tf_pesel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (!t1.matches("\\d*")) {
                    tf_pesel.setText(t1.replaceAll("[^\\d]", ""));
                }
                if (tf_pesel.getLength() >=12) {
                    String max = tf_pesel.getText().substring(0,11);
                    tf_pesel.setText(max);
                }
            }
        });
    }

    public void peselValidation(){
        peselValidator PeselVal;
        String PESEL = tf_pesel.getText();
        System.out.println(PESEL);
        PeselVal = new peselValidator(PESEL);

        if (PeselVal.isValid()) {
            System.out.println("Numer PESEL jest prawidlowy");
            System.out.println("Rok urodzenia: " + PeselVal.getBirthYear());
            System.out.println("Miesiac urodzenia: " + PeselVal.getBirthMonth());
            System.out.println("Dzien urodzenia: " + PeselVal.getBirthDay());
            System.out.println("Plec: " + PeselVal.getSex());
            hint.setTextFill(Color.web("#FFF"));
            hint.setText("Numer pesel jest prawidłowy");
        }else{
            hint.setText("Numer pesel jest nieprawidłowy");
        }
    }

    public void digitOnlyIdCard() {
        tf_id_card.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (!t1.matches("\\d*")) {
                    tf_id_card.setText(t1.replaceAll("[^\\d]", ""));
                }
                if (tf_id_card.getLength() >=15) {
                    String max = tf_id_card.getText().substring(0,15);
                    tf_id_card.setText(max);
                }
            }
        });
    }
}