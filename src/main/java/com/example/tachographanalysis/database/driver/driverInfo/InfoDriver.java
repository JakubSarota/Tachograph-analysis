package com.example.tachographanalysis.database.driver.driverInfo;

import com.example.tachographanalysis.DigitalAnalysisController;
import com.example.tachographanalysis.PDF.CreatePDF;
import com.example.tachographanalysis.database.DatabaseConnection;
import com.example.tachographanalysis.workinfo.WorkInfo;
import com.itextpdf.text.DocumentException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static java.lang.Integer.parseInt;

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
    private DatePicker datePickerSINCE, datePickerTO;
    @FXML
    public TableColumn<Data, String> dateWorkCol, dateAddCol, sumWorkCol, sumBreakCol, fileCol;
    @FXML
    private ObservableList<Data> dataList = FXCollections.observableArrayList();
    @FXML
    private BarChart barChart;
    @FXML
    private AreaChart areaChart,areaChart2;
    static int idDriver;
    static String firstName, lastName, Born, cardNumber, driverId;
//    int idStats, sumLast14DaysOfWork;
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
            driverId=queryOutput.getString("id");
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
    public String setDatePickerSINCE() {
        String date = String.valueOf(datePickerSINCE.getValue());
        return date;
    }
    public String setDatePickerTO() {
        String date = String.valueOf(datePickerTO.getValue());
        return date;
    }

    public void sumDate() {
        if(datePickerSINCE.getValue()!=null && datePickerTO.getValue()!=null) {
            FilteredList<Data> filteredList = new FilteredList<>(dataList, b -> true);
            filteredList.setPredicate(Data -> {
                LocalDate t1 = LocalDate.parse(setDatePickerSINCE());
                LocalDate t2 = LocalDate.parse(setDatePickerTO());
                LocalDate t3 = LocalDate.parse(Data.getDate_work());
                if(t3.isAfter(t1) && t3.isBefore(t2)){
                    return true;
                }
                return false;
            });
            SortedList<Data> sortedData = new SortedList<>(filteredList);
            sortedData.comparatorProperty().bind(dataView.comparatorProperty());
            dataView.setItems(sortedData);
        }
    }
    public void reset() throws SQLException {
        infoDriver();
    }
    public void infoDriver() throws SQLException {
        try {
            dataList = showData.data();
            dataView.setPlaceholder(new Label("Brak danych"));
            dateAddCol.setCellValueFactory(new PropertyValueFactory<>("data_add"));
            dateWorkCol.setCellValueFactory(new PropertyValueFactory<>("date_work"));
            sumWorkCol.setCellValueFactory(new PropertyValueFactory<>("sum_work"));
            sumBreakCol.setCellValueFactory(new PropertyValueFactory<>("sum_break"));
            sumRoadCol.setCellValueFactory(new PropertyValueFactory<>("sum_road"));
            dataView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Data>() {
                @Override
                public void changed(ObservableValue<? extends Data> observable, Data oldValue, Data newValue) {
                    data = newValue.id;
//                    System.err.println(data);
                    String query = "SELECT work_info FROM stats WHERE id='"+data+"' ORDER BY id";
                    try {
                        ResultSet rs = DatabaseConnection.exQuery(query);
                        work_info.setText(rs.getString("work_info"));

//                        System.out.println(data);
                        Object[] dataDiffOneDaTable = DigitalAnalysisController.dataDiffOneDay(work_info.getText());
                        String[] activityDataWork = (String[]) dataDiffOneDaTable[0];
                        String[] activityDataDrive = (String[]) dataDiffOneDaTable[1];
                        String[] activityDataBreak = (String[]) dataDiffOneDaTable[2];

                        barChart.getData().clear();
                        barChart.getData().removeAll();

                        barChart.setTitle("Aktywność pracownika ");
                        barChart.getXAxis().setLabel("Aktywność");
                        barChart.getYAxis().setLabel("Godziny");
                        barChart.setAnimated(false);

                        XYChart.Series series1 = new XYChart.Series();
                        XYChart.Series series2 = new XYChart.Series();
                        XYChart.Series series3 = new XYChart.Series();

                        series1.setName("Praca");
                        series2.setName("Przerwa");
                        series3.setName("Jazda");
                        series1.getData().add(new XYChart.Data("Praca", parseInt(String.valueOf(DigitalAnalysisController.timeDiffrence(activityDataWork))) ));
                        series2.getData().add(new XYChart.Data("Przerwa", parseInt(String.valueOf(DigitalAnalysisController.timeDiffrence(activityDataBreak))) ));
                        series3.getData().add(new XYChart.Data("Jazda", parseInt(String.valueOf(DigitalAnalysisController.timeDiffrence(activityDataDrive))) ));
                        barChart.getData().addAll(series1);
                        barChart.getData().addAll(series2);
                        barChart.getData().addAll(series3);

                        areaChart.getData().clear();
                        areaChart.getData().removeAll();
                        areaChart2.getData().clear();
                        areaChart2.getData().removeAll();
                        JSONArray json2 = WorkInfo.getDailyActivity(work_info.getText());
                        for (int i = 0; i < json2.length(); i++) {
                            JSONObject j2 = (JSONObject) json2.get(i);
                            String wykrzyknik = "";
                            if (j2.getInt("czas") > 270)
                                wykrzyknik = "!";
                            if (j2.getString("activity").equals("Break")) {
                                XYChart.Series seriesB = new XYChart.Series();
                                seriesB.setName("Break " + j2.getString("czas2"));
                                seriesB.getData().add(new XYChart.Data((float) j2.getInt("start2") / 60, 1));
                                seriesB.getData().add(new XYChart.Data((float) j2.getInt("stop2") / 60, 1));
                                XYChart.Series seriesB2 = new XYChart.Series();
                                seriesB2.setName("Break " + j2.getString("czas2"));
                                seriesB2.getData().add(new XYChart.Data((float) j2.getInt("start2") / 60, 1));
                                seriesB2.getData().add(new XYChart.Data((float) j2.getInt("stop2") / 60, 1));
                                areaChart.getData().addAll(seriesB);
                                areaChart2.getData().addAll(seriesB2);
                            } else {

                                XYChart.Series seriesW = new XYChart.Series();
                                seriesW.setName("Work " + j2.getString("czas2") + wykrzyknik);
                                seriesW.getData().add(new XYChart.Data((float) j2.getInt("start2") / 60, 2));
                                seriesW.getData().add(new XYChart.Data((float) j2.getInt("stop2") / 60, 2));
                                XYChart.Series seriesW2 = new XYChart.Series();
                                seriesW2.setName("Work " + j2.getString("czas2") + wykrzyknik);
                                seriesW2.getData().add(new XYChart.Data((float) j2.getInt("start2") / 60, 2));
                                seriesW2.getData().add(new XYChart.Data((float) j2.getInt("stop2") / 60, 2));
                                areaChart.getData().addAll(seriesW);
                                areaChart2.getData().addAll(seriesW2);
                            }
                        }

                        if(rs!=null) {
                            rs.close();
                        }

                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    } catch (ParseException e) {
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
        String fileName = firstName + "_" + lastName + "_oswiadczenie_o_nie_pracy";
        System.out.println(firstname);


        String createPDF = CreatePDF.createPDF(new String[]{
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

        String[] buttons = {"Zamknij", "Otwórz plik PDF"};
        int rs = JOptionPane.showOptionDialog(null, "Plik PDF został utworzony", "Twórz pdf", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, buttons, buttons[0]);
        switch (rs) {
            case 0:
                return;
            case 1:
                String pathpdf = System.getProperty("user.dir") + "\\PDF\\" + createPDF + ".pdf";
                System.out.println(pathpdf);
                String[] params = {"cmd", "/c", pathpdf};
                try {
                    Runtime.getRuntime().exec(params);
                } catch (Exception e) { }
        }

    }

    @FXML
    public void generateOsw2(MouseEvent mouseEvent) throws DocumentException, IOException, ParserConfigurationException, SAXException {
        Integer sumWork[] = new Integer[14];
        String dateWork[] = new String[14];
        Integer sumLast14DaysOfWork = 0;
        Integer i = 0;
        LocalDate ld= LocalDate.now().with(TemporalAdjusters.previous( DayOfWeek.MONDAY ));
        LocalDate ld2= ld.with(TemporalAdjusters.previous( DayOfWeek.MONDAY ));

        String connectQuery = "SELECT sum_work,date_work FROM stats WHERE driver_id='"+driverId+"'  " +
                "AND date_work>='"+ld.with(TemporalAdjusters.previous( DayOfWeek.MONDAY )) +"'";
        if(LocalDate.now().getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            connectQuery = "SELECT sum_work,date_work FROM stats WHERE driver_id='" + driverId + "'  " +
                    "AND date_work>='" + ld + "'";
            ld2=ld;
        }


        try{
            ResultSet queryOutput = DatabaseConnection.exQuery(connectQuery);
            while (queryOutput.next()){

                sumWork[i] = queryOutput.getInt("sum_work");
                dateWork[i] = queryOutput.getString("date_work");
                sumLast14DaysOfWork+=sumWork[i];
                 System.out.println( sumLast14DaysOfWork );
                i++;
            }

            try {
                if(queryOutput!=null) {
                    queryOutput.close();
                }
            } catch (Exception e) {}
        } catch (Exception e){
            e.printStackTrace();
        }



        System.out.println("Creating PDF");
        String fileName = firstName + "_" + lastName + "_oswiadczenie_o_pracy";
//        System.out.println(firstname);


        String createPDF = CreatePDF.createPDF(new String[]{
                "                                          " +
                        "                   " +
                        "ZAŚWIADCZENIE O DZIAŁALNOŚCI " +
                        "                                                                   " +
                        "                                             " +
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
                        "w okresie (14 dni): \n" +
                        " 14. od (rok-miesiąc-dzień): "+ld2+"\n" +
                        " 15. do (rok-miesiąc-dzień): "+LocalDate.now()+" \n" +
                        " 16. Przepracował (jazda + praca) : " + sumLast14DaysOfWork + " godzin   \n\n" +
                        "                                          " +
                        "                                   " +
                        "                                   " +
                        "         " +
                        " ................................................................" +
                        "                                          " +
                        "                                          " +
                        "                                          " +
                        "" +
                        "(Miejscowość, data i podpis pracownika)\n\n" +
                        " 21. Ja, jako kierowca, potwierdzam, że w wyżej wymienionym okresie prowadziłem pojazd wchodzący\n \n" +
                        "                                          " +
                        "                                          " +
                        "                                      " +
                        " ................................................................" +
                        "                                          " +
                        "                                          " +
                        "                                          " +
                        "" +
                        "(Miejscowość, data i podpis kierowcy)\n\n" +
                        "                                          " +
                        "                                          " +
                        "" +
                        ""} ,fileName,"" );

        String[] buttons = {"Zamknij", "Otwórz plik PDF"};
        int rs = JOptionPane.showOptionDialog(null, "Plik PDF został utworzony", "Twórz pdf", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, buttons, buttons[0]);
        switch (rs) {
            case 0:
                return;
            case 1:
                String pathpdf = System.getProperty("user.dir") + "\\PDF\\" + createPDF+ ".pdf";
                System.out.println(pathpdf);
                String[] params = {"cmd", "/c", pathpdf};
                try {
                    Runtime.getRuntime().exec(params);
                } catch (Exception e) { }
        }

    }
}
