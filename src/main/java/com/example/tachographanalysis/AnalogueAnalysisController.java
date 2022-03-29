package com.example.tachographanalysis;

import com.example.tachographanalysis.analogueAnalysis.analysisCircle;
import com.example.tachographanalysis.size.SizeController;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
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
    private TextArea textArea;

    private String imageFile, text = "Wybierz plik albo upuść go tutaj";
    static double ip = 0;
    analysisCircle analysisCircle = new analysisCircle();

    @FXML
    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
//        scene.setMaximized(true);
    }

    @FXML
    private void handleDragOverButton(DragEvent event) {
        if(event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        dragOver.setText("Upuść tutaj plik");
    }

    @FXML
    private void onDragClickedButton() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.png","*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        imageFile = selectedFile.toURI().toURL().toString();
        image = new Image(imageFile);
        if(image.getWidth() <= 1000 || image.getHeight() <= 1000) {
            dragOver.setText("Rozmiar pliku jest za mały");
        } else if(selectedFile != null) {
            getImageOnClick(imageFile);
            dragOver.setText(text);
        } else if(selectedFile == null) {
            dragOver.setText(text);
        }
    }

    @FXML
    private void handleDroppedButton(DragEvent event) throws IOException, InterruptedException {
        List<File> files = event.getDragboard().getFiles();
        List<String> validExtensions = Arrays.asList("jpg", "png");
        image = new Image(new FileInputStream(files.get(0)));
        if(!validExtensions.containsAll(event.getDragboard()
                .getFiles().stream().map(file -> getExtension(file.getName()))
                .collect(Collectors.toList()))) {
            dragOver.setText("To nie jest plik graficzny!");
        } else if(image.getWidth() <= 1000 || image.getHeight() <= 1000) {
            dragOver.setText("Rozmiar pliku jest za mały");
        } else {
            getImageDragAndDrop(files);
            dragOver.setText(text);
        }
    }

    public String getExtension(String fileName){
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1)
            return fileName.substring(i + 1).toLowerCase();
        return extension;
    }

    private void getImageOnClick(String image) throws IOException, InterruptedException {
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
            analysisCircle.blackImage.save("png",image
                    .replace("file:/","")+"praca.png");
        }else
            dragOver.setText("Nie odnaleziono tarczy");
    }

    private void getImageDragAndDrop(List<File> files) throws IOException, InterruptedException {
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
        }else
            dragOver.setText("Nie odnaleziono tarczy");
    }

    private void writeWork(JSONObject json){
        JSONArray jarr=json.getJSONArray("praca");
//        System.out.println(jarr.length());
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
//
        for (int i=0;i<jarr.length();i++) {
            boolean pracowal=false;
            boolean przerwa=false;
            int tmp=Integer.parseInt((String) jarr.get(i));
            if(i>0) {
                if (Integer.parseInt((String) jarr.get(i)) - 1 == Integer.parseInt((String) jarr.get(i - 1))) {

                    pracowal = true;
                }
                if(Integer.parseInt((String) jarr.get(i))-15  >= Integer.parseInt((String) jarr.get(i - 1))){
                    przerwa=true;
                    xml+="<ActivityChangeInfo FileOffset=\"\" Slot=\"\" Status=\"\" Inserted=\"True\" Activity=\"Break\" Time=\""+
                                    analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(i - 1)))+"\" />\n";

                    text+="Break "+analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(i - 1)))+"\n";
                }else{
                    pracowal = true;
                    if(przerwa){
                        przerwa=false;
                    }
                }
            }
            if(!pracowal) {
                text += "Work " + analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(i))) + "\n";
                xml+="<ActivityChangeInfo FileOffset=\"\" Slot=\"\" Status=\"\" Inserted=\"True\" Activity=\"Work\" Time=\""+
                        analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(i)))+"\" />\n";
            }
        }

        text+="Break "+analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(jarr.length() - 1)))+"\n";
        xml+="<ActivityChangeInfo FileOffset=\"\" Slot=\"\" Status=\"\" Inserted=\"True\" Activity=\"Break\" Time=\""+
                analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(jarr.length() - 1)))+"\" />\n";
        xml+="            </CardActivityDailyRecord>\n" +
                "            <DataBufferIsWrapped>True</DataBufferIsWrapped>\n" +
                "        </CardDriverActivity>\n" +
                "    </DriverActivityData>\n"+
                "</DriverData>";

//        System.out.println(xml);

        textArea.setText(text);
    }

}
