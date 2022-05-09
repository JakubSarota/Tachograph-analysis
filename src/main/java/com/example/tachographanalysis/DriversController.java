package com.example.tachographanalysis;

import com.example.tachographanalysis.database.DatabaseConnection;
import com.example.tachographanalysis.database.driver.Driver;
import com.example.tachographanalysis.database.driver.ShowList;
import com.example.tachographanalysis.database.driver.driverInfo.InfoDriver;
import com.example.tachographanalysis.database.driver.driverInfo.showData;
import com.example.tachographanalysis.database.trash.Trash;
import com.example.tachographanalysis.size.SizeController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.example.tachographanalysis.database.driver.Driver.getDriversObjectPropertyEdit;


public class DriversController {

    @FXML
    private Button btnBack, btnDrivers, generateOsw;
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
                                    +"-fx-fill:#00E676;"
                                    +"-fx-border-radius: 20;"
                                    +"-fx-background-color: transparent;"
                                    +"-fx-border-color: #2A73FF;"

                            );
                            edit.setOnMouseClicked(mouseEvent -> {
                                Stage stage = new Stage();
                                FXMLLoader loader = new FXMLLoader();
                                if(stage==null || !stage.isShowing()) {
                                    driver = getTableView().getItems().get(getIndex());
                                    InfoDriver.getIdDriver(driver.getId());
                                    showData.getIdDriverData(driver.getId());
                                    loader.setLocation(getClass().getResource("infoDriver.fxml"));
                                    try {
                                        loader.load();
                                    } catch (Exception e) { }
                                    Parent parent = loader.getRoot();
                                    stage.setScene(new Scene(parent));
                                    stage.getIcons().add(new Image(getClass().getResourceAsStream("images/DRIVER.png")));
                                    stage.setTitle(driver.getFname()+" "+driver.getLname());
                                    stage.show();
                                } else {
                                    stage.toFront();
                                }
                            });
                            HBox hbox = new HBox(edit);
                            hbox.setStyle("-fx-alignment: center");
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

            ///// Edit data

            this.firstnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.secondNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.lastnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.peselCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.cityCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.bornCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.countryCol.setCellFactory(TextFieldTableCell.forTableColumn());
            this.cardCol.setCellFactory(TextFieldTableCell.forTableColumn());
            accountListView.setPlaceholder(new Label("Brak kierowców"));
            this.accountTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
                Driver.setDriversObjectPropertyEdit(newValue);
                accountTableView.setEditable(true);

            });
