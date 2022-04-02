package com.example.tachographanalysis;


import com.example.tachographanalysis.size.SizeController;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.BaseFont;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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
    private BarChart barChart;
    @FXML
    private AreaChart chart;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private DatePicker dataPicker;
    @FXML
    private Tab one, two, three, four;
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

    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml")));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
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


        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("DDD Files", "*.ddd", "*.DDD", "*.xml"));
        File file = fileChooser.showOpenDialog(new Stage());
        System.out.println(file);
        if(file == null)
        {

        }
        else {
            fileChooser.setTitle("Open Resource File");
            //

            InputStream inputStream = new FileInputStream(file);
            Thread.sleep(500);

            String xmlExtCheck = (file.getName().substring(file.getName().length() - 4));
            String xml = ".xml";

            String fileNameXML = file.getName().subSequence(0, file.getName().length() - 4) + ".xml";
            String fileNameDDD = file.getName().subSequence(0, file.getName().length() - 4) + ".ddd";

            System.out.println(file);
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
                        System.out.println("Błąd plik XML nie został poprawnie załadowany bądź jest uszkodzony");
                    } else {
                        FileWriter logDataWrite = new FileWriter(".\\.log");
                        logDataWrite.append(dtf.format(now) + " błąd plik: '" + fileNameXML + "' nie został poprawnie załadowany bądź jest uszkodzony \n");
                        logDataWrite.close();
                        file.delete();
                        System.out.println("Błąd plik XML nie został poprawnie załadowany bądź jest uszkodzony");
                    }
                }

            }
        }
    }
//    @FXML
//    private void handleDroppedButton(DragEvent event) throws IOException {
//        List<File> files = event.getDragboard().getFiles();
//        List<String> validExtensions = Arrays.asList("ddd", "DDD","xml");
//        file1 = new File(String.valueOf(new FileInputStream(files.get(0))));
//        if(!validExtensions.containsAll(event.getDragboard()
//                .getFiles().stream().map(file -> getExtension(file.getName()))
//                .collect(Collectors.toList()))) {
//            dragOver.setText("To nie jest odpowiedni plik.");
//
//            File file = fileChooser.showOpenDialog(new Stage());
//
//        } else {
//            try {
//                Scanner scanner = new Scanner(files.get(0));
//           System.out.println(file1);
//                while (scanner.hasNextLine()) {
//                    File filepath = files.get(0);
////                    Files.write(filepath, )
//
//
////                    textArea.appendText(scanner.nextLine() + "\n");
//                    dragOver.setText("Poprawnie załadowano plik");
//
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//    }

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


