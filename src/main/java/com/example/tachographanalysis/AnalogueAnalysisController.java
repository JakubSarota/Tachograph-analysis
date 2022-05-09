package com.example.tachographanalysis;

import com.asprise.imaging.core.Imaging;
import com.asprise.imaging.core.Request;
import com.asprise.imaging.core.RequestOutputItem;
import com.asprise.imaging.core.Result;
import com.asprise.imaging.scan.ui.workbench.AspriseScanUI;
import com.example.tachographanalysis.PDF.CreatePDF;
import com.example.tachographanalysis.analogueAnalysis.AnalysisCircle;
import com.example.tachographanalysis.size.SizeController;
import com.itextpdf.text.DocumentException;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
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
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
;import static java.lang.Integer.parseInt;


public class AnalogueAnalysisController {
    @FXML
    private Button btnBack, dragOver, addStats, createPDF, loadAnotherFile, btnScanner, btnOpenAnalogue;
    @FXML
    private ImageView imageView, imageView2, imgViewOpenAnalogue;
    @FXML
    private Image image, imgOpenAnalogue;
    @FXML
    private ScrollPane scroll;
    @FXML
    private TextArea textArea, fileText;
    @FXML
    private AnchorPane showAnalysis, showDragAndDrop;
    @FXML
    private Label loading;
    @FXML
    private BarChart barChart;
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
//        System.out.println(event.getDragboard().getFiles().stream().map(file -> getExtension(String.valueOf(file.length()))));
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
//        System.out.println(image);
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

        if((writableImage[0]!=null) || (writableImage[1]!=null)) {
            showDragAndDrop.setVisible(false);
            showAnalysis.setVisible(true);
            addStats.setVisible(true);
            createPDF.setVisible(true);
            loadAnotherFile.setVisible(true);
            btnScanner.setDisable(true);
            btnOpenAnalogue.setDisable(true);
            btnOpenAnalogue.setDisable(true);
            File findm = new File(System.getProperty("user.dir")+"\\findMinimum.txt");
            File findmw = new File(System.getProperty("user.dir")+"\\findMinimumWithoutDegree.txt");
            if(findm.exists() || findmw.exists()) {
                findm.delete();
                findmw.delete();
            }
        } else {
            dragOver.setText("Nie odnaleziono tarczy, spróbuj ponownie");
            selectedFileAnalogue = null;
            loading.setVisible(false);
        }

        if(analysisCircle.blackImage!=null) {
            writeWork(analysisCircle.blackImage.czas_pracy());
//            analysisCircle.blackImage.save("png",image
//                    .replace("file:/","")+"praca.png");
        }
    }

    private void getImageDragAndDrop(List<File> files) throws Exception {
        scroll.pannableProperty().set(true);
        scroll.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        file_name=String.valueOf(files.get(0)).replace("file:/","");
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
            addStats.setVisible(true);
            createPDF.setVisible(true);
            loadAnotherFile.setVisible(true);
            btnScanner.setDisable(true);
            btnOpenAnalogue.setDisable(true);
            btnOpenAnalogue.setDisable(true);
            File findm = new File(System.getProperty("user.dir")+"\\findMinimum.txt");
            File findmw = new File(System.getProperty("user.dir")+"\\findMinimumWithoutDegree.txt");
            if(findm.exists() || findmw.exists()) {
                findm.delete();
                findmw.delete();
            }
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
                    lastBreak=i-1;
                } else {
                    pracowal = true;
                    if(przerwa) {
                        przerwa = false;
                    }
                }
            }
            if(!pracowal) {
                lastWork=i;
                xml+="<ActivityChangeInfo FileOffset=\"0x2D12\" Slot=\"0\" Status=\"0\" Inserted=\"True\" Activity=\"Work\" Time=\""+
                        analysisCircle.blackImage.ktoraGodzina(Integer.parseInt((String) jarr.get(i)))+"\" />\n";
            }
        }

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

        barChart.getData().clear();
        barChart.getData().removeAll();
        barChart.setVisible(true);

        barChart.setTitle("Aktywność pracownika ");
        barChart.getXAxis().setLabel("Aktywność");
        barChart.getYAxis().setLabel("Godziny");

        barChart.setAnimated(false);
        Object[] dataDiffOneDaTable = DigitalAnalysisController.dataDiffOneDay(s[1]);

        String[] activityDataWork = (String[]) dataDiffOneDaTable[0];
        String[] activityDataBreak = (String[]) dataDiffOneDaTable[2];

        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        sumBreak=parseInt(String.valueOf(DigitalAnalysisController.timeDiffrence(activityDataBreak))) / 60;
        sumWork=parseInt(String.valueOf(DigitalAnalysisController.timeDiffrence(activityDataWork))) / 60;
        series1.setName("Przerwa ("+sumBreak+")");
        series2.setName("Praca ("+sumWork+")");
        series2.getData().add(new XYChart.Data("Praca",
                sumWork));
