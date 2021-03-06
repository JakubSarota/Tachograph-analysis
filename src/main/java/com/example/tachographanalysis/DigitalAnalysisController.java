package com.example.tachographanalysis;

import com.example.tachographanalysis.PDF.CreatePDF;
import com.example.tachographanalysis.database.DatabaseConnection;
import com.example.tachographanalysis.database.stats.AddStats;
import com.example.tachographanalysis.size.SizeController;
import com.example.tachographanalysis.workinfo.WorkInfo;
import com.itextpdf.text.DocumentException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;


public class DigitalAnalysisController implements Initializable {

    @FXML
    private Text TitleFileName, TextLoading;
    @FXML
    private BarChart barChart;
    @FXML
    private BarChart barChartTMP;
    @FXML
    private AreaChart chart;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private DatePicker dataPicker;
    @FXML
    private Tab one, two, three, four, five;
    @FXML
    private TabPane tabPane;
    @FXML
    private TextArea dailyTextAreaDataChart, firstTabPaneText, secondTabPaneText, thirdTabPaneText, fourthTabPaneText;
    @FXML
    private Button btnBack, btnUpload, btnRaport, loadAnotherFile, btnOpenAnalogue;
    @FXML
    public Button dragOver;
    @FXML
    private File file;
    @FXML
    private File file1;
    @FXML
    private String DDDFile;
    @FXML
    private Button btnRaportPDF;
    @FXML
    private Button btnRaportPDFdnia;
    @FXML
    private Button btnAddStatsDigital;
    @FXML
    private Button btnAddStatsDigitalAll;
    @FXML
    private TextField sumRoad;
    @FXML
    private VBox draganddropPane, dataDigital;