//          progressBar.setVisible(true);
//          progressBar.setProgress(1.0);
//          dragOver.setVisible(false);
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

        dataT=readedData[1];



        TextArea driverRoute = new TextArea("");
        three.setContent(driverRoute);
        TextArea driverRouteArea = (TextArea) three.getContent();
        driverRouteArea.appendText(readedData[2]);



        TextArea vehicleHistory = new TextArea("");
        four.setContent(vehicleHistory);
        TextArea vehicleHistoryData = (TextArea) four.getContent();
        vehicleHistoryData.appendText(readedData[3]);


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
        dataPicker.setVisible(true);
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

        int firstActivity = data.indexOf("Aktywność");
        int bTime = 0;

        String activity[] = new String[3];

        activity[0] = "Break";
        activity[1] = "Driving";
        activity[2] = "Work";


        System.out.println(data);

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

        for (int i = 1; i < activityDataBreak.length; i++) {
            System.out.println("break" + activityDataBreak[i]);
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

        for (int i = 1; i < activityDataDrive.length; i++) {
            System.out.println("drive" + activityDataDrive[i]);
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

        System.out.println(timeDiffrence(activityDataWork));
        System.out.println(timeDiffrence(activityDataDrive));
        System.out.println(timeDiffrence(activityDataBreak));


        String selectedDate = data.substring(0,10);





        if(!savedData.contains(selectedDate)) {



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
        }
        savedData+=selectedDate;

//        barChart.getData().addAll(series1,series2,series3);

//        chart.setVisible(true);

//        chart.setTitle("Aktywność pracownka ");
//        chart.getXAxis().setLabel("Minuty");
//        chart.getYAxis().setLabel("Aktywność");
//
//        int workM =  parseInt(String.valueOf(timeDiffrence(activityDataWork)));
//        int driveM =  parseInt(String.valueOf(timeDiffrence(activityDataDrive)));
//        int breakM = parseInt(String.valueOf(timeDiffrence(activityDataBreak)));
//
//        chart.setAnimated(false);
//
//        XYChart.Series series1 = new XYChart.Series();
//        series1.setName("Praca");
//        series1.getData().add(new XYChart.Data( 0,10));
//        series1.getData().add(new XYChart.Data( workM,10));
//
//        XYChart.Series series2 = new XYChart.Series();
//        series2.setName("Jazda");
//        series2.getData().add(new XYChart.Data(0,10));
//        series2.getData().add(new XYChart.Data(driveM,10));
//
//        XYChart.Series series3 = new XYChart.Series();
//        series3.setName("Przerwa");
//        series3.getData().add(new XYChart.Data(0,10 ));
//        series3.getData().add(new XYChart.Data(breakM,10 ));
//
//
////        chart.getData().addAll(series1);
//
//        chart.getData().addAll(series1,series2,series3);
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

        if(dataT.length()!=0) {
            dataPicker.setVisible(false);
            btnRaportPDFdnia.setVisible(false);
            chart.setVisible(false);
            barChart.setVisible(false);
            savedData="";


        }

    }
    @FXML
    private void visibilityDataPickerEnter(){
        dataPicker.setVisible(true);
        barChart.setVisible(false);
        barChart.getData().clear();
        dataPicker.getEditor().clear();
        dataPicker.setValue(null);

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
//                        System.out.println(CardActivityDailyRecord.item(i).getAttributes().getNamedItem("DateTime"));
                        dailyActivityS +=(" \n\n data aktywności: " + CardActivityDailyRecord.item(i).
                                getAttributes().item(1).getNodeValue() + " \n");
                        dailyActivityS +=(" Dystans : " + CardActivityDailyRecord.item(i).
                                getAttributes().item(2).getNodeValue() + " km \n");
                        dailyActivityS +=(" Dzień pracy: " + CardActivityDailyRecord.item(i).
                                getAttributes().item(0).getNodeValue() + " \n\n");
                        for (int j=0;j<t.getLength();j++){
//                            System.out.println(t.item(j).getAttributes().getNamedItem("Time"));
//                            System.out.println(t.item(j).getAttributes());
                            if(t.item(j).getAttributes()!=null) {
                                dailyActivityS += (" \t Aktywność: " + t.item(j).getAttributes().getNamedItem("Activity"));
                                dailyActivityS += (" Czas: " + t.item(j).getAttributes().getNamedItem("Time") + "\n");
                            }
//
                        }
                    }
//                    System.out.println(dailyActivityS);
//                    for (int cout = 0; cout < CardActivityDailyRecord.getLength(); cout++) {
//
//
//                        dailyActivityS +=(" \n\n data aktywności: " + CardActivityDailyRecord.item(cout).
//                                getAttributes().item(1).getNodeValue() + " \n");
//
//                        dailyActivityS +=(" Dystans : " + CardActivityDailyRecord.item(cout).
//                                getAttributes().item(2).getNodeValue() + " km \n");
//                        dailyActivityS +=(" Dzień pracy: " + CardActivityDailyRecord.item(cout).
//                                getAttributes().item(0).getNodeValue() + " \n\n");
//                        // ilosc daily rekordy w danym dniu
//                        int itemsInCardActiveDailyRecord = CardActivityDailyRecord.item(cout).getChildNodes().getLength();
//                        // gdy
//                        int j = 0;
//
//                        while (j < itemsInCardActiveDailyRecord) {
//
////                                textArea.appendText(" \t FileOffset: "+ActivityChangeInfo.item(j+k).getAttributes().item(1).getNodeValue() + "\n");
////                                System.out.print(ActivityChangeInfo.item(j+k).getAttributes().item(3).getNodeValue() + "\n");
////                                System.out.print(ActivityChangeInfo.item(j+k).getAttributes().item(4).getNodeValue() + "\n");
////                                textArea.appendText(" \t Inserted: "+ActivityChangeInfo.item(j+k).getAttributes().item(2).getNodeValue() + "\n");
//                            dailyActivityS +=(" \t Aktywność: " + ActivityChangeInfo.item(j ).getAttributes().item(0).getNodeValue());
//                            dailyActivityS +=(" Czas: " + ActivityChangeInfo.item(j ).getAttributes().item(5).getNodeValue() + "\n");
//                            j++;
//                        }
//                        k += itemsInCardActiveDailyRecord;
//                    }

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