/////

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

    private Connection getConnection() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection conn = connectNow.getDBConnection();
        return conn;
    }

    @FXML
    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
    }


    public void getAddDrivers() throws Exception {
        StackPane stackPane = new StackPane();
        Scene secondScene = new Scene(stackPane, 950,420);
        Stage secondStage = new Stage();
        if(secondStage==null||!secondStage.isShowing()) {
            Parent fxmlLoader = FXMLLoader.load(getClass().getResource("addDrivers.fxml"));
            stackPane.getChildren().add(fxmlLoader);
            secondStage.getIcons().add(new Image(getClass().getResourceAsStream("images/DRIVER.png")));
            secondStage.setTitle("Dodaj kierowce");
            secondStage.setScene(secondScene);
            secondStage.show();
        } else {
            secondStage.toFront();
        }
    }

    //Edit data
    public void onEditFname(TableColumn.CellEditEvent<Driver, String> driversStringCellEditEvent) {
        getDriversObjectPropertyEdit().setFname(driversStringCellEditEvent.getNewValue());
        String value = driversStringCellEditEvent.getNewValue();
        String driverId= String.valueOf(accountTableView.getSelectionModel().getSelectedItem().getId());
        Connection conn = getConnection();
        System.out.println(value);
        String query = "UPDATE driver SET  first_name = '"+value+"' WHERE id = '"+driverId+"'";
        extracted(conn, query);
    }

    public void onEditSname(TableColumn.CellEditEvent<Driver, String> driversStringCellEditEvent) {
        getDriversObjectPropertyEdit().setSname(driversStringCellEditEvent.getNewValue());

        String value = driversStringCellEditEvent.getNewValue();
        String driverId= String.valueOf(accountTableView.getSelectionModel().getSelectedItem().getId());

        Connection conn = getConnection();
        System.out.println(value);

        String query = "UPDATE driver SET  second_name = '"+value+"' WHERE id = '"+driverId+"'";

        extracted(conn, query);
    }

    public void onEditLname(TableColumn.CellEditEvent<Driver, String> driversStringCellEditEvent) {
        getDriversObjectPropertyEdit().setLname(driversStringCellEditEvent.getNewValue());
        String value = driversStringCellEditEvent.getNewValue();
        String driverId= String.valueOf(accountTableView.getSelectionModel().getSelectedItem().getId());

        Connection conn = getConnection();

        System.out.println(value);
        String query = "UPDATE driver SET  last_name = '"+value+"' WHERE id = '"+driverId+"'";

        extracted(conn, query);
    }
    public void onEditEmail(TableColumn.CellEditEvent<Driver, String> driversStringCellEditEvent) {
        getDriversObjectPropertyEdit().setEmail(driversStringCellEditEvent.getNewValue());
        String value = driversStringCellEditEvent.getNewValue();
        String driverId= String.valueOf(accountTableView.getSelectionModel().getSelectedItem().getId());

        Connection conn = getConnection();
        System.out.println(value);
        String query = "UPDATE driver SET  email = '"+value+"' WHERE id = '"+driverId+"'";

        extracted(conn, query);
    }

    public void onEditPesel(TableColumn.CellEditEvent<Driver, String> driversStringCellEditEvent) {
        getDriversObjectPropertyEdit().setPesel(driversStringCellEditEvent.getNewValue());
        String value = driversStringCellEditEvent.getNewValue();
        String driverId= String.valueOf(accountTableView.getSelectionModel().getSelectedItem().getId());

        Connection conn = getConnection();

        System.out.println(value);
        String query = "UPDATE driver SET  pesel = '"+value+"' WHERE id = '"+driverId+"'";

        extracted(conn, query);
    }
    public void onEditCity(TableColumn.CellEditEvent<Driver, String> driversStringCellEditEvent) {
        getDriversObjectPropertyEdit().setCity(driversStringCellEditEvent.getNewValue());
        String value = driversStringCellEditEvent.getNewValue();
        String driverId= String.valueOf(accountTableView.getSelectionModel().getSelectedItem().getId());

        Connection conn = getConnection();

        System.out.println(value);
        String query = "UPDATE driver SET  city = '"+value+"' WHERE id = '"+driverId+"'";

        extracted(conn, query);
    }
    public void onEditBorn(TableColumn.CellEditEvent<Driver, String> driversStringCellEditEvent) {
        getDriversObjectPropertyEdit().setBorn(driversStringCellEditEvent.getNewValue());
        String value = driversStringCellEditEvent.getNewValue();
        String driverId= String.valueOf(accountTableView.getSelectionModel().getSelectedItem().getId());

        Connection conn = getConnection();

        System.out.println(value);
        String query = "UPDATE driver SET  born_date = '"+value+"' WHERE id = '"+driverId+"'";

        extracted(conn, query);
    }
    public void onEditCountry(TableColumn.CellEditEvent<Driver, String> driversStringCellEditEvent) {
        getDriversObjectPropertyEdit().setCountry(driversStringCellEditEvent.getNewValue());
        String value = driversStringCellEditEvent.getNewValue();
        String driverId= String.valueOf(accountTableView.getSelectionModel().getSelectedItem().getId());

        Connection conn = getConnection();

        System.out.println(value);
        String query = "UPDATE driver SET  country = '"+value+"' WHERE id = '"+driverId+"'";

        extracted(conn, query);
    }
    public void onEditIdCard(TableColumn.CellEditEvent<Driver, String> driversStringCellEditEvent) {
        getDriversObjectPropertyEdit().setCard(driversStringCellEditEvent.getNewValue());
        String value = driversStringCellEditEvent.getNewValue();
        String driverId= String.valueOf(accountTableView.getSelectionModel().getSelectedItem().getId());

        Connection conn = getConnection();

        System.out.println(value);
        String query = "UPDATE driver SET  id_card = '"+value+"' WHERE id = '"+driverId+"'";

        extracted(conn, query);
    }

    //Delete data
    public void removeDriver() throws SQLException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection conn = connectNow.getDBConnection();
        String query = "DELETE FROM driver WHERE id = ?";
        Integer myIndex = accountTableView.getSelectionModel().getSelectedIndex();
        int id = Integer.parseInt(String.valueOf(accountTableView.getItems().get(myIndex).getId()));

        try {
            Trash.deleteUserWithData(accountTableView.getItems()
                    .get(accountTableView.getSelectionModel().getSelectedIndex()));
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, String.valueOf(id));
            pst.executeUpdate();
            pst.close();
            PreparedStatement pst2 = conn.prepareStatement("DELETE FROM stats WHERE driver_id = ?");
            pst2.setString(1, String.valueOf(id));
            pst2.executeUpdate();
            pst2.close();

            JOptionPane.showMessageDialog(null, "Usunięto kierowce");
            loadTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    //
    private void extracted(Connection conn, String query) {
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.executeUpdate();
            pst.close();
            loadTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void backupDrivers(ActionEvent actionEvent) throws IOException {
        StackPane stackPane = new StackPane();
        Scene secondScene = new Scene(stackPane, 643,451);
        Stage secondStage = new Stage();
        if(secondStage==null||!secondStage.isShowing()) {
            Parent fxmlLoader = FXMLLoader.load(getClass().getResource("backUpDrivers.fxml"));
            stackPane.getChildren().add(fxmlLoader);
            secondStage.getIcons().add(new Image(getClass().getResourceAsStream("images/DRIVER.png")));
            secondStage.setTitle("Odzyskaj kierowcę");
            secondStage.setScene(secondScene);
            secondStage.show();
        } else {
            secondStage.toFront();
        }
    }

    public void openFolder(MouseEvent mouseEvent) {
        AnalogueAnalysisController.openFolder();
    }
}

