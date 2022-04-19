package com.example.tachographanalysis.database.driver.driverInfo;

import com.example.tachographanalysis.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InfoDriver {
    @FXML
    public Label firstname, lastname, secondname, email, pesel, city, born, country, cardnumber;
    @FXML
    public TableView<Data> dataView;
    @FXML
    public TableColumn<Data, Integer> idDriverCol, sumRoadCol;
    @FXML
    public TableColumn<Data, String> dateWorkCol, dateAddCol, sumWorkCol, sumBreakCol, fileCol, fileTypeCol;
    @FXML
    private ObservableList<Data> dataList = FXCollections.observableArrayList();
    static int idDriver;
    public static int getIdDriver(int id) {
        return idDriver = id;
    }

    public void initialize() throws SQLException {
        loadDriver();
        infoDriver();
    }

    public void loadDriver() throws SQLException {
        try {
            String query = "SELECT * FROM driver WHERE id='"+idDriver+"'";
            ResultSet queryOutput = DatabaseConnection.exQuery(query);
            firstname.setText(queryOutput.getString("first_name"));
            lastname.setText(queryOutput.getString("last_name"));
            secondname.setText(queryOutput.getString("second_name"));
            email.setText(queryOutput.getString("email"));
            pesel.setText(queryOutput.getString("pesel"));
            city.setText(queryOutput.getString("city"));
            born.setText(queryOutput.getString("born_date"));
            country.setText(queryOutput.getString("country"));
            cardnumber.setText(queryOutput.getString("id_card"));
            try {
                if(queryOutput!=null) {
                    queryOutput.close();
                }
            } catch (Exception e) { }
        } catch (Exception e) {
//            System.err.println(e.getMessage());
        }
    }

    public void infoDriver() throws SQLException {
        try {
            dataList = showData.data();
            dateAddCol.setCellValueFactory(new PropertyValueFactory<>("data_add"));
            dateWorkCol.setCellValueFactory(new PropertyValueFactory<>("date_work"));
            sumWorkCol.setCellValueFactory(new PropertyValueFactory<>("sum_work"));
            sumBreakCol.setCellValueFactory(new PropertyValueFactory<>("sum_break"));
            sumRoadCol.setCellValueFactory(new PropertyValueFactory<>("sum_road"));
            fileTypeCol.setCellValueFactory(new PropertyValueFactory<>("file_type"));
            fileCol.setCellValueFactory(new PropertyValueFactory<>("file"));
            dataView.setItems(dataList);
//            System.out.println(dataList);
        } catch (Exception e) {
//            System.err.println(e.getMessage());
        }

    }

}
