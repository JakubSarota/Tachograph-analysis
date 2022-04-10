package com.example.tachographanalysis.database;

import com.example.tachographanalysis.DigitalAnalysisController;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class addStatsDigital {
    @FXML
    private DatePicker dataPicker;
    @FXML
    private ListView accountListView;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField breakTime;
    @FXML
    private TextField workTime;
    @FXML
    private TextField sumRoad;
    private String driver;
    @FXML
    private Text returnInfo;
    @FXML
    protected void initialize() throws Exception {

//        try {
//
//
//            while (queryOutput.next()) {
//                String firstname = queryOutput.getString("first_name");
//                String lastname = queryOutput.getString("second_name");
//                int id = queryOutput.getInt("id");
//                String listOut = id + " " + firstname + " " + lastname;
//                accountListView.getItems().add(listOut);
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        accountListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                System.out.println("Aktwny kierowca zotał zmieniony z: "
//                        + oldValue + " na:  " + newValue);
//                driver=newValue;
//                System.out.println(DigitalAnalysisController.filexmlStats);
//            }
//
//        });
//        //String.valueOf(DigitalAnalysisController.readData(DigitalAnalysisController.filexmlStats));
//        breakTime.setText(DigitalAnalysisController.breakSum);
//        workTime.setText(DigitalAnalysisController.workSum);
//        dataPicker.setValue(LocalDate.parse(DigitalAnalysisController.dataPick));

    }

    public void addStatsDigital() throws IOException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getDBConnection();

        if(driver!=null) {
            Pattern p = Pattern.compile("[0-9]+");
            Matcher m = p.matcher(driver);
            String d="0";
            while(m.find()){
                d=m.group();
            }
            if(d!="0") {
                File dir = new File(".\\archiwum\\");
                String file_name= UUID.randomUUID().toString() + ".DDD";
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Files.copy(Path.of(String.valueOf(DigitalAnalysisController.filexmlStats)),
                        Path.of(".\\archiwum\\" + file_name));

//                for (int i = 0;i < 2; i++) {
                    returnInfo.setText(addStats.insertToDatabase(Integer.parseInt(d), dataPicker.getValue().toString(), LocalDate.now().toString(),
                            "Info o pracy", DigitalAnalysisController.workSum, DigitalAnalysisController.breakSum,
                            file_name, "cyfrowy", Integer.parseInt(sumRoad.getText())));
//                }
            }
        else{
            returnInfo.setText("Nie wybrano kierowcy");
        }
    }
  }
}
