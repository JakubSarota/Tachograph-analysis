package com.example.tachographanalysis;

import com.example.tachographanalysis.size.SizeController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
import java.util.*;
import java.util.stream.Collectors;



public class DigitalAnalysisController implements Initializable {

    @FXML
    private TextArea textArea;
    @FXML
    private Button btnBack, btnUpload, btnRaport;
    @FXML
    public Button dragOver;
    @FXML
    private File file;
    @FXML
    private String DDDFile;
    List<String> lstFile;

//    public class LoadMethod {
//
//
//    public void stayInvisible() {
//        textArea.setVisible(false);
//    }
//
//    }


//    private DigitalAnalysisController readFile;

//    public void DigitalAnalysisController(DigitalAnalysisFileController readFile) {
//        this.readFile = readFile;
//    }


//    public File getFile(File filexml) {
//        return filexml;
//    }



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
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("DDD Files", "*.ddd", "*.DDD", "*.xml"));
        File file = fileChooser.showOpenDialog(new Stage());
        fileChooser.setTitle("Open Resource File");
        //
        InputStream inputStream = new FileInputStream(file);
//      String pathxml = file.getParent()+"\\"+file.getName().subSequence(0,file.getName().length()-4)+".DDD";
//        System.out.println(file);

        String xmlExtCheck = new String(file.getName().substring(file.getName().length() - 4));
        String xml = new String(".xml");
        System.out.println(file);

        if (!xmlExtCheck.equals(xml)) {
            System.out.println("To nie jest plik .xml");
            try {
                File dir = new File(".\\ddd_to_xml\\data\\driver\\");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                else{
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
                    if (filexml.exists()) {
                        System.out.println("Plik xml został stworzony");
                        readData(filexml);
                    } else {
                        textArea.appendText("Błąd plik nie istnieje");
                    }


                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            System.out.println("To jest plik.xml");
            readData(file);

            }
    }

    private void readData(File filexml) {


        if(filexml.exists()) {
            dragOver.setVisible(false);
            textArea.setVisible(true);
            textArea.clear();

            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(filexml);
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("DriverData");
                System.out.println("Wyswietlanie danych ");
                for (int itr = 0; itr < nodeList.getLength(); itr++) {
                    Node node = nodeList.item(itr);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) node;
//                   Font font = Font.font(textArea.getFont().getSize()+10.0f);
//                   textArea.setFont(font);
                        //title page
                        textArea.appendText(filexml.getName() + ", ");
                        textArea.appendText(eElement.getElementsByTagName("CardHolderSurname").
                                item(0).getTextContent() + ", ");
                        textArea.appendText(eElement.getElementsByTagName("CardHolderFirstNames").
                                item(0).getTextContent() + ", ");
                        textArea.appendText(eElement.getElementsByTagName("CardHolderPreferredLanguage").
                                item(0).getTextContent() + "\n\n");
                        // CardExtendedSerialNumber

                        // dodać serial number / data / rfu/  (jak wyciąga się value xml elementu month year itp..)
                        textArea.appendText("Identyfikacja karty ICC:  \n");
                        textArea.appendText("\t ClockStop: " + eElement.getElementsByTagName("ClockStop").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t CardExtendedSerialNumber: " + eElement.getElementsByTagName("CardExtendedSerialNumber").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t Numer zatwierdzenia karty: " + eElement.getElementsByTagName("CardApprovalNumber").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t CardPersonaliserId: " + eElement.getElementsByTagName("CardPersonaliserId").
                                item(0).getTextContent() + "\n");
                        //EmbedderIcAssemblerId
                        textArea.appendText("\t EmbedderIcAssemblerId: " + "\n");
                        textArea.appendText("\t\t -CountryCode: " + eElement.getElementsByTagName("CountryCode").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t -ModuleEmbedder: " + eElement.getElementsByTagName("CountryCode").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t -ManufacturerInformation: " + eElement.getElementsByTagName("ManufacturerInformation").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t IcIdentifier: " + eElement.getElementsByTagName("IcIdentifier").
                                item(0).getTextContent() + "\n");
                        //CardChipIdentyfiaction
                        textArea.appendText("CardChipIdentification: " + eElement.getElementsByTagName("CardChipIdentification").
                                item(0).getTextContent() + "\n");
                        // ext value
                        textArea.appendText("\t IcSerialNumber: " + eElement.getElementsByTagName("IcSerialNumber").
                                item(0).getTextContent() + "\n");
                        // ext value
                        textArea.appendText("\t IcManufacturingReferences: " + eElement.getElementsByTagName("IcManufacturingReferences").
                                item(0).getTextContent() + "\n");
                        //DriverCardApplicationIdentyfication
                        textArea.appendText(" DriverCardApplicationIdentification: " + "\n");
                        textArea.appendText("\t Type: " + eElement.getElementsByTagName("Type").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t Version: " + eElement.getElementsByTagName("Version").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t NoOfEventsPerType: " + eElement.getElementsByTagName("NoOfEventsPerType").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t NoOfFaultsPerType: " + eElement.getElementsByTagName("NoOfFaultsPerType").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t ActivityStructureLength: " + eElement.getElementsByTagName("ActivityStructureLength").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t NoOfCardVehicleRecords: " + eElement.getElementsByTagName("NoOfCardVehicleRecords").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t NoOfCardPlaceRecords: " + eElement.getElementsByTagName("NoOfCardPlaceRecords").
                                item(0).getTextContent() + "\n");
                        //CardCertificate
                        textArea.appendText(" CardCertificate: " + "\n");
                        //ext value
                        textArea.appendText("\t Signature: " + eElement.getElementsByTagName("Signature").
                                item(0).getTextContent() + "\n");
                        //ext value
                        textArea.appendText("\t PublicKeyRemainder: " + eElement.getElementsByTagName("PublicKeyRemainder").
                                item(0).getTextContent() + "\n");
                        //CertificationAuthorityReference
                        textArea.appendText("\t CertificationAuthorityReference: " + "\n");
                        textArea.appendText("\t\t Nation: " + eElement.getElementsByTagName("Nation").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t NationCode: " + eElement.getElementsByTagName("NationCode").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t SerialNumber: " + eElement.getElementsByTagName("SerialNumber").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t AdditionalInfo: " + eElement.getElementsByTagName("AdditionalInfo").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t CaIdentifier: " + eElement.getElementsByTagName("CaIdentifier").
                                item(0).getTextContent() + "\n");
                        //Identification
                        textArea.appendText(" Identification: " + "\n");
                        //CardIdentyfication
                        textArea.appendText("\t CardIdentification: " + "\n");
                        textArea.appendText("\t\t CardIssuingMemberState: " + eElement.getElementsByTagName("CardIssuingMemberState").
                                item(0).getTextContent() + "\n");
                        // ext value
                        textArea.appendText("\t\t CardNumber: " + eElement.getElementsByTagName("CardNumber").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t CardIssuingAuthorityName: " + eElement.getElementsByTagName("CardIssuingAuthorityName").
                                item(0).getTextContent() + "\n");
                        // ext value
                        textArea.appendText("\t\t CardIssueDate: " + eElement.getElementsByTagName("CardIssueDate").
                                item(0).getTextContent() + "\n");
                        // ext value
                        textArea.appendText("\t\t CardValidityBegin: " + eElement.getElementsByTagName("CardValidityBegin").
                                item(0).getTextContent() + "\n");
                        // ext value
                        textArea.appendText("\t\t CardExpiryDate: " + eElement.getElementsByTagName("CardExpiryDate").
                                item(0).getTextContent() + "\n");
                        //DriverCardHolderIdentyfication
                        textArea.appendText("\t DriverCardHolderIdentification: " + eElement.getElementsByTagName("DriverCardHolderIdentification").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t CardHolderSurname: " + eElement.getElementsByTagName("CardHolderSurname").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t CardHolderFirstNames: " + eElement.getElementsByTagName("CardHolderFirstNames").
                                item(0).getTextContent() + "\n");
                        // ect value
                        textArea.appendText("\t\t CardHolderBirthDate: " + eElement.getElementsByTagName("CardHolderBirthDate").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t CardHolderPreferredLanguage: " + eElement.getElementsByTagName("CardHolderPreferredLanguage").
                                item(0).getTextContent() + "\n");
                        //CardDrivingLicenceInformation
                        textArea.appendText("\t CardDrivingLicenceInformation: " + eElement.getElementsByTagName("CardDrivingLicenceInformation").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t DrivingLicenceIssuingAuthority: " + eElement.getElementsByTagName("DrivingLicenceIssuingAuthority").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t DrivingLicenceIssuingNation: " + eElement.getElementsByTagName("DrivingLicenceIssuingNation").
                                item(0).getTextContent() + "\n");
                        textArea.appendText("\t\t DrivingLicenceNumber: " + eElement.getElementsByTagName("DrivingLicenceNumber").
                                item(0).getTextContent() + "\n");
                        //Events>CardEventRecorded>CardEventRecordedColection
                        //CardDrivingRecord
                        textArea.appendText(" CardEventRecord: " + eElement.getElementsByTagName("CardEventRecord").
                                item(0).getTextContent() + "\n");
                        textArea.appendText(" \t CardEventRecord: " + eElement.getElementsByTagName("CardEventRecord").
                                item(0).getTextContent() + "\n");
                        // BEGIN

//                    NodeList nodeListDriverData = doc.getElementsByTagName("CardEventRecord");
//                    for(int i = 0; i<=nodeListDriverData.getLength();i++) {
//                        Node nodeElementCardEvent = nodeListDriverData.item(i);
//                    }


                        NodeList dataDriver = doc.getElementsByTagName("CardDriverActivity");
//                    if (dataDriver.item(itr).hasAttributes()) {
                        System.out.println(dataDriver.getLength());

                        NodeList CardActivityDailyRecord = doc.getElementsByTagName("CardActivityDailyRecord");
                        NodeList ActivityChangeInfo = doc.getElementsByTagName("ActivityChangeInfo");
                        for (int cout = 0; cout < dataDriver.getLength(); cout++) {

                            System.out.print(CardActivityDailyRecord.item(cout).getAttributes().item(0).getNodeValue() + "\n");
                            System.out.print(ActivityChangeInfo.item(cout).getAttributes().item(0).getNodeValue() + "\n");


                            //NamedNodeMap dataTimeAtributes = dataDriver.item(cout).getAttributes();
                            //test
//                        System.out.println(cout);
//
//                        System.out.println(dataDriver.item(cout).getAttributes().item(0).getNodeValue());

//                        System.out.println(dataTimeAtributes.item(cout).getNodeValue());
//                        System.out.println(dataTimeAtributes.item(cout).getNodeValue());

//                        for (int j = 0; j <= dataTimeAtributes.getLength(); j++) {
//                            Node nodeAtribute = dataTimeAtributes.item(j);
//                            textArea.appendText(" \t\t EventType: " + eElement.getElementsByTagName("EventType").
//                                    item(0).getTextContent() + "\n");
//                            // ext value
//
////                        textArea.appendText(" \t\t EventBeginTime: " + eElement.getElementsByTagName("EventBeginTime").
////                                item(0).getTextContent() + "\n");
//                            System.out.println(nodeAtribute.getNodeValue());
//
////
////                            textArea.appendText(nodeList.item(0).getAttributes().getNamedItem("DateTime").getNodeValue());
//
//                            // ext value // Wyciągnąć wrtość z DataTime
//                            textArea.appendText(" \t\t EventEndTime: " + eElement.getNodeName() + "\n");
//                            textArea.appendText(" \t\t\t VehicleRegistration: " + eElement.getElementsByTagName("VehicleRegistration").
//                                    item(0).getTextContent() + "\n");
//                            textArea.appendText(" \t\t\t VehicleRegistrationNumber: " + eElement.getElementsByTagName("VehicleRegistration").
//                                    item(0).getTextContent() + "\n");
                        }
                    }
                }
                //END


//                    for (int itr = 0; itr < nodeList.getLength(); itr++) {
//                        Node node = nodeList.item(itr);
//                        if (node.getNodeType() == Node.ELEMENT_NODE) {
//
//                        }


                //Po załadowaniu pliku przechodzi do kolejnej zakładki i wyświetla text w TextArea
                // po prawej stronie konwersja do pdf i zapisanie

                //CardFaultRecords
//                }
            } catch (ParserConfigurationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SAXException ex) {
                ex.printStackTrace();
            }
        }else{
            dragOver.setText("Taki plik nie istnieje");
        }
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

}