//        series1.getData().add(new XYChart.Data("Jazda",
//                parseInt(String.valueOf(DigitalAnalysisController.timeDiffrence(activityDataDrive))) / 60));
        series1.getData().add(new XYChart.Data("Przerwa",
               sumBreak ));
        barChart.getData().addAll(series1);
        barChart.getData().addAll(series2);

    }

    StackPane stackPane = new StackPane();
    Scene secondScene = new Scene(stackPane, 950,420);
    Stage secondStage = new Stage();

    public void addStats() throws IOException {
        if(secondStage==null || !secondStage.isShowing()) {
            Parent fxmlLoader = FXMLLoader.load(getClass().getResource("addStats.fxml"));
            stackPane.getChildren().add(fxmlLoader);
            secondStage.getIcons().add(new Image(getClass().getResourceAsStream("images/DRIVER.png")));
            secondStage.setTitle("Dodaj statystyki");
            secondStage.setScene(secondScene);
            secondStage.show();
        } else {
            secondStage.toFront();
        }
    }

    public void makePDF(MouseEvent mouseEvent) throws DocumentException, IOException, ParserConfigurationException, SAXException {
        String fileNametoSearch = file_name.substring(file_name.lastIndexOf("/")+1);

//        Desktop desktop = Desktop.getDesktop();
//        File dirToOpen = null;
//        try {
//            dirToOpen = new File(".\\PDF\\");
//            desktop.open(dirToOpen);
//        } catch (IllegalArgumentException | IOException iae) { }
//        File file = new File(String.valueOf(fileChooser));
        if(file_name.indexOf("/")!=-1) {

            String createPDF = CreatePDF.createPDF(new String[]{textArea.getText()},file_name.substring(file_name.lastIndexOf("/")+1),file_name);

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
        if(file_name.indexOf("\\")!=-1) {
            String createPDF = CreatePDF.createPDF(new String[]{textArea.getText()},file_name.substring(file_name.lastIndexOf("\\")+1),file_name);
            String[] buttons = {"OK", "Otwórz plik PDF"};
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
    }

    public static void openFolder()  {
        Desktop desktop = Desktop.getDesktop();
        File dirToOpen = null;
        try {
            dirToOpen = new File(".\\PDF\\");
            desktop.open(dirToOpen);
        } catch (IllegalArgumentException | IOException iae) { }
    }

    public void openFolder1(MouseEvent mouseEvent)  {
      openFolder();
    }

    public void openScanner() throws Exception {
        loading.setVisible(true);
        Result result = new AspriseScanUI().setRequest(
                        new Request().addOutputItem(
                                new RequestOutputItem(Imaging.OUTPUT_SAVE, Imaging.FORMAT_PNG)
                                        .setSavePath(System.getProperty("user.dir")+"\\archiwum"+"\\\\${TMS}${EXT}")))
                .showDialog(null, "Użyj skanera", true, null);

//        System.out.println(result == null ? "(null)" : result.getImageFiles());
        if(result==null) {
            loading.setVisible(false);
            dragOver.setText(text);
            selectedFileAnalogue = null;
        } else {
            imageFile = String.valueOf(result.getImageFiles());
            file_name=imageFile.replace("[","");
            file_name=file_name.replace("]", "");
            System.out.println(file_name);
            if(file_name==null) {
                loading.setVisible(false);
                dragOver.setText("Nie udało się załadować pliku ze skanera");
                selectedFileAnalogue = null;
            } else {
                getImageOnClick(file_name);
            }
        }
    }

    public void loadImageAgain() throws IOException {
        selectedFileAnalogue = null;
        dragOver.setText(text);
        showAnalysis.setVisible(false);
        showDragAndDrop.setVisible(true);
        loading.setVisible(false);
        addStats.setVisible(false);
        createPDF.setVisible(false);
        loadAnotherFile.setVisible(false);
        btnScanner.setDisable(false);
        btnOpenAnalogue.setDisable(false);
        btnOpenAnalogue.setDisable(false);
    }

    public void OpenExistsFiles() throws IOException {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("drivers.fxml"));
        Stage scene = (Stage) btnBack.getScene().getWindow();
        scene.setScene(new Scene(fxmlLoader, SizeController.sizeW, SizeController.sizeH));
    }

}
