package com.example.tachographanalysis;

import com.example.tachographanalysis.database.DatabaseConnection;
import com.example.tachographanalysis.database.driver.Driver;
import com.example.tachographanalysis.database.driver.ShowList;
import com.example.tachographanalysis.database.driver.driverInfo.InfoDriver;
import com.example.tachographanalysis.database.driver.driverInfo.showData;
import com.example.tachographanalysis.size.SizeController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class DriversController {

    @FXML
    private Button btnBack, btnDrivers;
    @FXML
    private Pane boxDrivers;
    @FXML
    private ListView<String> accountListView;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Driver> accountTableView;
    @FXML
    private TableColumn<Driver, Integer> idCol;
    @FXML
    private TableColumn<Driver, String>  firstnameCol, secondNameCol, lastnameCol, emailCol, cityCol, bornCol, countryCol, licenseCol, peselCol, cardCol, editCol;

    ObservableList<Driver> driversList = FXCollections.observableArrayList();
    Driver driver;
    public void initialize() {
        try {
            loadTable();
            search();
        } catch (Exception e) {
//            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public void loadTable() {
        try {
            driversList = ShowList.driversList();
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
            Callback<TableColumn<Driver, String>, TableCell<Driver,String>> cellEdit = (TableColumn<Driver,String> param) -> {
                final TableCell<Driver,String> cell = new TableCell<>() {
                    private final Button edit = new Button();
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setGraphic(null);
                        } else {
                            edit.setText("Dane");
                            edit.setStyle(
                                    "-fx-cursor: hand ;"
                                   +"-fx-background-color: transparent;"
                                    +"-fx-border-color: black;"
                                    +"-fx-border-style: 2px solid"

                            );
                            edit.setOnMouseClicked(mouseEvent -> {
                                driver = getTableView().getItems().get(getIndex());
//                            System.out.println(driver.getId() + " " + driver.getPesel());
                                InfoDriver.getIdDriver(driver.getId());
                                showData.getIdDriverData(driver.getId());
                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(getClass().getResource("infoDriver.fxml"));
                                try {
                                    loader.load();
                                } catch (Exception e) { }

                                Parent parent = loader.getRoot();
                                Stage stage = new Stage();
                                stage.setScene(new Scene(parent));
                                stage.show();
                            });
                            HBox hbox = new HBox(edit);
                            hbox.setStyle("-fx-alignment:center");
                            HBox.setMargin(edit, new Insets(2, 2, 0, 3));
                            setGraphic(hbox);
                        }
                        setText(null);
                    }
                };
                return cell;
            };
            editCol.setCellFactory(cellEdit);
            accountTableView.setItems(driversList);
        }catch (Exception e) { }
    }

    public void search() {
        //search engine
        FilteredList<Driver> filteredData = new FilteredList<>(driversList, b -> true);

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

        SortedList<Driver> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(accountTableView.comparatorProperty());
        accountTableView.setItems(sortedData);
    }

    @FXML
    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
    }

    StackPane stackPane = new StackPane();
    Scene secondScene = new Scene(stackPane, 950,420);
    Stage secondStage = new Stage();

    public void getAddDrivers() throws Exception {
        if(secondStage==null||!secondStage.isShowing()) {
            Parent fxmlLoader = FXMLLoader.load(getClass().getResource("addDrivers.fxml"));
            stackPane.getChildren().add(fxmlLoader);
            secondStage.getIcons().add(new Image(getClass().getResourceAsStream("DRIVER.png")));
            secondStage.setTitle("Dodaj kierowce");
            secondStage.setScene(secondScene);
            secondStage.show();
        } else {
            secondStage.toFront();
        }
    }

    //Edycja
    public void onEditFname(TableColumn.CellEditEvent<Driver, String> driversStringCellEditEvent) {
//        Drivers.getDriversObjectPropertyEdit().setFname(driversStringCellEditEvent.getNewValue());
//        Drivers.getDriversObjectPropertyEdit().setSname(driversStringCellEditEvent.getNewValue());
//        Drivers.getDriversObjectPropertyEdit().setLname(driversStringCellEditEvent.getNewValue());
//        Drivers.getDriversObjectPropertyEdit().setEmail(driversStringCellEditEvent.getNewValue());
//        Drivers.getDriversObjectPropertyEdit().setPesel(driversStringCellEditEvent.getNewValue());
//        Drivers.getDriversObjectPropertyEdit().setCity(driversStringCellEditEvent.getNewValue());
//        Drivers.getDriversObjectPropertyEdit().setBorn(driversStringCellEditEvent.getNewValue());
//        Drivers.getDriversObjectPropertyEdit().setCountry(driversStringCellEditEvent.getNewValue());
//        Drivers.getDriversObjectPropertyEdit().setCard(driversStringCellEditEvent.getNewValue());

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
            loadTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
}