    @FXML
    private AreaChart areaChart, areaChart2;
    List<String> lstFile;
    private String inThisDayData;
    static String PDF = "";
    static String dataT = "";
    public static File filexmlStats;
    static String[] dataGD;
    public static String dataPick;
    static String savedData = "";
    static String dataPick1;
    static String breakSum, workSum, serial;
    public int id;
    static String lastDayOfWork = "";
    static int counterEnter = 0;
    private String lastDaily, firstDaily;


    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
        chart.getData().removeAll();
    }

    @FXML
    private void handleDragOverButton(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        dragOver.setText("Upu???? tutaj");
        TextLoading.setText("??adowanie...");
    }


    @FXML
    void onDragClickedButton(MouseEvent event) throws Exception {
        TextLoading.setText("??adowanie...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("DDD Files", "*.ddd", "*.DDD", "*.xml"));
        File file = fileChooser.showOpenDialog(new Stage());

        if (file == null) {
            TextLoading.setText("");
        } else {
            draganddropPane.setVisible(false);
            dataDigital.setVisible(true);
            loadAnotherFile.setVisible(true);
            btnOpenAnalogue.setDisable(true);
            this.file = file;
            TitleFileName.setText("Dane z pliku " + file.getName());

            fileChooser.setTitle("Open Resource File");

            InputStream inputStream = new FileInputStream(file);
            Thread.sleep(500);


            String xmlExtCheck = (file.getName().substring(file.getName().length() - 4));
            String xml = ".xml";

            String fileNameXML = file.getName().subSequence(0, file.getName().length() - 4) + ".xml";
            String fileNameDDD = file.getName().subSequence(0, file.getName().length() - 4) + ".ddd";

            Path logFilePath = Paths.get(".\\.log");
            //log current time
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            if (!xmlExtCheck.equals(xml)) {

                System.out.println("To jest plik .ddd");
                try {
                    File dir = new File(".\\ddd_to_xml\\data\\driver\\");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    } else {

                        Thread.sleep(1000);
                        String pathxml = ".\\ddd_to_xml\\data\\driver\\" + file.getName().subSequence(0, file.getName().length() - 4) + ".DDD";
                        File f = new File(pathxml);
                        f.createNewFile();
                        System.out.println(pathxml);
                        OutputStream outputStream = new FileOutputStream(pathxml);

                        byte[] allBytes = inputStream.readAllBytes();
                        outputStream.write(allBytes);

                        outputStream.close();
                        try {
                            Thread.sleep(1000);
                            Runtime.getRuntime().exec(".\\ddd_to_xml\\tachograph-reader-core.exe", null, new File(".\\ddd_to_xml\\"));
                            Thread.sleep(1000);
                            File filexml = new File(pathxml + ".xml");
                            filexmlStats = filexml;
                            String filexmlSize = String.valueOf(filexml);
                            long bytes = Files.size(Path.of(filexmlSize));
                            long kiloBytes = bytes / 1024;

                            if (filexml.exists() && kiloBytes > 0) {
                                System.out.println("Poprawnie zaimportowano plik .ddd");
                                f.deleteOnExit();
                                String[] readedData = readData(filexml);
                                showData(readedData);

                            } else {

                                if (Files.exists(logFilePath)) {
                                    FileWriter logDataWrite = new FileWriter(".\\.log", true);
                                    logDataWrite.append(dtf.format(now) + " b????d plik " + fileNameDDD + " nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony\n");
                                    logDataWrite.close();
                                    f.delete();
                                    TextLoading.setText("Spr??buj ponownie");
                                    dataDigital.setVisible(false);
                                    draganddropPane.setVisible(true);
                                    System.out.println("B????d plik nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony");

                                } else {
                                    FileWriter logDataWrite = new FileWriter(".\\.log");
                                    logDataWrite.append(dtf.format(now) + " b????d plik '" + fileNameDDD + "'' nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony\n");
                                    logDataWrite.close();
                                    f.delete();
                                    TextLoading.setText("Spr??buj ponownie");
                                    dataDigital.setVisible(false);
                                    draganddropPane.setVisible(true);
                                    System.out.println("B????d plik nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony");
                                }
                            }

                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                } catch (IOException e) {

                    if (Files.exists(logFilePath)) {
                        FileWriter logDataWrite = new FileWriter(".\\.log", true);
                        logDataWrite.append(dtf.format(now) + " B????d konwersja nie przebieg??a pomy??lnie\n");
                        logDataWrite.close();
                        System.out.println("B????d konwersja nie przebieg??a pomy??lnie");
                        TextLoading.setText("Spr??buj ponownie");
                        dataDigital.setVisible(false);
                        draganddropPane.setVisible(true);
                        loadAnotherFile.setVisible(false);
                    } else {
                        FileWriter logDataWrite = new FileWriter(".\\.log");
                        logDataWrite.append(dtf.format(now) + "B????d konwersja nie przebieg??a pomy??lnie\n");
                        logDataWrite.close();
                        System.out.println("B????d konwersja nie przebieg??a pomy??lnie");
                        TextLoading.setText("Spr??buj ponownie");
                        dataDigital.setVisible(false);
                        draganddropPane.setVisible(true);
                        loadAnotherFile.setVisible(false);
                    }
                    e.printStackTrace();
                }
            } else {


                String filexmlSize = String.valueOf(file);
                long bytesXML = Files.size(Path.of(filexmlSize));
                long kiloBytesXML = bytesXML / 1024;

                if (kiloBytesXML > 0) {
                    System.out.println("Poprawnie zaimportowano plik .xml");
                    String[] readedData = readData(file);
                    showData(readedData);
                } else {

                    if (Files.exists(logFilePath)) {
                        FileWriter logDataWrite = new FileWriter(".\\.log", true);
                        logDataWrite.append(dtf.format(now) + " b????d plik " + fileNameXML + " nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony\n");
                        logDataWrite.close();
                        file.delete();
                        dataDigital.setVisible(false);
                        draganddropPane.setVisible(true);
                        loadAnotherFile.setVisible(false);
                        TextLoading.setText("Spr??buj ponownie");
                        System.out.println("B????d plik XML nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony");
                    } else {
                        FileWriter logDataWrite = new FileWriter(".\\.log");
                        logDataWrite.append(dtf.format(now) + " b????d plik: '" + fileNameXML + "' nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony \n");
                        logDataWrite.close();
                        file.delete();
                        dataDigital.setVisible(false);
                        draganddropPane.setVisible(true);
                        loadAnotherFile.setVisible(false);
                        TextLoading.setText("Spr??buj ponownie");
                        System.out.println("B????d plik XML nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony");
                    }
                }
            }
        }
    }

    @FXML
    private void generatePDF2() throws DocumentException, IOException, ParserConfigurationException, SAXException, InterruptedException {

        String createPDF = CreatePDF.createPDF(dataGD, String.valueOf(this.file.getName()), "");
        String[] buttons = {"Zamknij", "Otw??rz plik PDF"};
        int rs = JOptionPane.showOptionDialog(null, "Plik PDF zosta?? utworzony", "Tw??rz pdf", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, buttons, buttons[0]);
        switch (rs) {
            case 0:
                return;
            case 1:
                String pathpdf = System.getProperty("user.dir") + "\\PDF\\" + createPDF + ".pdf";
                System.out.println(pathpdf);
                String[] params = {"cmd", "/c", pathpdf};
                try {
                    Runtime.getRuntime().exec(params);
                    Runtime.getRuntime().exec(params);
                } catch (Exception e) {
                }
        }
    }

    @FXML
    private void generatePDF3() throws DocumentException, IOException, ParserConfigurationException, SAXException, InterruptedException {

        String createPDF = CreatePDF.createPDF2(new String[]{inThisDayData}, String.valueOf(this.file.getName()) + dataPick, "", areaChart2);
        String[] buttons = {"Zamknij", "Otw??rz plik PDF"};
        int rs = JOptionPane.showOptionDialog(null, "Plik PDF zosta?? utworzony", "Tw??rz pdf", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, buttons, buttons[0]);
        switch (rs) {
            case 0:
                return;
            case 1:
                String pathpdf = System.getProperty("user.dir") + "\\PDF\\" + createPDF + ".pdf";
                System.out.println(pathpdf);
                String[] params = {"cmd", "/c", pathpdf};
                try {
                    Runtime.getRuntime().exec(params);
                } catch (Exception e) {
                }
        }
    }

    @FXML
    private void showData(String[] readedData) throws InterruptedException, DocumentException, IOException, ParserConfigurationException, SAXException {
        TextLoading.setText("");
        draganddropPane.setVisible(false);
        dataDigital.setVisible(true);
        TitleFileName.setText("Dane z pliku");
        chart.getData().removeAll();
        chart.getData().clear();
        showWeaklyChart(readedData[1] + " \n\n d");

        try {
            colorPicker();
            dataGD = readedData;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        btnRaportPDF.setVisible(true);
        tabPane.setVisible(true);
        if (btnRaportPDF.isPressed()) {
            generatePDF2();
        }
        if (btnRaportPDFdnia.isPressed()) {
            generatePDF3();
        }
        btnAddStatsDigitalAll.setVisible(true);
        firstTabPaneText.appendText(readedData[0]);

        secondTabPaneText.appendText("");
        dataT = readedData[1] + " \n\n d";
        if (dataPicker != null) {
            visibilityDataPickerEnter();
        }

        thirdTabPaneText.appendText(readedData[2]);
        fourthTabPaneText.appendText(readedData[3]);

//        TextArea driverRoute = new TextArea("");
//        three.setContent(driverRoute);
//        TextArea driverRouteArea = (TextArea) three.getContent();
//        driverRouteArea.appendText(readedData[2]);
//
//
//        TextArea vehicleHistory = new TextArea("");
//        four.setContent(vehicleHistory);
//        TextArea vehicleHistoryData = (TextArea) four.getContent();
//        vehicleHistoryData.appendText(readedData[3]);
//
//
//        TextArea dataChartTwoWeekend = new TextArea("");
//        five.setContent(dataChartTwoWeekend);
//        TextArea dataChartTwoWeekendArea = (TextArea) five.getContent();
//        dataChartTwoWeekendArea.setEditable(false);

        if (!two.isSelected()) {
            dataPicker.setVisible(false);
            btnRaportPDFdnia.setVisible(false);
            btnAddStatsDigital.setVisible(false);
        }
    }
    @FXML
    private void setDataPicker() {
        try {
            setDataPicker2(dataT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void setDataPicker2(String dataXml) throws ParseException {

        String datePickerTime = String.valueOf(dataPicker.getValue());
        String indexOfDataPickerTime = String.valueOf(dataXml.indexOf(datePickerTime));
        inThisDayData = "";
        dataPick = datePickerTime;
        dataPick1 = indexOfDataPickerTime;

//        TextArea dailyData = new TextArea("");
//        two.setContent(dailyData);
//        TextArea dailyDataDriver = (TextArea) two.getContent();

        btnRaportPDFdnia.setVisible(true);
        btnAddStatsDigital.setVisible(true);

        if (!indexOfDataPickerTime.equals("-1")) {
            secondTabPaneText.setText("Dzienna Aktywno????: ");
            int indeksString = parseInt(indexOfDataPickerTime);
            int i = 0;
            while (!String.valueOf(dataXml.charAt(indeksString + i)).equals("d")) {
                secondTabPaneText.appendText("" + dataXml.charAt(parseInt(indexOfDataPickerTime) + i));
                inThisDayData += String.valueOf(dataXml.charAt(parseInt(indexOfDataPickerTime) + i));
                i += 1;
            }


            showChart(inThisDayData);

        }

    }

    @FXML
    private void showChart(String data) throws ParseException {
//        System.out.println(data);
        Object[] dataDiffOneDaTable = dataDiffOneDay(data);

        String selectedDate = data.substring(0, 10);

        String[] activityDataWork = (String[]) dataDiffOneDaTable[0];
        String[] activityDataDrive = (String[]) dataDiffOneDaTable[1];
        String[] activityDataBreak = (String[]) dataDiffOneDaTable[2];
        barChart.getData().clear();
        barChart.getData().removeAll();
        barChartTMP.getData().clear();
        barChartTMP.getData().removeAll();

        areaChart.getData().clear();
        areaChart.getData().removeAll();
        areaChart2.getData().clear();
        areaChart2.getData().removeAll();
        JSONArray json2 = WorkInfo.getDailyActivity(data);
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

        counterEnter++;


        //Barchart dzialaj??cy
        barChart.setVisible(true);

        barChart.setTitle("Aktywno???? pracownika ");
        barChart.getXAxis().setLabel("Aktywno????");
        barChart.getYAxis().setLabel("Godziny");
        barChartTMP.setTitle("Aktywno???? pracownika ");
        barChartTMP.getXAxis().setLabel("Aktywno????");
        barChartTMP.getYAxis().setLabel("Godziny");

        barChart.setAnimated(false);

        XYChart.Series series1 = new XYChart.Series();
        series1.setName(selectedDate);
        series1.getData().add(new XYChart.Data("Praca", parseInt(String.valueOf(timeDiffrence(activityDataWork))) / 60));
        series1.getData().add(new XYChart.Data("Jazda", parseInt(String.valueOf(timeDiffrence(activityDataDrive))) / 60));
        series1.getData().add(new XYChart.Data("Przerwa", parseInt(String.valueOf(timeDiffrence(activityDataBreak))) / 60));
        XYChart.Series series2 = new XYChart.Series();
        series2.setName(selectedDate);
        series2.getData().add(new XYChart.Data("Praca", parseInt(String.valueOf(timeDiffrence(activityDataWork))) / 60));
        series2.getData().add(new XYChart.Data("Jazda", parseInt(String.valueOf(timeDiffrence(activityDataDrive))) / 60));
        series2.getData().add(new XYChart.Data("Przerwa", parseInt(String.valueOf(timeDiffrence(activityDataBreak))) / 60));
        workSum = String.valueOf(parseInt(String.valueOf(timeDiffrence(activityDataWork) / 60)) + parseInt(String.valueOf(timeDiffrence(activityDataDrive) / 60)));
        breakSum = String.valueOf(parseInt(String.valueOf(timeDiffrence(activityDataBreak))) / 60);
        barChart.getData().addAll(series1);
        barChartTMP.getData().addAll(series2);

        savedData += selectedDate;
        counterEnter++;
    }

    // Last two weeks driver data
    @FXML
    private void showWeaklyChart(String readedData) {

        int lastIndexOfData = readedData.length();
        String splitedData[] = new String[lastIndexOfData];
        String newSplitedData[] = new String[lastIndexOfData];
        String twoWeeksData[] = new String[14];
        for (int i = 0; i < readedData.length(); i++) {
            splitedData[i] = String.valueOf(readedData.charAt(i));
        }

        int j = 0;

        for (int i = lastIndexOfData - 1; i > 0; i--) {
            newSplitedData[j] = String.valueOf(readedData.charAt(i));
            j++;
        }

        int days = 0;
        String dataday = "";

        for (int i = 0; i < readedData.length() - 1; i++) {
            dataday = "";

            if (days == 14) {
                break;
            }
            if (newSplitedData[i].equals("Z")) {
                for (int k = 10; k >= 1; k--) {
                    dataday += newSplitedData[i + 9 + k];

                }
                twoWeeksData[days] = dataday;
                days++;

            }
        }
        String twoWeeksDataCorrectly[] = new String[14];
        int altI = 0;
        for (int i = 13; i >= 0; i--) {
            twoWeeksDataCorrectly[altI] = twoWeeksData[i];
            altI++;
        }


        String inThisDayData[] = new String[14];
        String dailyDataDriver = "";
        int counter14days = 0;
        String indexOfDataPickerTime[] = new String[14];

        for (int i = 0; i < 14; i++) {
            indexOfDataPickerTime[i] = String.valueOf(readedData.indexOf(twoWeeksDataCorrectly[i]));
        }


        while (counter14days != 14) {
            int indeksString = parseInt(indexOfDataPickerTime[counter14days]);
            int i = 0;

            while ((!String.valueOf(readedData.charAt(indeksString + i)).equals("d"))) {
                inThisDayData[counter14days] += String.valueOf(readedData.charAt(parseInt(indexOfDataPickerTime[counter14days]) + i));
                i += 1;
            }
            counter14days++;
//            }
        }

        String[] inThisDayDataCorrectly = new String[14];
        int lastIndexOfTheDay = 0;

        for (int i = 0; i < 14; i++) {
            if (inThisDayData[i].startsWith("null")) {
                lastIndexOfTheDay = inThisDayData[i].length();
                inThisDayDataCorrectly[i] = inThisDayData[i].substring(4, lastIndexOfTheDay);
            } else {
                inThisDayDataCorrectly[i] = inThisDayData[i].substring(0, lastIndexOfTheDay);
            }
        }

        String[] activityDataWork = new String[0];
        String[] activityDataBreak = new String[0];
        String[] activityDataDrive = new String[0];

        Object[] activityDataWorkObject = new Object[14];
        Object[] activityDataDriveObject = new Object[14];
        Object[] activityDataBreakObject = new Object[14];


        for (int i = 0; i < 14; i++) {

            Object[] dataDiffOneDaTable = dataDiffOneDay(inThisDayDataCorrectly[i]);

            activityDataWork = (String[]) dataDiffOneDaTable[0];
            activityDataDrive = (String[]) dataDiffOneDaTable[1];
            activityDataBreak = (String[]) dataDiffOneDaTable[2];

            activityDataWorkObject[i] = activityDataWork;
            activityDataDriveObject[i] = activityDataDrive;
            activityDataBreakObject[i] = activityDataBreak;

        }


        chart.setTitle("Dwutygodniowa aktywno???? pracownka ");
        chart.getXAxis().setLabel("Aktywno????");
        chart.getYAxis().setLabel("Godziny");

        chart.setAnimated(false);

        XYChart.Series seriesChart1 = new XYChart.Series();
        seriesChart1.setName("Praca");
        XYChart.Series seriesChart2 = new XYChart.Series();
        seriesChart2.setName("Przerwa");
        XYChart.Series seriesChart3 = new XYChart.Series();
        seriesChart3.setName("Jazda");
        for (int i = 0; i < twoWeeksDataCorrectly.length; i++) {
            seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[i], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[i]))) / 60));
            seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[i], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[i]))) / 60));
            seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[i], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[i]))) / 60));
        }


        chart.getData().addAll(seriesChart1, seriesChart3, seriesChart2);


    }

    @FXML
    public static Object[] dataDiffOneDay(String data) {
//        System.out.println(data);
        int firstActivity = data.indexOf("Aktywno????");
        int bTime = 0;

        String activity[] = new String[3];

        activity[0] = "Break";
        activity[1] = "Driving";
        activity[2] = "Work";


        String split[] = (data.split(" "));

        int count = 0;
        int count2 = 0;

        // break hours

        for (int i = 0; i < split.length; i++) {
            if (activity[0].equals(split[i])) {
                count++;
            }
        }


        String activityDataBreak[] = new String[count + 1];

        for (int i = 0; i < split.length; i++) {
            if (activity[0].equals(split[i])) {
                count2++;
                if (split.length - i == 4) {
                    activityDataBreak[count2] = split[i + 2] + "24:00";
                } else {
                    if (i + 7 < split.length)
                        activityDataBreak[count2] = split[i + 2] + split[i + 7];
                    else
                        activityDataBreak[count2] = split[i + 2] + "24:00";
                }
            }
        }

        // drive hours
        count = 0;
        count2 = 0;

        for (int i = 0; i < split.length; i++) {
            if (activity[1].equals(split[i])) {
                count++;
            }
        }

        String activityDataDrive[] = new String[count + 1];

        for (int i = 0; i < split.length; i++) {

            if (activity[1].equals(split[i])) {
                count2++;

                if (split.length - i == 4) {
                    activityDataDrive[count2] = split[i + 2] + "24:00";
                } else {
                    activityDataDrive[count2] = split[i + 2] + split[i + 7];
                }

            }
        }


        // work hours
        count = 0;
        count2 = 0;

        for (int i = 0; i < split.length; i++) {
            if (activity[2].equals(split[i])) {
                count++;
            }
        }

        String activityDataWork[] = new String[count + 1];

        for (int i = 0; i < split.length; i++) {
            if (activity[2].equals(split[i])) {
                count2++;

                if (split.length - i == 4) {
                    activityDataWork[count2] = split[i + 2] + "24:00";
                } else {
                    activityDataWork[count2] = split[i + 2] + split[i + 7];
                }
            }
        }

        Object[] timeDiffOneDayTable = new Object[3];

        timeDiffOneDayTable[0] = activityDataWork;
        timeDiffOneDayTable[1] = activityDataDrive;
        timeDiffOneDayTable[2] = activityDataBreak;


        return timeDiffOneDayTable;


    }

    @FXML
    public static int timeDiffrence(String[] activity) {

        int sumActivityDataBreakM = 0;
        String[] dateBreakHM = new String[8];
        String start = "";
        String stop = "";

        for (int i = 1; i < activity.length; i++) {
            dateBreakHM[0] = String.valueOf(activity[i].charAt(0));
            dateBreakHM[1] = String.valueOf(activity[i].charAt(1));
            dateBreakHM[2] = String.valueOf(activity[i].charAt(3));
            dateBreakHM[3] = String.valueOf(activity[i].charAt(4));
            dateBreakHM[4] = String.valueOf(activity[i].charAt(6));
            dateBreakHM[5] = String.valueOf(activity[i].charAt(7));
            dateBreakHM[6] = String.valueOf(activity[i].charAt(9));
            dateBreakHM[7] = String.valueOf(activity[i].charAt(10));

            start = dateBreakHM[0] + dateBreakHM[1] + ":" + dateBreakHM[2] + dateBreakHM[3];
            stop = dateBreakHM[4] + dateBreakHM[5] + ":" + dateBreakHM[6] + dateBreakHM[7];
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date date1 = null;
            try {
                date1 = format.parse(start);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date date2 = null;
            try {
                date2 = format.parse(stop);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long diffrence = date2.getTime() - date1.getTime();

            sumActivityDataBreakM += diffrence / 1000 / 60;

        }
        return sumActivityDataBreakM;
    }

    @FXML
    private void visibilityDataPickerLeave() {

        if (dataT.length() != 0) {

            if (dataPicker != null) {
                dataPicker.setVisible(false);
            }
            if (btnRaportPDFdnia != null) {
                btnRaportPDFdnia.setVisible(false);
            }
            if (btnAddStatsDigital != null) {
                btnAddStatsDigital.setVisible(false);
            }
            if (chart != null) {
                chart.setVisible(false);
            }
            if (barChart != null) {
                barChart.setVisible(false);
            }
            savedData = "";


        }

    }

    @FXML
    private void visibilityDataPickerEnter() {

        dataPicker.setVisible(true);
        barChart.setVisible(false);
        secondTabPaneText.clear();
        barChart.getData().clear();
        secondTabPaneText.clear();
        barChartTMP.getData().clear();
        dataPicker.getEditor().clear();
        chart.setVisible(false);

        int lastZLatter = dataT.lastIndexOf("Z");
        lastDayOfWork = String.valueOf(dataPicker.getValue());
        int lastDataIndex = lastZLatter - 19;
        String lastaDataString = (dataT.substring(lastDataIndex, lastDataIndex + 10));
        //Nr ostatniego dnia pracy
        lastDaily = (dataT.substring(lastDataIndex + 55, lastDataIndex + 58));
        int firstZLatter = dataT.indexOf("Z");
        int firstDataIndex = firstZLatter - 19;
        firstDaily = (dataT.substring(firstDataIndex + 55, firstDataIndex + 58));

        String year = (lastaDataString.substring(0, 4));
        String month = (lastaDataString.substring(5, 7));
        String day = (lastaDataString.substring(8, 10));

        int parseDay = parseInt(day);
        int parseMonth = parseInt(month);
        int parseYear = parseInt(year);
        if ((parseMonth == 4 || parseMonth == 6 || parseMonth == 9 || parseMonth == 11) && parseDay == 30 && parseMonth != 12) {
            dataPicker.setValue(LocalDate.of(parseYear, parseMonth + 1, 1));
        } else if (parseMonth == 2 && parseDay == 28) {
            dataPicker.setValue(LocalDate.of(parseYear, parseMonth + 1, 1));
        } else if (parseMonth == 12 && parseDay == 31) {
            dataPicker.setValue(LocalDate.of(parseYear + 1, 1, 1));
        } else if ((parseMonth == 1 || parseMonth == 3 || parseMonth == 5 || parseMonth == 7 || parseMonth == 8 || parseMonth == 10 || parseMonth == 12) && parseDay == 31) {
            dataPicker.setValue(LocalDate.of(parseYear, parseMonth + 1, 1));
        } else {
            dataPicker.setValue(LocalDate.of(parseYear, parseMonth, parseDay + 1));
        }

    }


    @FXML
    private void visiblityChartArea() {
        chart.setVisible(true);
        dataPicker.setVisible(false);
    }

    @FXML
    private void btnRaportPDFdnia() {
        btnRaportPDFdnia.setVisible(true);
    }

    private void colorPicker() throws ParserConfigurationException {

        List<LocalDate> work = new ArrayList<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = null;
        try {
            doc = db.parse(PDF);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();
        NodeList CardActivityDailyRecord = doc.getElementsByTagName("CardActivityDailyRecord");

        for (int i = 0; i < CardActivityDailyRecord.getLength(); i++) {

            String year = "";
            String month = "";
            String day = "";
            String dataCalendar = CardActivityDailyRecord.item(i).getAttributes().item(1).getNodeValue();

            year += dataCalendar.charAt(0);
            year += dataCalendar.charAt(1);
            year += dataCalendar.charAt(2);
            year += dataCalendar.charAt(3);

            month += dataCalendar.charAt(5);
            month += dataCalendar.charAt(6);

            day += dataCalendar.charAt(8);
            day += dataCalendar.charAt(9);


            work.add(LocalDate.of(parseInt(year), parseInt(month), parseInt(day)));
        }


        dataPicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty && item != null) {
                            if (work.contains(item)) {
                                this.setStyle("-fx-background-color: pink");
                            } else {
                                this.setDisable(true);
                            }
                        }
                    }
                };
            }
        });
    }

    public static String[] readData(File filexml) throws Exception {

        PDF = String.valueOf(filexml);


        String generalDataS = "";
        String dailyActivityS = "";
        String driverRouteS = "";
        String mileageCarS = "";

        String[] xmlDate = new String[4];
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(filexml);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("DriverData");
            NodeList CardVehicleRecord = doc.getElementsByTagName("CardVehicleRecords");
            System.out.println("Wyswietlanie danych ");

            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node nodeVehic = CardVehicleRecord.item(itr);
                Node node = nodeList.item(itr);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    Element elElement = (Element) nodeVehic;

                    generalDataS = (filexml.getName() + ", ");
                    generalDataS += (eElement.getElementsByTagName("CardHolderSurname").
                            item(0).getTextContent() + ", ");

                    generalDataS += (eElement.getElementsByTagName("CardHolderFirstNames").
                            item(0).getTextContent() + ", ");
                    generalDataS += (eElement.getElementsByTagName("CardHolderPreferredLanguage").
                            item(0).getTextContent() + "\n\n");
                    // CardExtendedSerialNumber

                    // doda?? serial number / data / rfu/  (jak wyci??ga si?? value xml elementu month year itp..)
                    generalDataS += ("Identyfikacja karty ICC:  \n");
                    generalDataS += ("\t ClockStop: " + eElement.getElementsByTagName("ClockStop").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t CardExtendedSerialNumber: " + eElement.getElementsByTagName("CardExtendedSerialNumber").
                            item(0).getTextContent() + "\n");
                    serial = (eElement.getElementsByTagName("CardExtendedSerialNumber").
                            item(0).getTextContent());
                    generalDataS += ("\t Numer zatwierdzenia karty: " + eElement.getElementsByTagName("CardApprovalNumber").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t CardPersonaliserId: " + eElement.getElementsByTagName("CardPersonaliserId").
                            item(0).getTextContent() + "\n");
                    //EmbedderIcAssemblerId
                    generalDataS += ("\t EmbedderIcAssemblerId: " + "\n");
                    generalDataS += ("\t\t -CountryCode: " + eElement.getElementsByTagName("CountryCode").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t -ModuleEmbedder: " + eElement.getElementsByTagName("CountryCode").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t -ManufacturerInformation: " + eElement.getElementsByTagName("ManufacturerInformation").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t IcIdentifier: " + eElement.getElementsByTagName("IcIdentifier").
                            item(0).getTextContent() + "\n");
                    //CardChipIdentyfiaction
                    generalDataS += ("CardChipIdentification: " + eElement.getElementsByTagName("CardChipIdentification").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS += ("\t IcSerialNumber: " + eElement.getElementsByTagName("IcSerialNumber").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS += ("\t IcManufacturingReferences: " + eElement.getElementsByTagName("IcManufacturingReferences").
                            item(0).getTextContent() + "\n");
                    //DriverCardApplicationIdentyfication
                    generalDataS += (" DriverCardApplicationIdentification: " + "\n");
                    generalDataS += ("\t Type: " + eElement.getElementsByTagName("Type").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t Version: " + eElement.getElementsByTagName("Version").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t NoOfEventsPerType: " + eElement.getElementsByTagName("NoOfEventsPerType").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t NoOfFaultsPerType: " + eElement.getElementsByTagName("NoOfFaultsPerType").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t ActivityStructureLength: " + eElement.getElementsByTagName("ActivityStructureLength").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t NoOfCardVehicleRecords: " + eElement.getElementsByTagName("NoOfCardVehicleRecords").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t NoOfCardPlaceRecords: " + eElement.getElementsByTagName("NoOfCardPlaceRecords").
                            item(0).getTextContent() + "\n");
                    //CardCertificate
                    generalDataS += (" CardCertificate: " + "\n");
                    //ext value
                    generalDataS += ("\t Signature: " + eElement.getElementsByTagName("Signature").
                            item(0).getTextContent() + "\n");
                    //ext value
                    generalDataS += ("\t PublicKeyRemainder: " + eElement.getElementsByTagName("PublicKeyRemainder").
                            item(0).getTextContent() + "\n");
                    //CertificationAuthorityReference
                    generalDataS += ("\t CertificationAuthorityReference: " + "\n");
                    generalDataS += ("\t\t Nation: " + eElement.getElementsByTagName("Nation").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t NationCode: " + eElement.getElementsByTagName("NationCode").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t SerialNumber: " + eElement.getElementsByTagName("SerialNumber").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t AdditionalInfo: " + eElement.getElementsByTagName("AdditionalInfo").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t CaIdentifier: " + eElement.getElementsByTagName("CaIdentifier").
                            item(0).getTextContent() + "\n");
                    //Identification
                    generalDataS += (" Identification: " + "\n");
                    //CardIdentyfication
                    generalDataS += ("\t CardIdentification: " + "\n");
                    generalDataS += ("\t\t CardIssuingMemberState: " + eElement.getElementsByTagName("CardIssuingMemberState").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS += ("\t\t CardNumber: " + eElement.getElementsByTagName("CardNumber").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t CardIssuingAuthorityName: " + eElement.getElementsByTagName("CardIssuingAuthorityName").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS += ("\t\t CardIssueDate: " + eElement.getElementsByTagName("CardIssueDate").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS += ("\t\t CardValidityBegin: " + eElement.getElementsByTagName("CardValidityBegin").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS += ("\t\t CardExpiryDate: " + eElement.getElementsByTagName("CardExpiryDate").
                            item(0).getTextContent() + "\n");
                    //DriverCardHolderIdentyfication
                    generalDataS += ("\t DriverCardHolderIdentification: " + eElement.getElementsByTagName("DriverCardHolderIdentification").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t CardHolderSurname: " + eElement.getElementsByTagName("CardHolderSurname").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t CardHolderFirstNames: " + eElement.getElementsByTagName("CardHolderFirstNames").
                            item(0).getTextContent() + "\n");
                    // ect value
                    generalDataS += ("\t\t CardHolderBirthDate: " + eElement.getElementsByTagName("CardHolderBirthDate").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t CardHolderPreferredLanguage: " + eElement.getElementsByTagName("CardHolderPreferredLanguage").
                            item(0).getTextContent() + "\n");
                    //CardDrivingLicenceInformation
                    generalDataS += ("\t CardDrivingLicenceInformation: " + eElement.getElementsByTagName("CardDrivingLicenceInformation").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t DrivingLicenceIssuingAuthority: " + eElement.getElementsByTagName("DrivingLicenceIssuingAuthority").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t DrivingLicenceIssuingNation: " + eElement.getElementsByTagName("DrivingLicenceIssuingNation").
                            item(0).getTextContent() + "\n");
                    generalDataS += ("\t\t DrivingLicenceNumber: " + eElement.getElementsByTagName("DrivingLicenceNumber").
                            item(0).getTextContent() + "\n");


                    // Aktywno??c dzienna kierowcy
                    int k = 0;
                    NodeList CardActivityDailyRecord = doc.getElementsByTagName("CardActivityDailyRecord");

                    for (int i = 0; i < CardActivityDailyRecord.getLength(); i++) {
                        NodeList t = CardActivityDailyRecord.item(i).getChildNodes();
                        dailyActivityS += (" \n\n data aktywno??ci: " + CardActivityDailyRecord.item(i).
                                getAttributes().item(1).getNodeValue() + " \n");
                        dailyActivityS += (" Dystans : " + CardActivityDailyRecord.item(i).
                                getAttributes().item(2).getNodeValue() + " km \n");
                        dailyActivityS += (" Dzie?? pracy: " + CardActivityDailyRecord.item(i).
                                getAttributes().item(0).getNodeValue() + " \n\n");
                        for (int j = 0; j < t.getLength(); j++) {
                            if (t.item(j).getAttributes() != null) {
                                dailyActivityS += (" \t Aktywno????: " + t.item(j).getAttributes().getNamedItem("Activity"));
                                dailyActivityS += (" Czas: " + t.item(j).getAttributes().getNamedItem("Time") + "\n");
                            }

                        }

                    }

                    // Trasa kierowcy
                    NodeList elPlaceRecord = doc.getElementsByTagName("PlaceRecord");

                    NodeList EntryTime = doc.getElementsByTagName("EntryTime");
                    NodeList DailyWorkPeriodCountry = doc.getElementsByTagName("DailyWorkPeriodCountry"); //atrib

                    driverRouteS += ("\n Trasa kierowcy: \n\n");
                    for (int i = 0; i < elPlaceRecord.getLength(); i++) {
                        driverRouteS += ("\t Kraj: " + DailyWorkPeriodCountry.item(i).getAttributes().item(0).getNodeValue() + " ");
                        driverRouteS += (" Data i godzina: " + EntryTime.item(i).getAttributes().item(0).getNodeValue());
                        driverRouteS += ("  Przebieg: " + eElement.getElementsByTagName("VehicleOdometerValue").item(i).getTextContent() + " km \n");
                    }

                    //Przebieg
                    NodeList elCardVehicleRecord = doc.getElementsByTagName("CardVehicleRecord");

                    NodeList VehicleFirstUse = doc.getElementsByTagName("VehicleFirstUse");
                    NodeList VehicleLastUse = doc.getElementsByTagName("VehicleLastUse"); //atrib

                    mileageCarS += ("\n Dane pojazdu: \n\n");
                    for (int i = 0; i < elCardVehicleRecord.getLength(); i++) {
                        mileageCarS += ("\t Przebieg startowy: " + eElement.getElementsByTagName("VehicleOdometerBegin").item(i).getTextContent() + " km, ");
                        mileageCarS += (" przebieg ko??cowy: " + eElement.getElementsByTagName("VehicleOdometerEnd").item(i).getTextContent() + " km, ");
                        mileageCarS += (" od: " + VehicleFirstUse.item(i).getAttributes().item(0).getNodeValue() + ", ");
                        mileageCarS += (" do: " + VehicleLastUse.item(i).getAttributes().item(0).getNodeValue() + ", ");
                        mileageCarS += (" Numer rejestracyjny: " + elElement.getElementsByTagName("VehicleRegistrationNumber").item(i).getTextContent() + " \n");
                    }
                }
            }
            dailyActivityS = dailyActivityS.replace("Time=", "");
            dailyActivityS = dailyActivityS.replace("\"", "");
            dailyActivityS = dailyActivityS.replace("Activity=", "");
            xmlDate[0] = generalDataS;
            xmlDate[1] = dailyActivityS;
            xmlDate[2] = driverRouteS;
            xmlDate[3] = mileageCarS;

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
        String deleteXML = filexml.getPath();
        if (deleteXML.contains("data") && deleteXML.contains("driver")) {
            filexml.deleteOnExit();
        }
        return xmlDate;
    }


    @FXML
    private void handleDroppedButton(DragEvent event) throws FileNotFoundException {
        List<File> files = event.getDragboard().getFiles();
        List<String> validExtensions = Arrays.asList("ddd", "DDD", "xml");
        file = new File(String.valueOf(new Stage()));


        if (!validExtensions.containsAll(event.getDragboard()
                .getFiles().stream().map(file -> getExtension(file.getName()))
                .collect(Collectors.toList()))) {
            dragOver.setText("To nie jest plik .ddd");
            TextLoading.setText("");
        } else {
            try {
                File filepath = files.get(0);
                System.out.println(filepath);
                dataDigital.setVisible(true);
                loadAnotherFile.setVisible(true);
                btnOpenAnalogue.setDisable(true);
                if (file == null) {

                } else {

                    //

                    InputStream inputStream = new FileInputStream(filepath);
                    Thread.sleep(500);

                    String xmlExtCheck = (filepath.getName().substring(filepath.getName().length() - 4));
                    String xml = ".xml";

                    String fileNameXML = filepath.getName().subSequence(0, filepath.getName().length() - 4) + ".xml";
                    String fileNameDDD = filepath.getName().subSequence(0, filepath.getName().length() - 4) + ".ddd";

//                    System.out.println(filepath);
                    Path logFilePath = Paths.get(".\\.log");
                    //log current time
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    if (!xmlExtCheck.equals(xml)) {
                        System.out.println("To jest plik .ddd");
                        try {
                            File dir = new File(".\\ddd_to_xml\\data\\driver\\");
                            if (!dir.exists()) {
                                dir.mkdirs();
                            } else {

                                Thread.sleep(1000);
                                String pathxml = ".\\ddd_to_xml\\data\\driver\\" + filepath.getName().subSequence(0, filepath.getName().length() - 4) + ".DDD";
                                File f = new File(pathxml);
                                f.createNewFile();
                                System.out.println(pathxml);
                                OutputStream outputStream = new FileOutputStream(pathxml);

                                byte[] allBytes = inputStream.readAllBytes();
                                outputStream.write(allBytes);

                                outputStream.close();
                                try {
                                    Thread.sleep(1000);

                                    Runtime.getRuntime().exec(".\\ddd_to_xml\\tachograph-reader-core.exe", null, new File(".\\ddd_to_xml\\"));
                                    Thread.sleep(1000);
                                    File filexml = new File(pathxml + ".xml");
                                    String filexmlSize = String.valueOf(filexml);
                                    long bytes = Files.size(Path.of(filexmlSize));
                                    long kiloBytes = bytes / 1024;

                                    if (filexml.exists() && kiloBytes > 1) {
                                        System.out.println("Poprawnie zaimportowano plik .ddd");
//                                        System.out.println(filexml);
                                        f.deleteOnExit();
                                        String[] readedData = readData(filexml);
                                        showData(readedData);

                                    } else {

                                        if (Files.exists(logFilePath)) {
                                            FileWriter logDataWrite = new FileWriter(".\\.log", true);
                                            logDataWrite.append(dtf.format(now) + " b????d plik " + fileNameDDD + " nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony\n");
                                            logDataWrite.close();
                                            f.delete();
                                            System.out.println("B????d plik nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony");

                                        } else {
                                            FileWriter logDataWrite = new FileWriter(".\\.log");
                                            logDataWrite.append(dtf.format(now) + " b????d plik '" + fileNameDDD + "'' nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony\n");
                                            logDataWrite.close();
                                            f.delete();
                                            System.out.println("B????d plik nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony");
                                        }
                                    }

                                } catch (InterruptedException ex) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        } catch (IOException e) {

                            if (Files.exists(logFilePath)) {
                                FileWriter logDataWrite = new FileWriter(".\\.log", true);
                                logDataWrite.append(dtf.format(now) + " B????d konwersja nie przebieg??a pomy??lnie\n");
                                logDataWrite.close();
                                System.out.println("B????d konwersja nie przebieg??a pomy??lnie");

                            } else {
                                FileWriter logDataWrite = new FileWriter(".\\.log");
                                logDataWrite.append(dtf.format(now) + "B????d konwersja nie przebieg??a pomy??lnie\n");
                                logDataWrite.close();
                                System.out.println("B????d konwersja nie przebieg??a pomy??lnie");
                            }
                            e.printStackTrace();
                        }
                    } else {


                        String filexmlSize = String.valueOf(filepath);
                        long bytesXML = Files.size(Path.of(filexmlSize));
                        long kiloBytesXML = bytesXML / 1024;

                        if (kiloBytesXML > 1) {
                            System.out.println("Poprawnie zaimportowano plik .xml");
                            String[] readedData = readData(filepath);
                            showData(readedData);
                        } else {

                            if (Files.exists(logFilePath)) {
                                FileWriter logDataWrite = new FileWriter(".\\.log", true);
                                logDataWrite.append(dtf.format(now) + " b????d plik " + fileNameXML + " nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony\n");
                                logDataWrite.close();
                                filepath.delete();
                                System.out.println("B????d plik XML nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony");
                            } else {
                                FileWriter logDataWrite = new FileWriter(".\\.log");
                                logDataWrite.append(dtf.format(now) + " b????d plik: '" + fileNameXML + "' nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony \n");
                                logDataWrite.close();
                                filepath.delete();
                                System.out.println("B????d plik XML nie zosta?? poprawnie za??adowany b??d?? jest uszkodzony");
                            }
                        }

                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1)
            return fileName.substring(i + 1).toLowerCase();

        return extension;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lstFile = new ArrayList<>();
        lstFile.add("*.ddd");
        lstFile.add("*.DDD");
    }

    public void addStatsDigital() throws IOException, SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connectDB = databaseConnection.getDBConnection();

        Statement stmt = connectDB.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT id FROM driver WHERE id_card='" + serial + "'");

        while (rs.next()) {
            id = rs.getInt("id");

        }
        if (id == 0) {
            //JOptionPane.showMessageDialog(null, "W bazie nie ma takiego u??ytkownika!");
            String[] options = {"Dodaj u??ytkownika", "Anuluj"};
            int odp = JOptionPane.showOptionDialog(null, "W bazie nie ma takiego u??ytkownika!", "Uwaga!",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            if (odp == JOptionPane.YES_OPTION) {
                StackPane stackPane = new StackPane();
                Scene secondScene = new Scene(stackPane);
                Stage secondStage = new Stage();
                secondStage.resizableProperty().set(false);
                if (secondStage == null || !secondStage.isShowing()) {
                    Parent fxmlLoader = FXMLLoader.load(getClass().getResource("addDrivers.fxml"));
                    stackPane.getChildren().add(fxmlLoader);
                    secondStage.getIcons().add(new Image(getClass().getResourceAsStream("images/DRIVER.png")));
                    secondStage.setTitle("Dodaj kierowce");
                    secondStage.setScene(secondScene);
                    secondStage.show();
                }
            }

        } else {

//---------------------------------------------------------------------------------------------------------------------//
            int liczbaDni = parseInt(lastDaily) - parseInt(firstDaily);
            String s = inThisDayData.substring(inThisDayData.indexOf("Dystans : ") + 10, inThisDayData.indexOf("km"));
            Pattern p = Pattern.compile("[0-9]+");
            Matcher m = p.matcher(s);
            String d = "0";
            while (m.find()) {
                d = m.group();
            }
            if (d != "0") {
                String file_name = UUID.randomUUID() + ".DDD";
                AddStats.insertToDatabase(parseInt(String.valueOf(id)), dataPicker.getValue().toString(), LocalDate.now().toString(),
                        inThisDayData, workSum, breakSum, file_name, "cyfrowy", Integer.parseInt(d),
                        dataPicker.getValue().toString(), "", "");
                JOptionPane.showMessageDialog(null, "Pomy??lnie dodano!");
            }
        }
    }


    public void addStatsDigitalAll() throws Exception, SQLException {
        String connectQuery = "SELECT id FROM driver WHERE id_card='" + serial + "'";
        try {
            ResultSet queryOutput = DatabaseConnection.exQuery(connectQuery);
            while (queryOutput.next()) {
                id = queryOutput.getInt("id");
            }
            if (id == 0) {
                //JOptionPane.showMessageDialog(null, "W bazie nie ma takiego u??ytkownika!");
                String[] options = {"Dodaj u??ytkownika", "Anuluj"};
                int odp = JOptionPane.showOptionDialog(null, "W bazie nie ma takiego u??ytkownika!", "Uwaga!",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, options, options[0]);
                if (odp == JOptionPane.YES_OPTION) {
                    StackPane stackPane = new StackPane();
                    Scene secondScene = new Scene(stackPane);
                    Stage secondStage = new Stage();
                    if (secondStage == null || !secondStage.isShowing()) {
                        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("addDrivers.fxml"));
                        stackPane.getChildren().add(fxmlLoader);
                        secondStage.getIcons().add(new Image(getClass().getResourceAsStream("images/DRIVER.png")));
                        secondStage.setTitle("Dodaj kierowce");
                        secondStage.setScene(secondScene);
                        secondStage.show();
                    }
                }

            } else {
                int i = 0;
//                String file_name = UUID.randomUUID() + ".DDD";
                File dir = new File(".\\archiwum\\");
                String file_name = UUID.randomUUID().toString() + ".ddd";
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Files.copy(Path.of(String.valueOf(file)),
                        Path.of(".\\archiwum\\" + file_name));
                for (String dataGD1 :
                        dataGD[1].split("data aktywno??ci:")) {


                    String s = dataGD1.substring(dataGD1.indexOf("Dystans : ") + 1, dataGD1.indexOf("km") + 2);
                    Pattern p = Pattern.compile("[0-9]+");
                    Matcher m = p.matcher(s);
                    String d = "0";
                    while (m.find()) {
                        d = m.group();
                    }
                    if (d != "0") {
                        if (dataGD1.length() > 4) {
                            Object[] dataDiffOneDaTable = dataDiffOneDay(dataGD1);
                            String[] activityDataWork = (String[]) dataDiffOneDaTable[0];
                            String[] activityDataDrive = (String[]) dataDiffOneDaTable[1];
                            String[] activityDataBreak = (String[]) dataDiffOneDaTable[2];

                            String tmpS = AddStats.insertToDatabase(parseInt(String.valueOf(id)), dataGD1.substring(1, 11),
                                    LocalDate.now().toString(), dataGD1,
                                    String.valueOf(parseInt(String.valueOf(timeDiffrence(activityDataWork)))  +
                                            parseInt(String.valueOf(timeDiffrence(activityDataDrive))) ),
                                    String.valueOf(parseInt(String.valueOf(timeDiffrence(activityDataBreak))) ),
                                    file_name, "cyfrowy", Integer.parseInt(d),
                                    dataGD1.substring(0, 11), "", "");
                            if (tmpS.equals("Dodano"))
                                i++;
                        }
                    }
                }
                JOptionPane.showMessageDialog(null, "Pomy??lnie dodano " + i + " rekord??w");
                try {
                    if (queryOutput != null) {
                        queryOutput.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (HeadlessException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void openFolder(MouseEvent mouseEvent) {
        Desktop desktop = Desktop.getDesktop();
        File dirToOpen;
        try {
            dirToOpen = new File(".\\PDF\\");
            desktop.open(dirToOpen);
        } catch (IllegalArgumentException | IOException iae) {
        }
    }

    public void loadFileAgain() {
        file = null;
        TitleFileName.setText(null);
        dataDigital.setVisible(false);
        draganddropPane.setVisible(true);
        btnRaportPDF.setVisible(false);
        btnRaportPDFdnia.setVisible(false);
        btnAddStatsDigital.setVisible(false);
        btnAddStatsDigitalAll.setVisible(false);
        dragOver.setText("Wybierz plik albo upu???? go tutaj");
        loadAnotherFile.setVisible(false);
        btnOpenAnalogue.setDisable(false);
        firstTabPaneText.clear();
        secondTabPaneText.clear();
        thirdTabPaneText.clear();
        fourthTabPaneText.clear();
    }

    public void OpenExistsFiles() throws IOException {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("drivers.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
    }
}
