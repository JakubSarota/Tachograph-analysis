package com.example.tachographanalysis;

import com.example.tachographanalysis.database.driver.Driver.Drivers;
import com.example.tachographanalysis.database.driver.ShowList;
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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    private TableView<Drivers> accountTableView;
    @FXML
    private TableColumn<Drivers, Integer> idCol;
    @FXML
    private TableColumn<Drivers, String>  firstnameCol, secondNameCol, lastnameCol, emailCol, cityCol, bornCol, countryCol, licenseCol, peselCol, cardCol, editCol;

    private ObservableList<Drivers> driversList = FXCollections.observableArrayList();
    Drivers driver = null;


    public void initialize() {
        try {
            System.out.println(driver);
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

            Callback<TableColumn<Drivers, String>, TableCell<Drivers, String>> cellEdit = (TableColumn<Drivers, String> param) -> {
                final TableCell<Drivers, String> cell = new TableCell<>() {
                    @Override
                    public void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if(b) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            Button edit = new Button();
                            edit.setText("Dane");
                            edit.setStyle(
                                    "-fx-cursor: hand ;"
                                            +"-fx-fill:#00E676;"
                            );

                            edit.setOnMouseClicked((MouseEvent) -> {
                                driver = accountTableView.getSelectionModel().getSelectedItem();

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

                            HBox managebtn = new HBox(edit);
                            managebtn.setStyle("-fx-alignment:center");
                            HBox.setMargin(edit, new Insets(2, 2, 0, 3));
                            setGraphic(managebtn);
                            setText(null);
                        }
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
}


