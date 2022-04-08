package com.example.tachographanalysis;


import com.example.tachographanalysis.size.SizeController;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;


public class DigitalAnalysisController implements Initializable {

    @FXML
    private Text TitleFileName,TextLoading,TextError;
    @FXML
    private BarChart barChart;
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
    private TextArea dailyTextAreaDataChart;
    @FXML
    private Button btnBack, btnUpload, btnRaport;
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
    List<String> lstFile;

    static String PDF =  "";
    static String dataT =  "";
    static String[] dataGD;
    static String dataPick;
    static String savedData = "";
    static String dataPick1;
    static String daily = "";
    static BaseFont helvetica;
    static String lastDayOfWork= "";
    static int counterEnter = 0;


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
        dragOver.setText("Upuść tutaj");
    }


    @FXML
    void onDragClickedButton(MouseEvent event) throws Exception {

        TextError.setText("");
        TextLoading.setText("Przetwarzanie...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("DDD Files", "*.ddd", "*.DDD", "*.xml"));
        File file = fileChooser.showOpenDialog(new Stage());
/*
        Thread.sleep(500);
*/

//        System.out.println(file);
        if(file == null) {
            TextLoading.setText("");
        }
        else {

            TitleFileName.setText("Dane z pliku "+file.getName());

            fileChooser.setTitle("Open Resource File");
            //

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
                                    logDataWrite.append(dtf.format(now) + " błąd plik " + fileNameDDD + " nie został poprawnie załadowany bądź jest uszkodzony\n");
                                    logDataWrite.close();
                                    f.delete();
                                    TextLoading.setText("Spróbuj ponownie");
                                    System.out.println("Błąd plik nie został poprawnie załadowany bądź jest uszkodzony");

                                } else {
                                    FileWriter logDataWrite = new FileWriter(".\\.log");
                                    logDataWrite.append(dtf.format(now) + " błąd plik '" + fileNameDDD + "'' nie został poprawnie załadowany bądź jest uszkodzony\n");
                                    logDataWrite.close();
                                    f.delete();
                                    TextLoading.setText("Spróbuj ponownie");
                                    System.out.println("Błąd plik nie został poprawnie załadowany bądź jest uszkodzony");
                                }
                            }

                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                } catch (IOException e) {

                    if (Files.exists(logFilePath)) {
                        FileWriter logDataWrite = new FileWriter(".\\.log", true);
                        logDataWrite.append(dtf.format(now) + " Błąd konwersja nie przebiegła pomyślnie\n");
                        logDataWrite.close();
                        System.out.println("Błąd konwersja nie przebiegła pomyślnie");
                        TextLoading.setText("Spróbuj ponownie");

                    } else {
                        FileWriter logDataWrite = new FileWriter(".\\.log");
                        logDataWrite.append(dtf.format(now) + "Błąd konwersja nie przebiegła pomyślnie\n");
                        logDataWrite.close();
                        System.out.println("Błąd konwersja nie przebiegła pomyślnie");
                        TextLoading.setText("Spróbuj ponownie");
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
                        logDataWrite.append(dtf.format(now) + " błąd plik " + fileNameXML + " nie został poprawnie załadowany bądź jest uszkodzony\n");
                        logDataWrite.close();
                        file.delete();
                        TextLoading.setText("Spróbuj ponownie");
                        System.out.println("Błąd plik XML nie został poprawnie załadowany bądź jest uszkodzony");
                    } else {
                        FileWriter logDataWrite = new FileWriter(".\\.log");
                        logDataWrite.append(dtf.format(now) + " błąd plik: '" + fileNameXML + "' nie został poprawnie załadowany bądź jest uszkodzony \n");
                        logDataWrite.close();
                        file.delete();
                        TextLoading.setText("Spróbuj ponownie");
                        System.out.println("Błąd plik XML nie został poprawnie załadowany bądź jest uszkodzony");
                    }
                }

            }
        }
    }

    @FXML
    private void  generatePDF2(){
        generatePDF(PDF);
    }
    @FXML
    private void  generatePDF3(){
        generatePDFdnia(PDF);
    }
    @FXML
    private void showData(String[] readedData){

        TextLoading.setText("");
        TextError.setText("");
        chart.getData().removeAll();
        chart.getData().clear();
        showWeaklyChart(readedData[1]+" \n\n d");
        try {
            colorPicker();
            dataGD=readedData;


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        btnRaportPDF.setVisible(true);
            tabPane.setVisible(true);
            if(btnRaportPDF.isPressed()) {
                generatePDF2();
            }
            if(btnRaportPDFdnia.isPressed()) {
                generatePDF3();
        }

        TextArea generalData = new TextArea("");
        one.setContent(generalData);
        TextArea textArea = (TextArea) one.getContent();
        textArea.appendText(readedData[0]);

        TextArea dailyData = new TextArea("");
        two.setContent(dailyData);
        //TextArea dailyDataDriver = (TextArea) two.getContent();
//        dailyDataDriver.appendText(readedData[1]);

        dataT=readedData[1]+" \n\n d";



        TextArea driverRoute = new TextArea("");
        three.setContent(driverRoute);
        TextArea driverRouteArea = (TextArea) three.getContent();
        driverRouteArea.appendText(readedData[2]);



        TextArea vehicleHistory = new TextArea("");
        four.setContent(vehicleHistory);
        TextArea vehicleHistoryData = (TextArea) four.getContent();
        vehicleHistoryData.appendText(readedData[3]);


        TextArea dataChartTwoWeekend = new TextArea("");
        five.setContent(dataChartTwoWeekend);
        TextArea dataChartTwoWeekendArea = (TextArea) five.getContent();
        dataChartTwoWeekendArea.setEditable(false);


    }

    @FXML
    private void  setDataPicker() {
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
          String inThisDayData = "";

          dataPick= datePickerTime;
          dataPick1= indexOfDataPickerTime;

        TextArea dailyData = new TextArea("");
        two.setContent(dailyData);
        TextArea dailyDataDriver = (TextArea) two.getContent();

//        Color color = (Color) dataPicker.getBackground().getFills().get(0).getFill();
//        if(true) {
//            dataPicker.setVisible(true);
//        }
        btnRaportPDFdnia.setVisible(true);

        if(indexOfDataPickerTime.equals("-1")) {
//            dailyDataDriver.appendText("Ten pracownik nie pracował tego dnia ");
        }
        else {

            dailyDataDriver.appendText("Dzienna Aktywność: ");
            int indeksString = parseInt(indexOfDataPickerTime);
            int i = 0;
            while (!String.valueOf(dataXml.charAt(indeksString + i)).equals("d")) {
                dailyDataDriver.appendText("" + dataXml.charAt(parseInt(indexOfDataPickerTime) + i));
                inThisDayData += String.valueOf(dataXml.charAt(parseInt(indexOfDataPickerTime) + i));
                i += 1;
            }


            showChart(inThisDayData);

        }



//        System.out.println(dataXml+"\n");
//        System.out.println(dataPicker.getValue());

//        String dataPickerValue = String.valueOf(dataPicker.getValue());
//        System.out.println(dataPickerValue);
//        dailyPaneDataChart.setVisible(true);
//        dailyTextAreaDataChart.appendText(dataPickerValue);

    }




    @FXML
    private void showChart(String data) throws ParseException {

        Object[] dataDiffOneDaTable = dataDiffOneDay(data);

        String selectedDate = data.substring(0,10);

        String[] activityDataWork = (String[]) dataDiffOneDaTable[0];
        String[] activityDataDrive = (String[]) dataDiffOneDaTable[1];
        String[] activityDataBreak = (String[]) dataDiffOneDaTable[2];




//        if(!savedData.contains(selectedDate)) {
            barChart.getData().clear();
            barChart.getData().removeAll();
            counterEnter++;

            //Barchart dzialający
            barChart.setVisible(true);

            barChart.setTitle("Aktywność pracownka ");
            barChart.getXAxis().setLabel("Aktywność");
            barChart.getYAxis().setLabel("Godziny");

            barChart.setAnimated(false);

            XYChart.Series series1 = new XYChart.Series();
            series1.setName(selectedDate);
            series1.getData().add(new XYChart.Data("Praca", parseInt(String.valueOf(timeDiffrence(activityDataWork))) / 60));
            series1.getData().add(new XYChart.Data("Jazda", parseInt(String.valueOf(timeDiffrence(activityDataDrive))) / 60));
            series1.getData().add(new XYChart.Data("Przerwa", parseInt(String.valueOf(timeDiffrence(activityDataBreak))) / 60));
            XYChart.Series series2 = new XYChart.Series();

            barChart.getData().addAll(series1);
//        }
        savedData += selectedDate;
    }

    // Last two weeks driver data
    @FXML
    private void showWeaklyChart(String readedData) {

        int lastIndexOfData = readedData.length();
        String splitedData[] = new String[lastIndexOfData];
        String newSplitedData[] = new String[lastIndexOfData];
        String twoWeeksData[] = new String[14];
        //          System.out.println(readedData);
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
//            if (counter14days == 13) {
//                while (indeksString + i != readedData.length()) {
//                    inThisDayData[counter14days] += String.valueOf(readedData.charAt(parseInt(indexOfDataPickerTime[counter14days]) + i));
//                    i += 1;
//                    if(indeksString +i+1 == readedData.length()){
//                        inThisDayData[counter14days] += (" ");
//                    }
//                }
//                counter14days++;
//            } else {

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
            if( inThisDayData[i].startsWith("null")) {
                lastIndexOfTheDay = inThisDayData[i].length();
                inThisDayDataCorrectly[i] = inThisDayData[i].substring(4, lastIndexOfTheDay);
            }
            else{
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

            activityDataWorkObject[i] =  activityDataWork;
            activityDataDriveObject[i] = activityDataDrive;
            activityDataBreakObject[i] = activityDataBreak;

        }


//        readedData.
        //for przypisująca rekurencyjnie do nowej tablicy znaki


        chart.setTitle("Dwutygodniowa aktywność pracownka ");
        chart.getXAxis().setLabel("Aktywność");
        chart.getYAxis().setLabel("Godziny");

        chart.setAnimated(false);

        XYChart.Series seriesChart1 = new XYChart.Series();
        seriesChart1.setName("Praca");
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[0], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[0]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[1], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[1]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[2], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[2]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[3], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[3]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[4], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[4]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[5], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[5]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[6], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[6]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[7], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[7]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[8], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[8]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[9], parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[9]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[10],parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[10]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[11],parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[11]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[12],parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[12]))) / 60));
        seriesChart1.getData().add(new XYChart.Data(twoWeeksDataCorrectly[13],parseInt(String.valueOf(timeDiffrence((String[]) activityDataWorkObject[13]))) / 60));

        XYChart.Series seriesChart2 = new XYChart.Series();
        seriesChart2.setName("Przerwa");
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[0], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[0]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[1], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[1]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[2], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[2]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[3], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[3]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[4], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[4]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[5], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[5]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[6], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[6]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[7], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[7]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[8], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[8]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[9], parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[9]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[10],parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[10]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[11],parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[11]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[12],parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[12]))) / 60));
        seriesChart2.getData().add(new XYChart.Data(twoWeeksDataCorrectly[13],parseInt(String.valueOf(timeDiffrence((String[]) activityDataBreakObject[13]))) / 60));

        XYChart.Series seriesChart3 = new XYChart.Series();
        seriesChart3.setName("Jazda");
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[0], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[0]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[1], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[1]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[2], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[2]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[3], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[3]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[4], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[4]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[5], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[5]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[6], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[6]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[7], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[7]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[8], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[8]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[9], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[9]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[10], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[10]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[11], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[11]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[12], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[12]))) / 60));
        seriesChart3.getData().add(new XYChart.Data(twoWeeksDataCorrectly[13], parseInt(String.valueOf(timeDiffrence((String[]) activityDataDriveObject[13]))) / 60));


        chart.getData().addAll(seriesChart1, seriesChart3, seriesChart2);


    }

    @FXML
    private Object[] dataDiffOneDay(String data){

        int firstActivity = data.indexOf("Aktywność");
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
                    activityDataBreak[count2] = split[i + 2] + split[i + 7];
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


//        String[] timeDiffOneDayTable = new String[3];
        Object[] timeDiffOneDayTable = new Object[3];

        timeDiffOneDayTable[0]= activityDataWork;
        timeDiffOneDayTable[1]= activityDataDrive;
        timeDiffOneDayTable[2]= activityDataBreak;


//        System.out.println(timeDiffrence(activityDataWork));
//        System.out.println(timeDiffrence(activityDataDrive));
//        System.out.println(timeDiffrence(activityDataBreak));
        return timeDiffOneDayTable;


    }

    @FXML
    public int timeDiffrence(String[] activity){

        int sumActivityDataBreakM= 0;
        String[] dateBreakHM = new String[8];
        String start = "";
        String stop = "";

        for (int i = 1 ; i<activity.length ; i++){

            dateBreakHM[0]= String.valueOf(activity[i].charAt(0));
            dateBreakHM[1]= String.valueOf(activity[i].charAt(1));
            dateBreakHM[2]= String.valueOf(activity[i].charAt(3));
            dateBreakHM[3]= String.valueOf(activity[i].charAt(4));
            dateBreakHM[4]= String.valueOf(activity[i].charAt(6));
            dateBreakHM[5]= String.valueOf(activity[i].charAt(7));
            dateBreakHM[6]= String.valueOf(activity[i].charAt(9));
            dateBreakHM[7]= String.valueOf(activity[i].charAt(10));

            start =  dateBreakHM[0]+dateBreakHM[1]+":"+dateBreakHM[2]+dateBreakHM[3];
            stop = dateBreakHM[4]+dateBreakHM[5]+":"+dateBreakHM[6]+dateBreakHM[7];
            SimpleDateFormat  format = new SimpleDateFormat("HH:mm");
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
            long diffrence = date2.getTime()-date1.getTime();

            sumActivityDataBreakM+=diffrence/1000/60;

        }
        return sumActivityDataBreakM;
    }
    @FXML
    private void visibilityDataPickerLeave(){

        if(dataT.length()!=0 ) {

                if(dataPicker!=null) {
                    dataPicker.setVisible(false);
                }
                if(btnRaportPDFdnia!=null) {
                    btnRaportPDFdnia.setVisible(false);
                }
                if(chart!=null) {
                    chart.setVisible(false);
                }
                if(barChart!=null) {
                    barChart.setVisible(false);
                }
                savedData = "";



        }

    }
    @FXML
    private void visibilityDataPickerEnter(){

        dataPicker.setVisible(true);
        barChart.setVisible(false);
        barChart.getData().clear();
        dataPicker.getEditor().clear();
        chart.setVisible(false);

        int lastZLatter = dataT.lastIndexOf("Z");
        lastDayOfWork = String.valueOf(dataPicker.getValue());
        int lastDataIndex = lastZLatter-19;
        String lastaDataString = (dataT.substring(lastDataIndex,lastDataIndex+10));

        String year = (lastaDataString.substring(0,4));
        String month = (lastaDataString.substring(5,7));
        String day = (lastaDataString.substring(8,10));

        int parseDay = parseInt(day);
        int parseMonth = parseInt(month);
        int parseYear = parseInt(year);
//        dataPicker.setValue(null);
        if((parseMonth==4 || parseMonth==6 || parseMonth==9 || parseMonth==11 )&& parseDay==30 && parseMonth!=12) {
            dataPicker.setValue(LocalDate.of(parseYear,parseMonth+1,1));
        }else if (parseMonth==2 && parseDay == 28){
            dataPicker.setValue(LocalDate.of(parseYear,parseMonth+1,1));
        }else if (parseMonth==12 && parseDay == 31 ){
            dataPicker.setValue(LocalDate.of(parseYear+1,1,1));
        }else if ((parseMonth==1 || parseMonth==3 || parseMonth==5 || parseMonth==7 || parseMonth==8 || parseMonth==10 || parseMonth==12 )&& parseDay==31){
            dataPicker.setValue(LocalDate.of(parseYear,parseMonth+1,1));
        }
        else{
            dataPicker.setValue(LocalDate.of(parseYear,parseMonth,parseDay+1));
        }



    }
    @FXML
    private void visiblityChartArea(){
        chart.setVisible(true);

    }

    @FXML
    private void btnRaportPDFdnia(){
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
                        }
                        else{
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

                    generalDataS +=(eElement.getElementsByTagName("CardHolderFirstNames").
                            item(0).getTextContent() + ", ");
                    generalDataS +=(eElement.getElementsByTagName("CardHolderPreferredLanguage").
                            item(0).getTextContent() + "\n\n");
                    // CardExtendedSerialNumber

                    // dodać serial number / data / rfu/  (jak wyciąga się value xml elementu month year itp..)
                    generalDataS +=("Identyfikacja karty ICC:  \n");
                    generalDataS +=("\t ClockStop: " + eElement.getElementsByTagName("ClockStop").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t CardExtendedSerialNumber: " + eElement.getElementsByTagName("CardExtendedSerialNumber").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t Numer zatwierdzenia karty: " + eElement.getElementsByTagName("CardApprovalNumber").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t CardPersonaliserId: " + eElement.getElementsByTagName("CardPersonaliserId").
                            item(0).getTextContent() + "\n");
                    //EmbedderIcAssemblerId
                    generalDataS +=("\t EmbedderIcAssemblerId: " + "\n");
                    generalDataS +=("\t\t -CountryCode: " + eElement.getElementsByTagName("CountryCode").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t -ModuleEmbedder: " + eElement.getElementsByTagName("CountryCode").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t -ManufacturerInformation: " + eElement.getElementsByTagName("ManufacturerInformation").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t IcIdentifier: " + eElement.getElementsByTagName("IcIdentifier").
                            item(0).getTextContent() + "\n");
                    //CardChipIdentyfiaction
                    generalDataS +=("CardChipIdentification: " + eElement.getElementsByTagName("CardChipIdentification").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS +=("\t IcSerialNumber: " + eElement.getElementsByTagName("IcSerialNumber").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS +=("\t IcManufacturingReferences: " + eElement.getElementsByTagName("IcManufacturingReferences").
                            item(0).getTextContent() + "\n");
                    //DriverCardApplicationIdentyfication
                    generalDataS +=(" DriverCardApplicationIdentification: " + "\n");
                    generalDataS +=("\t Type: " + eElement.getElementsByTagName("Type").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t Version: " + eElement.getElementsByTagName("Version").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t NoOfEventsPerType: " + eElement.getElementsByTagName("NoOfEventsPerType").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t NoOfFaultsPerType: " + eElement.getElementsByTagName("NoOfFaultsPerType").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t ActivityStructureLength: " + eElement.getElementsByTagName("ActivityStructureLength").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t NoOfCardVehicleRecords: " + eElement.getElementsByTagName("NoOfCardVehicleRecords").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t NoOfCardPlaceRecords: " + eElement.getElementsByTagName("NoOfCardPlaceRecords").
                            item(0).getTextContent() + "\n");
                    //CardCertificate
                    generalDataS +=(" CardCertificate: " + "\n");
                    //ext value
                    generalDataS +=("\t Signature: " + eElement.getElementsByTagName("Signature").
                            item(0).getTextContent() + "\n");
                    //ext value
                    generalDataS +=("\t PublicKeyRemainder: " + eElement.getElementsByTagName("PublicKeyRemainder").
                            item(0).getTextContent() + "\n");
                    //CertificationAuthorityReference
                    generalDataS +=("\t CertificationAuthorityReference: " + "\n");
                    generalDataS +=("\t\t Nation: " + eElement.getElementsByTagName("Nation").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t NationCode: " + eElement.getElementsByTagName("NationCode").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t SerialNumber: " + eElement.getElementsByTagName("SerialNumber").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t AdditionalInfo: " + eElement.getElementsByTagName("AdditionalInfo").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t CaIdentifier: " + eElement.getElementsByTagName("CaIdentifier").
                            item(0).getTextContent() + "\n");
                    //Identification
                    generalDataS +=(" Identification: " + "\n");
                    //CardIdentyfication
                    generalDataS +=("\t CardIdentification: " + "\n");
                    generalDataS +=("\t\t CardIssuingMemberState: " + eElement.getElementsByTagName("CardIssuingMemberState").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS +=("\t\t CardNumber: " + eElement.getElementsByTagName("CardNumber").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t CardIssuingAuthorityName: " + eElement.getElementsByTagName("CardIssuingAuthorityName").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS +=("\t\t CardIssueDate: " + eElement.getElementsByTagName("CardIssueDate").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS +=("\t\t CardValidityBegin: " + eElement.getElementsByTagName("CardValidityBegin").
                            item(0).getTextContent() + "\n");
                    // ext value
                    generalDataS +=("\t\t CardExpiryDate: " + eElement.getElementsByTagName("CardExpiryDate").
                            item(0).getTextContent() + "\n");
                    //DriverCardHolderIdentyfication
                    generalDataS +=("\t DriverCardHolderIdentification: " + eElement.getElementsByTagName("DriverCardHolderIdentification").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t CardHolderSurname: " + eElement.getElementsByTagName("CardHolderSurname").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t CardHolderFirstNames: " + eElement.getElementsByTagName("CardHolderFirstNames").
                            item(0).getTextContent() + "\n");
                    // ect value
                    generalDataS +=("\t\t CardHolderBirthDate: " + eElement.getElementsByTagName("CardHolderBirthDate").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t CardHolderPreferredLanguage: " + eElement.getElementsByTagName("CardHolderPreferredLanguage").
                            item(0).getTextContent() + "\n");
                    //CardDrivingLicenceInformation
                    generalDataS +=("\t CardDrivingLicenceInformation: " + eElement.getElementsByTagName("CardDrivingLicenceInformation").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t DrivingLicenceIssuingAuthority: " + eElement.getElementsByTagName("DrivingLicenceIssuingAuthority").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t DrivingLicenceIssuingNation: " + eElement.getElementsByTagName("DrivingLicenceIssuingNation").
                            item(0).getTextContent() + "\n");
                    generalDataS +=("\t\t DrivingLicenceNumber: " + eElement.getElementsByTagName("DrivingLicenceNumber").
                            item(0).getTextContent() + "\n");




                    // Aktywnośc dzienna kierowcy
                    int k = 0;
                    NodeList CardActivityDailyRecord = doc.getElementsByTagName("CardActivityDailyRecord");
                    NodeList ActivityChangeInfo = doc.getElementsByTagName("ActivityChangeInfo");
                    for(int i=0;i<CardActivityDailyRecord.getLength();i++){
        NodeList t=CardActivityDailyRecord.item(i).getChildNodes();
                        dailyActivityS +=(" \n\n data aktywności: " + CardActivityDailyRecord.item(i).
                                getAttributes().item(1).getNodeValue() + " \n");
                        dailyActivityS +=(" Dystans : " + CardActivityDailyRecord.item(i).
                                getAttributes().item(2).getNodeValue() + " km \n");
                        dailyActivityS +=(" Dzień pracy: " + CardActivityDailyRecord.item(i).
                                getAttributes().item(0).getNodeValue() + " \n\n");
                        for (int j=0;j<t.getLength();j++){
                            if(t.item(j).getAttributes()!=null) {
                                dailyActivityS += (" \t Aktywność: " + t.item(j).getAttributes().getNamedItem("Activity"));
                                dailyActivityS += (" Czas: " + t.item(j).getAttributes().getNamedItem("Time") + "\n");
                            }

                        }
                    }

                    // Trasa kierowcy
                    NodeList elPlaceRecord = doc.getElementsByTagName("PlaceRecord");

                    NodeList EntryTime = doc.getElementsByTagName("EntryTime");
                    NodeList DailyWorkPeriodCountry = doc.getElementsByTagName("DailyWorkPeriodCountry"); //atrib

                    driverRouteS +=("\n Trasa kierowcy: \n\n");
                    for (int i = 0; i < elPlaceRecord.getLength(); i++) {
                        driverRouteS +=("\t Kraj: " + DailyWorkPeriodCountry.item(i).getAttributes().item(0).getNodeValue() + " ");
                        driverRouteS +=(" Data i godzina: " + EntryTime.item(i).getAttributes().item(0).getNodeValue());
                        driverRouteS +=("  Przebieg: " + eElement.getElementsByTagName("VehicleOdometerValue").item(i).getTextContent() + " km \n");
                    }



                    //Przebieg
                    NodeList elCardVehicleRecord = doc.getElementsByTagName("CardVehicleRecord");

                    NodeList VehicleFirstUse = doc.getElementsByTagName("VehicleFirstUse");
                    NodeList VehicleLastUse = doc.getElementsByTagName("VehicleLastUse"); //atrib

                    mileageCarS +=("\n Dane pojazdu: \n\n");
                    for (int i = 0; i < elCardVehicleRecord.getLength(); i++) {
                        mileageCarS +=("\t Przebieg startowy: " + eElement.getElementsByTagName("VehicleOdometerBegin").item(i).getTextContent() + " km, ");
                        mileageCarS +=(" przebieg końcowy: " + eElement.getElementsByTagName("VehicleOdometerEnd").item(i).getTextContent() + " km, ");
                        mileageCarS +=(" od: " + VehicleFirstUse.item(i).getAttributes().item(0).getNodeValue() + ", ");
                        mileageCarS +=(" do: " + VehicleLastUse.item(i).getAttributes().item(0).getNodeValue() + ", ");
                        mileageCarS +=(" Numer rejestracyjny: " + elElement.getElementsByTagName("VehicleRegistrationNumber").item(i).getTextContent() + " \n");
                    }

                }
            }
//            progressBar.progressProperty().bind(progressBar.progressProperty());
            dailyActivityS=dailyActivityS.replace("Time=","");
            dailyActivityS=dailyActivityS.replace("\"","");
            dailyActivityS=dailyActivityS.replace("Activity=","");
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


//        dragOver.setText("Czytanie danych");

        return xmlDate;
    }




    @FXML
    private void handleDroppedButton(DragEvent event) throws FileNotFoundException {
        List<File> files = event.getDragboard().getFiles();
        List<String> validExtensions = Arrays.asList("ddd", "DDD", "xml");
        file = new File(String.valueOf(new Stage()));
        //image = new Image(new FileInputStream(files.get(0))); //Drag&Drop IMG
        //File file = fileChooser.showOpenDialog(new Stage());  //Chooser DIGI

        if (!validExtensions.containsAll(event.getDragboard()
                .getFiles().stream().map(file -> getExtension(file.getName()))
                .collect(Collectors.toList()))) {
            dragOver.setText("To nie jest plik .ddd");
        } else {
            try {
               // Scanner scanner = new Scanner(files.get(0));
            //System.out.println(file);
                //while (scanner.hasNextLine()) {
                   File filepath = files.get(0);
                   //Files.write(filepath);
                    System.out.println(filepath);


//                    textArea.appendText(scanner.nextLine() + "\n");
                    dragOver.setText("Poprawnie załadowano plik");

                if(file == null)
                {

                }
                else {

                    //

                    InputStream inputStream = new FileInputStream(filepath);
                    Thread.sleep(500);

                    String xmlExtCheck = (filepath.getName().substring(filepath.getName().length() - 4));
                    String xml = ".xml";

                    String fileNameXML = filepath.getName().subSequence(0, filepath.getName().length() - 4) + ".xml";
                    String fileNameDDD = filepath.getName().subSequence(0, filepath.getName().length() - 4) + ".ddd";

                    System.out.println(filepath);
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
                                        f.deleteOnExit();
                                        String[] readedData = readData(filexml);
                                        showData(readedData);

                                    } else {

                                        if (Files.exists(logFilePath)) {
                                            FileWriter logDataWrite = new FileWriter(".\\.log", true);
                                            logDataWrite.append(dtf.format(now) + " błąd plik " + fileNameDDD + " nie został poprawnie załadowany bądź jest uszkodzony\n");
                                            logDataWrite.close();
                                            f.delete();
                                            System.out.println("Błąd plik nie został poprawnie załadowany bądź jest uszkodzony");

                                        } else {
                                            FileWriter logDataWrite = new FileWriter(".\\.log");
                                            logDataWrite.append(dtf.format(now) + " błąd plik '" + fileNameDDD + "'' nie został poprawnie załadowany bądź jest uszkodzony\n");
                                            logDataWrite.close();
                                            f.delete();
                                            System.out.println("Błąd plik nie został poprawnie załadowany bądź jest uszkodzony");
                                        }
                                    }

                                } catch (InterruptedException ex) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        } catch (IOException e) {

                            if (Files.exists(logFilePath)) {
                                FileWriter logDataWrite = new FileWriter(".\\.log", true);
                                logDataWrite.append(dtf.format(now) + " Błąd konwersja nie przebiegła pomyślnie\n");
                                logDataWrite.close();
                                System.out.println("Błąd konwersja nie przebiegła pomyślnie");

                            } else {
                                FileWriter logDataWrite = new FileWriter(".\\.log");
                                logDataWrite.append(dtf.format(now) + "Błąd konwersja nie przebiegła pomyślnie\n");
                                logDataWrite.close();
                                System.out.println("Błąd konwersja nie przebiegła pomyślnie");
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
                                logDataWrite.append(dtf.format(now) + " błąd plik " + fileNameXML + " nie został poprawnie załadowany bądź jest uszkodzony\n");
                                logDataWrite.close();
                                filepath.delete();
                                System.out.println("Błąd plik XML nie został poprawnie załadowany bądź jest uszkodzony");
                            } else {
                                FileWriter logDataWrite = new FileWriter(".\\.log");
                                logDataWrite.append(dtf.format(now) + " błąd plik: '" + fileNameXML + "' nie został poprawnie załadowany bądź jest uszkodzony \n");
                                logDataWrite.close();
                                filepath.delete();
                                System.out.println("Błąd plik XML nie został poprawnie załadowany bądź jest uszkodzony");
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

    public void generatePDF(String PDF_) {
//created PDF document instance
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        PdfWriter writer;
        try {
//generate a PDF at the specified location
            File dir = new File(".\\PDF\\");
            if (!dir.exists()) {
                dir.mkdirs();
            }
//            String fname = null;
//            File file = null;
//
//            System.out.println("Please choose file name:");
//            while (true) {
//                try (Scanner in = new Scanner(System.in)) {
//                    // Reads a single line from the console
//                    fname = in.nextLine();
//                    file = new File(fname);
//                    if (!file.createNewFile()) {
//                        throw new RuntimeException("File already exist");
//                    }
//                    break;
//                } catch (Exception ex) {
//                    System.out.println(ex.getMessage() + ", please try again:");
//                }
//            }
//
//            return file;

//            FileChooser fileChooser = new FileChooser();
//            File file = fileChooser.showOpenDialog(new Stage());
            //Tworzenie pliku PDF
            //String PDF1 = PDF.substring(25, PDF.length() - 4);
            File PDF2 = new File(new File(PDF_).getName()); // nazwa pliku

            writer = PdfWriter.getInstance(doc, new FileOutputStream(".\\PDF\\" + PDF2.getName().subSequence(0, PDF2.getName().length() - 8) + ".pdf"));
            System.out.println("Tworzenie pliku PDF powiodło się.");
//Otwieranie pliku PDF
            doc.open();
//Dodwawanie paragrafów do pliku PDF
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc1 = db.parse(PDF_);
                doc1.getDocumentElement().normalize();

                        doc.add(new Paragraph(dataGD[0]));
                        doc.add(new Paragraph(dataGD[1]));
                        doc.add(new Paragraph(dataGD[2]));
                        doc.add(new Paragraph(dataGD[3]));







//                    for (int itr = 0; itr < nodeList.getLength(); itr++) {
//                        Node node = nodeList.item(itr);
//                        if (node.getNodeType() == Node.ELEMENT_NODE) {
//
//                        }


                //CardFaultRecords
//                }
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            }
//close the PDF file
            doc.close();
//closes the writer
            writer.close();
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void generatePDFdnia(String PDF_) {
//created PDF document instance
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();

        PdfWriter writer;

        try {
//generate a PDF at the specified location
            File dir = new File(".\\PDF\\");
            if (!dir.exists()) {
                dir.mkdirs();
            }
//            String fname = null;
//            File file = null;
//
//            System.out.println("Please choose file name:");
//            while (true) {
//                try (Scanner in = new Scanner(System.in)) {
//                    // Reads a single line from the console
//                    fname = in.nextLine();
//                    file = new File(fname);
//                    if (!file.createNewFile()) {
//                        throw new RuntimeException("File already exist");
//                    }
//                    break;
//                } catch (Exception ex) {
//                    System.out.println(ex.getMessage() + ", please try again:");
//                }
//            }
//
//            return file;

//            FileChooser fileChooser = new FileChooser();
//            File file = fileChooser.showOpenDialog(new Stage());
            //Tworzenie pliku PDF
            //String PDF1 = PDF.substring(25, PDF.length() - 4);
            File PDF2 = new File(new File(PDF_).getName());
            // nazwa pliku
            writer = PdfWriter.getInstance(doc, new FileOutputStream(".\\PDF\\" + PDF2.getName().subSequence(0, PDF2.getName().length() - 8) + dataPick + ".pdf"));
            System.out.println("Tworzenie pliku PDF powiodło się.");
//Otwieranie pliku PDF
            doc.open();
//Dodwawanie paragrafów do pliku PDF
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc1 = db.parse(PDF_);
                doc1.getDocumentElement().normalize();
                helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
                com.itextpdf.text.Font polskieFonty=new com.itextpdf.text.Font(helvetica,10);

                //doc.add(new Paragraph(dataGD[1]));
//                doc.add(new Paragraph(dataGD[1]));
//                doc.add(new Paragraph(dataGD[2]));
//                doc.add(new Paragraph(dataGD[3]));

                if(dataPick1.equals("-1")) {
                    doc.add(new Paragraph("Pracownik nie pracował w tym dniu."));
                }
                else {
                    int indeksString = parseInt(dataPick1);
                    int i = 0;
                    while (!String.valueOf(dataT.charAt(indeksString + i)).equals("d")) {
                        daily += (dataT.charAt(parseInt(dataPick1) + i));
                        //doc.add(new Paragraph("" + dataT.charAt(parseInt(dataPick1) + i)));
                        i += 1;
                        //doc.add(new Paragraph(""+daily));
                    }
                    doc.add(new Paragraph(daily,polskieFonty));
                }







//                    for (int itr = 0; itr < nodeList.getLength(); itr++) {
//                        Node node = nodeList.item(itr);
//                        if (node.getNodeType() == Node.ELEMENT_NODE) {
//
//                        }


                //CardFaultRecords
//                }
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            }
//close the PDF file
            doc.close();
//closes the writer
            writer.close();
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }






}
