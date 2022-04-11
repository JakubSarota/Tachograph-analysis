package com.example.tachographanalysis.database.stats;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class AddStatsDigital {
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

    public AddStatsDigital() throws IOException {

//        String file_name= UUID.randomUUID().toString() + ".DDD";
//        addStats.insertToDatabase(Integer.parseInt("1"), dataPicker.getValue().toString(), LocalDate.now().toString(),
//                "Info o pracy", DigitalAnalysisController.workSum, DigitalAnalysisController.breakSum,
//                file_name, "cyfrowy", parseInt("5345"));
//        System.out.println("Pomyślnie dodano");
  }
}
