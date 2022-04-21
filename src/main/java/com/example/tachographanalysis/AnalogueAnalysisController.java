package com.example.tachographanalysis;

import com.example.tachographanalysis.PDF.CreatePDF;
import com.example.tachographanalysis.analogueAnalysis.AnalysisCircle;
import com.example.tachographanalysis.size.SizeController;
import com.itextpdf.text.DocumentException;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnalogueAnalysisController {
    @FXML
    private Button btnBack, dragOver;
    @FXML
    private ImageView imageView, imageView2;
    @FXML
    private Image image;
    @FXML
    private ScrollPane scroll;
    @FXML
    private TextArea textArea, fileText;
    @FXML
    private AnchorPane showAnalysis, showDragAndDrop;
    @FXML
    private Label loading;

    public static int sumBreak=0;
    public static int sumWork=0;
    public static String file_name;

    private String imageFile, text = "Wybierz plik albo upuść go tutaj";
    static double ip = 0;
    AnalysisCircle analysisCircle = new AnalysisCircle();
    File selectedFileAnalogue;
    @FXML
    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
    }

    @FXML
    private void handleDragOverButton(DragEvent event) {
        if(event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        if(event.isAccepted()) {
            loading.setVisible(true);
        }
        dragOver.setText("Upuść tutaj plik");
    }

    @FXML
    private void onDragClickedButton() throws Exception {
        loading.setVisible(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.png","*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if(selectedFile==null){
            loading.setVisible(false);
        }
//        System.out.println("selectedFile "+ selectedFile);
        imageFile = selectedFile.toURI().toURL().toString();
//        System.out.println("imageFile "+ imageFile);
        file_name=imageFile.replace("file:/","");
        image = new Image(imageFile);
        this.selectedFileAnalogue = selectedFile;
        if(image.getWidth() <= 1000 || image.getHeight() <= 1000) {
            dragOver.setText("Rozmiar pliku jest za mały, spróbuj ponownie");
            loading.setVisible(false);
        } else if(selectedFile != null) {
            getImageOnClick(imageFile);
        }

    }

    @FXML
    private void handleDroppedButton(DragEvent event) throws Exception {
        List<File> files = event.getDragboard().getFiles();
        List<String> validExtensions = Arrays.asList("jpg", "png");
        image = new Image(new FileInputStream(files.get(0)));
        if(!validExtensions.containsAll(event.getDragboard()
                .getFiles().stream().map(file -> getExtension(file.getName()))
                .collect(Collectors.toList()))) {
            dragOver.setText("To nie jest plik graficzny, spróbuj ponownie");
            loading.setVisible(false);
        } else if(image.getWidth() <= 1000 || image.getHeight() <= 1000) {
            dragOver.setText("Rozmiar pliku jest za mały, spróbuj ponownie");
            loading.setVisible(false);
        } else {
            getImageDragAndDrop(files);
        }
    }

    public String getExtension(String fileName){
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1)
            return fileName.substring(i + 1).toLowerCase();
        return extension;
    }

    private void getImageOnClick(String image) throws Exception {
        scroll.pannableProperty().set(true);
        scroll.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);

        BufferedImage writableImage[] = analysisCircle.getHuanByCircle(image);

        if(writableImage[0]!=null){
            WritableImage wi=SwingFXUtils.toFXImage(writableImage[0],null);
            imageView.setImage(wi);
        }

        if(writableImage[1]!=null){
            WritableImage wi2=SwingFXUtils.toFXImage(writableImage[1],null);
            imageView2.setImage(wi2);
        }

        if(analysisCircle.blackImage!=null) {
            writeWork(analysisCircle.blackImage.czas_pracy());
//            analysisCircle.blackImage.save("png",image
//                    .replace("file:/","")+"praca.png");
        }

        if((writableImage[0]!=null) || (writableImage[1]!=null)) {
            showDragAndDrop.setVisible(false);
            showAnalysis.setVisible(true);
        } else {
            dragOver.setText("Nie odnaleziono tarczy, spróbuj ponownie");
            selectedFileAnalogue = null;
            loading.setVisible(false);
        }
    }

    private void getImageDragAndDrop(List<File> files) throws Exception {
        scroll.pannableProperty().set(true);
        scroll.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        BufferedImage writableImage[] = analysisCircle.getHuanByCircle(String.valueOf(files.get(0)));
        if(writableImage[0]!=null){
            WritableImage wi=SwingFXUtils.toFXImage(writableImage[0],null);
            imageView.setImage(wi);
        }
        if(writableImage[1]!=null){
            WritableImage wi2=SwingFXUtils.toFXImage(writableImage[1],null);
            imageView2.setImage(wi2);
        }
        if(analysisCircle.blackImage!=null) {
            writeWork(analysisCircle.blackImage.czas_pracy());
            analysisCircle.blackImage.save("png",String.valueOf(files.get(0))
                    .replace("file:/","")+"praca.png");
        } else {
//            showDragAndDrop.setVisible(true);
//            showAnalysis.setVisible(false);
//            dragOver.setText("Nie odnaleziono tarczy");
//            fileText.setText("Nie odnaleziono tarczy");
        }
        if((writableImage[0]!=null) || (writableImage[1]!=null)) {
            showDragAndDrop.setVisible(false);
            showAnalysis.setVisible(true);
        } else {
            dragOver.setText("Nie odnaleziono tarczy, spróbuj ponownie");
            loading.setVisible(false);
        }
    }

    private void writeWork(JSONObject json) throws Exception {
        sumBreak=0;
        sumWork=0;
        JSONArray jarr=json.getJSONArray("praca");
        String text="";
        String xml="" +
                "<DriverData>\n" +
                "    <CardIccIdentification>\n" +
                "        <ClockStop></ClockStop>\n" +
                "        <CardExtendedSerialNumber></CardExtendedSerialNumber>\n" +
                "        <CardApprovalNumber></CardApprovalNumber>\n" +
                "        <CardPersonaliserId></CardPersonaliserId>\n" +
                "        <EmbedderIcAssemblerId>\n" +
                "            <CountryCode></CountryCode>\n" +
                "            <ModuleEmbedder></ModuleEmbedder>\n" +
                "            <ManufacturerInformation></ManufacturerInformation>\n" +
                "        </EmbedderIcAssemblerId>\n" +
                "        <IcIdentifier></IcIdentifier>\n" +
                "    </CardIccIdentification>\n" +
                "    <CardChipIdentification>\n" +
                "        <IcSerialNumber/>\n" +
                "        <IcManufacturingReferences/>\n" +
                "    </CardChipIdentification>\n" +
                "    <DriverCardApplicationIdentification>\n" +
                "        <Type></Type>\n" +
                "        <Version></Version>\n" +
                "        <NoOfEventsPerType></NoOfEventsPerType>\n" +
                "        <NoOfFaultsPerType>4</NoOfFaultsPerType>\n" +
                "        <ActivityStructureLength></ActivityStructureLength>\n" +
                "        <NoOfCardVehicleRecords></NoOfCardVehicleRecords>\n" +
                "        <NoOfCardPlaceRecords></NoOfCardPlaceRecords>\n" +
                "    </DriverCardApplicationIdentification>\n" +
                "    <CardCertificate>\n" +
                "        <Signature/>\n" +
                "        <PublicKeyRemainder/>\n" +
                "        <CertificationAuthorityReference>\n" +
                "            <Nation></Nation>\n" +
                "            <NationCode></NationCode>\n" +
                "            <SerialNumber></SerialNumber>\n" +
                "            <AdditionalInfo></AdditionalInfo>\n" +
                "            <CaIdentifier></CaIdentifier>\n" +
                "        </CertificationAuthorityReference>\n" +
                "    </CardCertificate>\n" +
                "    <CACertificate>\n" +
                "        <Signature />\n" +
                "        <PublicKeyRemainder />\n" +
                "        <CertificationAuthorityReference>\n" +
                "            <Nation></Nation>\n" +
                "            <NationCode></NationCode>\n" +
                "            <SerialNumber></SerialNumber>\n" +
                "            <AdditionalInfo></AdditionalInfo>\n" +
                "            <CaIdentifier></CaIdentifier>\n" +
                "        </CertificationAuthorityReference>\n" +
                "    </CACertificate>\n" +
                "    <Identification>\n" +
                "        <CardIdentification>\n" +
                "            <CardIssuingMemberState></CardIssuingMemberState>\n" +
                "            <CardNumber></CardNumber>\n" +
                "            <CardIssuingAuthorityName></CardIssuingAuthorityName>\n" +
                "            <CardIssueDate/>\n" +
                "            <CardValidityBegin/>\n" +
                "            <CardExpiryDate/>\n" +
                "        </CardIdentification>\n" +
                "        <DriverCardHolderIdentification>\n" +
                "            <CardHolderSurname></CardHolderSurname>\n" +
                "            <CardHolderFirstNames></CardHolderFirstNames>\n" +
                "            <CardHolderBirthDate/>\n" +
                "            <CardHolderPreferredLanguage></CardHolderPreferredLanguage>\n" +
                "        </DriverCardHolderIdentification>\n" +
                "    </Identification>\n" +
                "    <CardDrivingLicenceInformation>\n" +
                "        <DrivingLicenceIssuingAuthority />\n" +
                "        <DrivingLicenceIssuingNation></DrivingLicenceIssuingNation>\n" +
                "        <DrivingLicenceNumber></DrivingLicenceNumber>\n" +
                "    </CardDrivingLicenceInformation>\n" +
                "    <EventsData>\n" +
                "        <CardEventRecords>\n" +
                "            <CardEventRecordCollection>\n" +
                "                <CardEventRecord>\n" +
                "                    <EventType></EventType>\n" +
                "                    <EventBeginTime/>\n" +
                "                    <EventEndTime/>\n" +
                "                    <VehicleRegistration>\n" +
                "                        <VehicleRegistrationNation></VehicleRegistrationNation>\n" +
                "                        <VehicleRegistrationNumber />\n" +
                "                    </VehicleRegistration>\n" +
                "                </CardEventRecord>\n" +
                "            </CardEventRecordCollection>\n" +
                "        </CardEventRecords>\n" +
                "    </EventsData>\n" +
                "    <FaultsData>\n" +
                "        <CardFaultRecords>\n" +
                "            <CardFaultRecordCollection>\n" +
                "                <CardFaultRecord>\n" +
                "                    <FaultType></FaultType>\n" +
                "                    <FaultBeginTime/>\n" +
                "                    <FaultEndTime/>\n" +
                "                    <VehicleRegistration>\n" +
                "                        <VehicleRegistrationNation></VehicleRegistrationNation>\n" +
                "                        <VehicleRegistrationNumber />\n" +
                "                    </VehicleRegistration>\n" +
                "                </CardFaultRecord>\n" +
                "                \n" +
                "            </CardFaultRecordCollection>\n" +
                "        </CardFaultRecords>\n" +
                "    </FaultsData>\n"+
                "    <DriverActivityData>\n" +
                "<CardDriverActivity>\n" +
        "            <CardActivityDailyRecord DateTime=\"\" DailyPresenceCounter=\"\" Distance=\"\">\n";

        int lastWork=0,lastBreak=0;
        for (int i=0;i<jarr.length();i++) {
            boolean pracowal=false;
            boolean przerwa=false;

            if(i>0) {
                if (Integer.parseInt((String) jarr.get(i)) - 1 == Integer.parseInt((String) jarr.get(i - 1))) {
                    pracowal = true;
                }

                if(Integer.parseInt((String) jarr.get(i))-15  >= Integer.parseInt((String) jarr.get(i - 1))) {
                    przerwa=true;
                    xml+="<ActivityChangeInfo FileOffset=\"0x2D12\" Slot=\"0\" Status=\"0\" Inserted=\"True\" Activity=\"Break\" Time=\""+
                                    analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(i - 1)))+"\" />\n";
                    sumWork+=Integer.parseInt((String) jarr.get(i - 1))-Integer.parseInt((String) jarr.get(lastWork));
                    lastBreak=i-1;
//                    text+="Break "+analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(i - 1)))+"\n";
                } else {
                    pracowal = true;
                    if(przerwa) {
                        przerwa = false;
                    }
                }
            }
            if(!pracowal) {
                sumBreak+=Integer.parseInt((String) jarr.get(i))-Integer.parseInt((String) jarr.get(lastBreak));
                lastWork=i;
//                text += "Work " + analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(i))) + "\n";
                xml+="<ActivityChangeInfo FileOffset=\"0x2D12\" Slot=\"0\" Status=\"0\" Inserted=\"True\" Activity=\"Work\" Time=\""+
                        analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(i)))+"\" />\n";
            }
        }

        text+="Break "+analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(jarr.length() - 1)))+"\n";
        sumWork+=Integer.parseInt((String) jarr.get(jarr.length() - 1))-Integer.parseInt((String) jarr.get(lastWork));
        xml+="<ActivityChangeInfo FileOffset=\"0x2D12\" Slot=\"0\" Status=\"0\" Inserted=\"True\" Activity=\"Break\" Time=\""+
                analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(jarr.length() - 1)))+"\" />\n";
        xml+="            </CardActivityDailyRecord>\n" +
                "        </CardDriverActivity>\n" +
                "    </DriverActivityData>\n"+
                "</DriverData>";
        File dir = new File(".\\ddd_to_xml\\data\\driver\\");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileWriter xmlfile=new FileWriter(".\\ddd_to_xml\\data\\driver\\analoguexml.xml");
        xmlfile.write(xml);
        xmlfile.close();
        String[] s=DigitalAnalysisController.readData(new File(".\\ddd_to_xml\\data\\driver\\analoguexml.xml"));
        textArea.setText(s[1].substring(s[1].indexOf("Dzień pracy:")+12));
    }
    StackPane stackPane = new StackPane();
    Scene secondScene = new Scene(stackPane, 950,420);
    Stage secondStage = new Stage();

    public void addStats() throws IOException {
        if(selectedFileAnalogue ==null) {
            dragOver.setText("Nie można dodać do statystyk, plik nie został przesłany");
        } else {
            if(secondStage==null || !secondStage.isShowing()) {
                Parent fxmlLoader = FXMLLoader.load(getClass().getResource("addStats.fxml"));
                stackPane.getChildren().add(fxmlLoader);
                secondStage.getIcons().add(new Image(getClass().getResourceAsStream("DRIVER.png")));
                secondStage.setTitle("Dodaj statystyki");
                secondStage.setScene(secondScene);
                secondStage.show();
            } else {
                secondStage.toFront();
            }
        }
    }
    public void makePDF(MouseEvent mouseEvent) throws DocumentException, IOException, ParserConfigurationException, SAXException {
        if(selectedFileAnalogue ==null) {
            dragOver.setText("Nie został presłany plik, operacja nieudana");
        } else {
            CreatePDF.createPDF(new String[]{textArea.getText()},file_name.substring(file_name.lastIndexOf("/")+1),file_name);
//            dragOver.setText("Plik PDF został utworzony!");
            JOptionPane.showMessageDialog(null, "Plik PDF został utworzony");
        }

    }

    public void openFolder(MouseEvent mouseEvent)  {
        Desktop desktop = Desktop.getDesktop();
        File dirToOpen = null;
        try {
            dirToOpen = new File(".\\PDF\\");
            desktop.open(dirToOpen);
        } catch (IllegalArgumentException | IOException iae) { }
    }

    public void loadImageAgain() throws IOException {
        selectedFileAnalogue =null;
        dragOver.setText(text);
        showAnalysis.setVisible(false);
        showDragAndDrop.setVisible(true);
        loading.setVisible(false);
    }
}
