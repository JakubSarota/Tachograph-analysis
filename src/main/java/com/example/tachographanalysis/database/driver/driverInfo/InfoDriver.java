package com.example.tachographanalysis.database.driver.driverInfo;

import com.example.tachographanalysis.PDF.CreatePDF;
import com.example.tachographanalysis.database.DatabaseConnection;
import com.itextpdf.text.DocumentException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
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
    private TextArea work_info;
    @FXML
    public TableColumn<Data, String> dateWorkCol, dateAddCol, sumWorkCol, sumBreakCol, fileCol, fileTypeCol;
    @FXML
    private ObservableList<Data> dataList = FXCollections.observableArrayList();

    static int idDriver;
    static String firstName, lastName, Born, cardNumber;
    int idStats;
    Integer data;


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
            firstName=queryOutput.getString("first_name");
            lastName=queryOutput.getString("last_name");
            Born=queryOutput.getString("born_date");
            cardNumber=queryOutput.getString("id_card");

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
//            fileCol.setCellValueFactory(new PropertyValueFactory<>("file"));
            dataView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Data>() {
                @Override
                public void changed(ObservableValue<? extends Data> observable, Data oldValue, Data newValue) {
                    data = newValue.id;
//                    System.err.println(data);
                    String query = "SELECT work_info FROM stats WHERE id='"+data+"'";
                    try {
                        ResultSet rs = DatabaseConnection.exQuery(query);
                        work_info.setText(rs.getString("work_info"));
                        if(rs!=null) {
                            rs.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                }
            });
            dataView.setItems(dataList);


        } catch (Exception e) {
//            System.err.println(e.getMessage());
        }

    }

    @FXML
    public void generateOsw(MouseEvent mouseEvent) throws DocumentException, IOException, ParserConfigurationException, SAXException {
        System.out.println("Creating PDF");
        String fileName = firstName + "_" + lastName + "_oswiadczenie_";
        System.out.println(firstname);


        CreatePDF.createPDF(new String[]{
                "                                          " +
                        "                   " +
                "ZAŚWIADCZENIE O DZIAŁALNOŚCI " +
                        "                                                                   " +
                        "                                             " +
                        "(ROZPORZĄDZENIE (WE) NR 561/2006 LUB AETR (1))" +
                        "                                          " +
                "                                      \n" +
                "Część wypełniana przez przedsiębiorstwo: \n" +
                " 1. Nazwa firmy:         ..............................................................................................................................\n" +
                " 2. Ulica i numer:        ..............................................................................................................................\n" +
                " 3. Kod pocztowy:       .............................................................................................................................. \n" +
                " 4. Miejscowość:        .............................................................................................................................. \n" +
                " 5. Państwo:               .............................................................................................................................\n" +
                " 6. Numer telefonu:      ..............................................................................................................................\n" +
                " 7. Numer faksu:          ..............................................................................................................................\n" +
                " 8. Adres email:           ..............................................................................................................................\n\n" +
                "Ja niżej podpisany: \n" +
                " 9. Imię i nazwisko:        ..............................................................................................................................\n" +
                " 10. Stanowisko:              ................................................................................................................................\n\n" +
                "oświadczam, że kierowca: \n" +
                " 11. Imię nazwisko: " + firstname.getText() + " " + lastname.getText() + "\n" +
                " 12. Data urodzenia: " + Born + "\n" +
                " 13. Karta kierowcy: " + cardNumber + "\n\n" +
                "w okresie: \n" +
                " 14. od (godzina-dzień-miesiąc-rok): ...............................................................................................................\n" +
                " 15. do (godzina-dzień-miesiąc-rok): ...............................................................................................................\n\n" +
                " 16. [  ] pozostawał w gotowości (*)\n" +
                " 17. [  ] przebywał na zwolnieniu chorobowym (*)\n" +
                " 18. [  ] przebywał na urlopie wypoczynkowym (*)\n" +
                " 18. [  ] miał czas wolny od pracy lub odpoczywał (*)\n" +
                " 19. [  ] wykonywał pracę inną niż prowadzenie pojazdu (*)\n" +
                " 20. [  ] prowadził pojazd wyłączony z zakresu stosowania rozporządzenia (WE) nr 561/2006 lub AETR (*)\n\n\n" +
                "                                          " +
                "                                          " +
                "                                   " +
                " ................................................................" +
                "                                          " +
                "                                          " +
                "                                          " +
                "" +
                "(Miejscowość, data i podpis pracownika)\n\n" +
                " 21. Ja, jako kierowca, potwierdzam, że w wyżej wymienionym okresie nie prowadziłem pojazdu wchodzącego\n       w zakres stosowania rozporządzenia (WE) nr 561/2006 lub AETR. \n" +
                        "                                          " +
                        "                                          " +
                        "                                      " +
                        " ................................................................" +
                        "                                          " +
                        "                                          " +
                        "                                          " +
                        "" +
                        "(Miejscowość, data i podpis kierowcy)\n\n" +
                        "----------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                        "(1) Umowa europejska dotyczy pracy zasług pojazdów wykonujących międzynarodowa przewozy drogowe.\n" +
                        "(*) Można wybrać tylko jedną z rubryk." +
                        ""} ,fileName,"" );

    }
}
