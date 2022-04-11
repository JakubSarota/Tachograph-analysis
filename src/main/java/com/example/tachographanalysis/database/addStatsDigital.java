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

    public addStatsDigital() throws IOException {

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
