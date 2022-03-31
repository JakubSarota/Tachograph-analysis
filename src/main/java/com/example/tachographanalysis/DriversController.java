package com.example.tachographanalysis;

import com.example.tachographanalysis.database.DatabaseConnection;
import com.example.tachographanalysis.size.SizeController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DriversController implements Initializable {

    @FXML
    private Button btnBack;
    @FXML
    private Button btnDrivers;
    @FXML
    private Pane boxDrivers;
    @FXML
    private ListView<String> accountListView;
    ///////
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Drivers> accountTableView;
    @FXML
    private TableColumn<Drivers, Integer> idCol;
    @FXML
    private TableColumn<Drivers, String>  firstnameCol, secondNameCol, lastnameCol;


    public class Drivers {

        Integer id;
        String fname, sname, lname;

        public Drivers(Integer id, String fname, String sname, String lname) {
            this.id = id;
            this.fname = fname;
            this.sname = sname;
            this.lname = lname;
        }

        public Integer getId() { return id; }

        public String getFname() {
            return fname;
        }

        public String getSname() {
            return sname;
        }

        public String getLname() {
            return lname;
        }


        public void setId(Integer id) {
            this.id = id;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public void setSname(String sname) {
                this.sname = sname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }
    }


    ObservableList<Drivers> driversList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getDBConnection();

        String query = "SELECT id, first_name, second_name, last_name FROM driver";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(query);

            while (queryOutput.next()) {

                Integer queryId = queryOutput.getInt("id");
                String queryFirstName = queryOutput.getString("first_name");
                String querySecondName = queryOutput.getString("second_name");
                String queryLastName = queryOutput.getString("last_name");

                driversList.add(new Drivers(queryId, queryFirstName, querySecondName, queryLastName));

            }
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            firstnameCol.setCellValueFactory(new PropertyValueFactory<>("fname"));
            secondNameCol.setCellValueFactory(new PropertyValueFactory<>("sname"));
            lastnameCol.setCellValueFactory(new PropertyValueFactory<>("lname"));

            accountTableView.setItems(driversList);

            //Wyszukiwarka
            FilteredList<Drivers> filteredData = new FilteredList<>(driversList, b -> true);

            searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(Drivers ->{

                    if (newValue.isEmpty() || newValue.isBlank() || newValue == null){
                        return true;
                    }
                    String searchKeyword = newValue.toLowerCase();
                    if (Drivers.getFname().toLowerCase().indexOf(searchKeyword) > -1){
                        return true;
                    }else if(Drivers.getSname().toLowerCase().indexOf(searchKeyword) > -1){
                        return true;
                    }else if(Drivers.getLname().toLowerCase().indexOf(searchKeyword) > -1){
                        return true;
                    }else
                        return false;

                });
            });

            SortedList<Drivers> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(accountTableView.comparatorProperty());

            accountTableView.setItems(sortedData);

        } catch (SQLException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    @FXML
    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
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


