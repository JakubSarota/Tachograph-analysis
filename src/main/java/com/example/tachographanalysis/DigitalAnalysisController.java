package com.example.tachographanalysis;

import com.example.tachographanalysis.size.SizeController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Button btnBack, btnUpload;
    @FXML
    private Label uploadText;
    @FXML
    private Button dragOver;
    @FXML
    private File file;
    @FXML
    private String DDDFile;
    //    String text = "Choose file from memory or drag and drop here";
    List<String> lstFile;

    public void getBack() throws Exception {
        Parent fxmlLoader = FXMLLoader.load(getClass().getResource("main.fxml"));
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
    void onDragClickedButton(MouseEvent event) throws IOException {

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
                    textArea.appendText(filexml.getName()+", ");
                    textArea.appendText( eElement.getElementsByTagName("CardHolderSurname").
                            item(0).getTextContent()+", ");
                    textArea.appendText(eElement.getElementsByTagName("CardHolderFirstNames").
                            item(0).getTextContent()+", ");
                    textArea.appendText(  eElement.getElementsByTagName("CardHolderPreferredLanguage").
                            item(0).getTextContent() + "\n\n");
                    // CardExtendedSerialNumber

                    textArea.appendText(  "Identyfikacja karty ICC: "+ eElement.getElementsByTagName("CardExtendedSerialNumber").
                            item(0).getTextContent() + "\n\n");
                    textArea.appendText(  "Numer zatwierdzenia karty: "+ eElement.getElementsByTagName("CardExtendedSerialNumber").
                            item(0).getTextContent() + "\n\n");


                }
            }


        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
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


                    textArea.appendText(scanner.nextLine() + "\n");
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
