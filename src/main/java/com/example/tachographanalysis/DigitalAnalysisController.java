package com.example.tachographanalysis;


import com.example.tachographanalysis.size.SizeController;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;


public class DigitalAnalysisController implements Initializable {

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
    private String DDDFile;
    @FXML
    private Button btnRaportPDF;
    List<String> lstFile;

    static String PDF =  "";
    static String dataT =  "";

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


                String filexmlSize = String.valueOf(file);
                long bytesXML = Files.size(Path.of(filexmlSize));
                long kiloBytesXML = bytesXML / 1024;

                if (kiloBytesXML > 1) {
                    System.out.println("Poprawnie zaimportowano plik .xml");
                    readData(file);
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

    @FXML
    private void  generatePDF2(){
        generatePDF(PDF);
    }
    @FXML
    private void showData(String[] readedData){


//          progressBar.setVisible(true);
//          progressBar.setProgress(1.0);
//          dragOver.setVisible(false);
        try {
            colorPicker();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        btnRaportPDF.setVisible(true);

            tabPane.setVisible(true);
            if(btnRaportPDF.isPressed()) {
                generatePDF2();
            }

        TextArea generalData = new TextArea("");
        one.setContent(generalData);
        TextArea textArea = (TextArea) one.getContent();
        textArea.appendText(readedData[0]);

        TextArea dailyData = new TextArea("");
        two.setContent(dailyData);
        TextArea dailyDataDriver = (TextArea) two.getContent();
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
        setDataPicker2(dataT);
    }

    @FXML
    private void setDataPicker2(String dataXml){

          String datePickerTime = String.valueOf(dataPicker.getValue());
          String indexOfDataPickerTime = String.valueOf(dataXml.indexOf(datePickerTime));

          TextArea dailyData = new TextArea("");
          two.setContent(dailyData);
          TextArea dailyDataDriver = (TextArea) two.getContent();
          dataPicker.setVisible(true);

        if(indexOfDataPickerTime.equals("-1")) {
            dailyDataDriver.appendText("Ten pracownik nie pracował tego dnia ");
        }
        else {

            dailyDataDriver.appendText("Dzienna Aktywność: ");
            int indeksString = parseInt(indexOfDataPickerTime);
            int i = 0;
            while (!String.valueOf(dataXml.charAt(indeksString + i)).equals("d")) {
                dailyDataDriver.appendText("" + dataXml.charAt(parseInt(indexOfDataPickerTime) + i));
                i += 1;
            }
        }



//        System.out.println(dataXml+"\n");
//        System.out.println(dataPicker.getValue());

//        String dataPickerValue = String.valueOf(dataPicker.getValue());
//        System.out.println(dataPickerValue);
//        dailyPaneDataChart.setVisible(true);
//        dailyTextAreaDataChart.appendText(dataPickerValue);

    }
    @FXML
    private void visibilityDataPickerLeave(){

        if(dataT.length()!=0) {
            dataPicker.setVisible(false);
        }

    }
    @FXML
    private void visibilityDataPickerEnter(){
        dataPicker.setVisible(true);
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

    for(int i=0;i<CardActivityDailyRecord.getLength();i++) {

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


        work.add(LocalDate.of(parseInt(year),parseInt(month),parseInt(day)));
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

    public String[] readData(File filexml) throws Exception {

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
                    for (int cout = 0; cout < CardActivityDailyRecord.getLength(); cout++) {


                        dailyActivityS +=(" \n\n data aktywności: " + CardActivityDailyRecord.item(cout).
                                getAttributes().item(1).getNodeValue() + " \n");

                        dailyActivityS +=(" Dystans : " + CardActivityDailyRecord.item(cout).
                                getAttributes().item(2).getNodeValue() + " km \n");
                        dailyActivityS +=(" Dzień pracy: " + CardActivityDailyRecord.item(cout).
                                getAttributes().item(0).getNodeValue() + " \n\n");
                        // ilosc daily rekordy w danym dniu
                        int itemsInCardActiveDailyRecord = CardActivityDailyRecord.item(cout).getChildNodes().getLength();
                        // gdy
                        int j = 0;
                        while (j < itemsInCardActiveDailyRecord) {

//                                textArea.appendText(" \t FileOffset: "+ActivityChangeInfo.item(j+k).getAttributes().item(1).getNodeValue() + "\n");
//                                System.out.print(ActivityChangeInfo.item(j+k).getAttributes().item(3).getNodeValue() + "\n");
//                                System.out.print(ActivityChangeInfo.item(j+k).getAttributes().item(4).getNodeValue() + "\n");
//                                textArea.appendText(" \t Inserted: "+ActivityChangeInfo.item(j+k).getAttributes().item(2).getNodeValue() + "\n");
                            dailyActivityS +=(" \t Aktywność: " + ActivityChangeInfo.item(j + k).getAttributes().item(0).getNodeValue());
                            dailyActivityS +=(" Czas: " + ActivityChangeInfo.item(j + k).getAttributes().item(5).getNodeValue() + "\n");
                            j++;
                        }
                        k += itemsInCardActiveDailyRecord;
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
        List<String> validExtensions = Arrays.asList("ddd", "DDD", "txt", "xml");
        file = new File(String.valueOf(new FileInputStream(files.get(0))));
        if (!validExtensions.containsAll(event.getDragboard()
                .getFiles().stream().map(file -> getExtension(file.getName()))
                .collect(Collectors.toList()))) {
            dragOver.setText("To nie jest plik .ddd");
        } else {
            try {
                Scanner scanner = new Scanner(files.get(0));
//            System.out.println(file);
                while (scanner.hasNextLine()) {
//                    File filepath = files.get(0);
//                    Files.write(filepath, )


//                    textArea.appendText(scanner.nextLine() + "\n");
                    dragOver.setText("Poprawnie załadowano plik");

                }
            } catch (FileNotFoundException e) {
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
                NodeList nodeList = doc1.getElementsByTagName("DriverData");
                NodeList CardVehicleRecord = doc1.getElementsByTagName("CardVehicleRecords");
                for (int itr = 0; itr < nodeList.getLength(); itr++) {
                    Node nodeVehic = CardVehicleRecord.item(itr);
                    Node node = nodeList.item(itr);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) node;
                        Element elElement = (Element) nodeVehic;

                        // dodać serial number / data / rfu/  (jak wyciąga się value xml elementu month year itp..)
                        doc.add(new Paragraph("\t ClockStop: " + eElement.getElementsByTagName("ClockStop").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t CardExtendedSerialNumber: " + eElement.getElementsByTagName("CardExtendedSerialNumber").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t Numer zatwierdzenia karty: " + eElement.getElementsByTagName("CardApprovalNumber").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t CardPersonaliserId: " + eElement.getElementsByTagName("CardPersonaliserId").
                                item(0).getTextContent() + "\n"));
                        //EmbedderIcAssemblerId
                        doc.add(new Paragraph("\t EmbedderIcAssemblerId: " + "\n"));
                        doc.add(new Paragraph("\t\t CountryCode: " + eElement.getElementsByTagName("CountryCode").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t ModuleEmbedder: " + eElement.getElementsByTagName("CountryCode").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t ManufacturerInformation: " + eElement.getElementsByTagName("ManufacturerInformation").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t IcIdentifier: " + eElement.getElementsByTagName("IcIdentifier").
                                item(0).getTextContent() + "\n"));
                        //CardChipIdentyfiaction
                        doc.add(new Paragraph("CardChipIdentification: " + eElement.getElementsByTagName("CardChipIdentification").
                                item(0).getTextContent() + "\n"));
                        // ext value
                        doc.add(new Paragraph("\t IcSerialNumber: " + eElement.getElementsByTagName("IcSerialNumber").
                                item(0).getTextContent() + "\n"));
                        // ext value
                        doc.add(new Paragraph("\t IcManufacturingReferences: " + eElement.getElementsByTagName("IcManufacturingReferences").
                                item(0).getTextContent() + "\n"));
                        //DriverCardApplicationIdentyfication
                        doc.add(new Paragraph(" DriverCardApplicationIdentification: " + "\n"));
                        doc.add(new Paragraph("\t Type: " + eElement.getElementsByTagName("Type").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t Version: " + eElement.getElementsByTagName("Version").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t NoOfEventsPerType: " + eElement.getElementsByTagName("NoOfEventsPerType").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t NoOfFaultsPerType: " + eElement.getElementsByTagName("NoOfFaultsPerType").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t ActivityStructureLength: " + eElement.getElementsByTagName("ActivityStructureLength").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t NoOfCardVehicleRecords: " + eElement.getElementsByTagName("NoOfCardVehicleRecords").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t NoOfCardPlaceRecords: " + eElement.getElementsByTagName("NoOfCardPlaceRecords").
                                item(0).getTextContent() + "\n"));
                        //CardCertificate
                        doc.add(new Paragraph(" CardCertificate: " + "\n"));
                        //ext value
                        doc.add(new Paragraph("\t Signature: " + eElement.getElementsByTagName("Signature").
                                item(0).getTextContent() + "\n"));
                        //ext value
                        doc.add(new Paragraph("\t PublicKeyRemainder: " + eElement.getElementsByTagName("PublicKeyRemainder").
                                item(0).getTextContent() + "\n"));
                        //CertificationAuthorityReference
                        doc.add(new Paragraph("\t CertificationAuthorityReference: " + "\n"));
                        doc.add(new Paragraph("\t\t Nation: " + eElement.getElementsByTagName("Nation").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t NationCode: " + eElement.getElementsByTagName("NationCode").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t SerialNumber: " + eElement.getElementsByTagName("SerialNumber").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t AdditionalInfo: " + eElement.getElementsByTagName("AdditionalInfo").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t CaIdentifier: " + eElement.getElementsByTagName("CaIdentifier").
                                item(0).getTextContent() + "\n"));
                        //Identification
                        doc.add(new Paragraph(" Identification: " + "\n"));
                        //CardIdentyfication
                        doc.add(new Paragraph("\t CardIdentification: " + "\n"));
                        doc.add(new Paragraph("\t\t CardIssuingMemberState: " + eElement.getElementsByTagName("CardIssuingMemberState").
                                item(0).getTextContent() + "\n"));
                        // ext value
                        doc.add(new Paragraph("\t\t CardNumber: " + eElement.getElementsByTagName("CardNumber").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t CardIssuingAuthorityName: " + eElement.getElementsByTagName("CardIssuingAuthorityName").
                                item(0).getTextContent() + "\n"));
                        // ext value
                        doc.add(new Paragraph("\t\t CardIssueDate: " + eElement.getElementsByTagName("CardIssueDate").
                                item(0).getTextContent() + "\n"));
                        // ext value
                        doc.add(new Paragraph("\t\t CardValidityBegin: " + eElement.getElementsByTagName("CardValidityBegin").
                                item(0).getTextContent() + "\n"));
                        // ext value
                        doc.add(new Paragraph("\t\t CardExpiryDate: " + eElement.getElementsByTagName("CardExpiryDate").
                                item(0).getTextContent() + "\n"));
                        //DriverCardHolderIdentyfication
                        doc.add(new Paragraph("\t DriverCardHolderIdentification: " + eElement.getElementsByTagName("DriverCardHolderIdentification").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t CardHolderSurname: " + eElement.getElementsByTagName("CardHolderSurname").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t CardHolderFirstNames: " + eElement.getElementsByTagName("CardHolderFirstNames").
                                item(0).getTextContent() + "\n"));
                        // ect value
                        doc.add(new Paragraph("\t\t CardHolderBirthDate: " + eElement.getElementsByTagName("CardHolderBirthDate").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t CardHolderPreferredLanguage: " + eElement.getElementsByTagName("CardHolderPreferredLanguage").
                                item(0).getTextContent() + "\n"));
                        //CardDrivingLicenceInformation
                        doc.add(new Paragraph("\t CardDrivingLicenceInformation: " + eElement.getElementsByTagName("CardDrivingLicenceInformation").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t DrivingLicenceIssuingAuthority: " + eElement.getElementsByTagName("DrivingLicenceIssuingAuthority").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t DrivingLicenceIssuingNation: " + eElement.getElementsByTagName("DrivingLicenceIssuingNation").
                                item(0).getTextContent() + "\n"));
                        doc.add(new Paragraph("\t\t DrivingLicenceNumber: " + eElement.getElementsByTagName("DrivingLicenceNumber").
                                item(0).getTextContent() + "\n"));

                    }
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
            doc.add(new Paragraph(PDF_));
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
