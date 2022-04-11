package com.example.tachographanalysis;

import com.example.tachographanalysis.database.DatabaseConnection;
import com.example.tachographanalysis.size.SizeController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import javax.swing.*;
import java.net.URL;
import java.sql.*;
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
    private Button btnRemoveDriver;
    @FXML
    private ListView<String> accountListView;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Drivers> accountTableView;
    @FXML
    private TableColumn<Drivers, Integer> idCol;
    @FXML
    private TableColumn<Drivers, String>  firstnameCol, secondNameCol, lastnameCol, emailCol, cityCol, bornCol, countryCol, licenseCol, peselCol, cardCol;




    public static class Drivers {

        Integer id;
        String fname, sname, lname, email, city, country, license, born, pesel, card;


        public Drivers(Integer id, String fname, String sname, String lname, String email, String pesel, String city, String born, String country, String card, String license) {
            this.id = id;
            this.fname = fname;
            this.sname = sname;
            this.lname = lname;
            this.email = email;
            this.pesel = pesel;
            this.city = city;
            this.born = born;
            this.country = country;
            this.card = card;
            this.license = license;
        }
        public Drivers() {}


        //setter getter
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
        public String getEmail() {
            return email;
        }
        public String getPesel() {
            return pesel;
        }
        public String getCity() {
            return city;
        }
        public String getBorn() {
            return born;
        }
        public String getCountry() {
            return country;
        }
        public String getCard() { return card; }
        public String getLicense() {
            return license;
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
        public void setEmail(String email) {this.email = email;}
        public void setPesel(String pesel) {this.pesel = pesel;}
        public void setCity(String city) {this.city = city;}
        public void setBorn(String born) {this.born = born;}
        public void setCountry(String country) {this.country = country;}
        public void setCard(String card) {this.card = card;}
        public void setLicense(String license) {this.license = license;}



        ///////

        public ObjectProperty<Drivers>driversObjectProperty = new SimpleObjectProperty<>();
        static ObjectProperty<Drivers>driversObjectPropertyEdit = new SimpleObjectProperty<>(new Drivers());


        public static Drivers getDriversObjectPropertyEdit() {
            return driversObjectPropertyEdit.get();
        }

        public static ObjectProperty<Drivers> driversObjectPropertyEditProperty() {
            return driversObjectPropertyEdit;
        }

        public static void setDriversObjectPropertyEdit(Drivers driversObjectPropertyEdit) {
            Drivers.driversObjectPropertyEdit.set(driversObjectPropertyEdit);
        }

        ///////
    }

    ObservableList<Drivers> driversList = FXCollections.observableArrayList();


    public void UpdateTable() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getDBConnection();

        driversList.clear();
        try {
            String query = "SELECT * FROM driver";

            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(query);

            while (queryOutput.next()) {

                Integer queryId = queryOutput.getInt("id");
                String queryFirstName = queryOutput.getString("first_name");
                String querySecondName = queryOutput.getString("second_name");
                String queryLastName = queryOutput.getString("last_name");
                String queryEmail = queryOutput.getString("email");
                String queryPesel = queryOutput.getString("pesel");
                String queryCity = queryOutput.getString("city");
                String  queryBorn = queryOutput.getString("born_date");
                String queryCountry = queryOutput.getString("country");
                String queryCard = queryOutput.getString("id_card");
                String queryLicense = queryOutput.getString("license_drive");

                driversList.add(new Drivers(queryId, queryFirstName, querySecondName, queryLastName, queryEmail, queryPesel, queryCity, queryBorn, queryCountry, queryCard, queryLicense));

            }

            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            firstnameCol.setCellValueFactory(new PropertyValueFactory<>("fname"));
            secondNameCol.setCellValueFactory(new PropertyValueFactory<>("sname"));
            lastnameCol.setCellValueFactory(new PropertyValueFactory<>("lname"));
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            peselCol.setCellValueFactory(new PropertyValueFactory<>("pesel"));
            cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
            bornCol.setCellValueFactory(new PropertyValueFactory<>("born"));
            countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
            cardCol.setCellValueFactory(new PropertyValueFactory<>("card"));
            licenseCol.setCellValueFactory(new PropertyValueFactory<>("license"));

            accountTableView.setItems(driversList);

///// edycja

            this.firstnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.secondNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.lastnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.peselCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.cityCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.bornCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.countryCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.cardCol.setCellFactory(TextFieldTableCell.forTableColumn());

            this.accountTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
                Drivers.setDriversObjectPropertyEdit(newValue);
                accountTableView.setEditable(true);
            });

/////

            //Wyszukiwanie kierowców
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
                    }else if(Drivers.getPesel().toString().indexOf(searchKeyword) > -1){
                        return true;
                    }else if(Drivers.getCity().toLowerCase().indexOf(searchKeyword) > -1){
                        return true;
                    }else
                        return false;

                });
            });

            SortedList<Drivers> sortedData = new SortedList<>(filteredData);

            sortedData.comparatorProperty().bind(accountTableView.comparatorProperty());

            accountTableView.setItems(sortedData);
            //

        } catch (SQLException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UpdateTable();
        //loadData();
    }

 /*   public void loadData(){
        ObservableList<Drivers> driversList = FXCollections.observableArrayList();
        for (int i=0; i<7; i++){
            driversList.add(new Drivers(String.valueOf(i), "fname"+i, "sname" +i, "lname"+i,"email"+1, new Button("update")));
        }
        accountTableView.setItems(driversList);
    }
*/

    //Wstecz
    @FXML
    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
    }

    //Dodawanie
    public void getAddDrivers() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("addDrivers.fxml"));
        StackPane stackPane = new StackPane();
        Scene secondScene = new Scene(stackPane, 950,420);
        stackPane.getChildren().add(fxmlLoader);
        Stage secondStage = new Stage();
        secondStage.getIcons().add(new Image(getClass().getResourceAsStream("DRIVER.png")));
        secondStage.setTitle("Dodaj kierowce");
        secondStage.setScene(secondScene);

        UpdateTable();
        secondStage.show();
    }

    //Edycja
    public void onEditFname(TableColumn.CellEditEvent<Drivers, String> driversStringCellEditEvent) {
        Drivers.getDriversObjectPropertyEdit().setFname(driversStringCellEditEvent.getNewValue());
        Drivers.getDriversObjectPropertyEdit().setSname(driversStringCellEditEvent.getNewValue());
        Drivers.getDriversObjectPropertyEdit().setLname(driversStringCellEditEvent.getNewValue());
        Drivers.getDriversObjectPropertyEdit().setEmail(driversStringCellEditEvent.getNewValue());
        Drivers.getDriversObjectPropertyEdit().setPesel(driversStringCellEditEvent.getNewValue());
        Drivers.getDriversObjectPropertyEdit().setCity(driversStringCellEditEvent.getNewValue());
        Drivers.getDriversObjectPropertyEdit().setBorn(driversStringCellEditEvent.getNewValue());
        Drivers.getDriversObjectPropertyEdit().setCountry(driversStringCellEditEvent.getNewValue());
        Drivers.getDriversObjectPropertyEdit().setCard(driversStringCellEditEvent.getNewValue());

    }



    //Usuwanie
    public void removeDriver(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection conn = connectNow.getDBConnection();
        String query = "DELETE FROM driver WHERE id = ?";
        Integer myIndex = accountTableView.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(accountTableView.getItems().get(myIndex).getId()));

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, String.valueOf(id));
            pst.executeUpdate();

            pst.close();

            JOptionPane.showMessageDialog(null, "Czy chcesz usunąć użytkownika ?");
            UpdateTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
}

