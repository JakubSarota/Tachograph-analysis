package com.example.tachographanalysis.database;

import com.example.tachographanalysis.AnalogueAnalysisController;
import com.example.tachographanalysis.DigitalAnalysisController;
import com.example.tachographanalysis.analogueAnalysis.analysisCircle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;


import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class addStats {

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
    protected void initialize() throws Exception {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getDBConnection();

        String connectQuery = "SELECT id,first_name,second_name, last_name FROM driver";
        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryOutput = statement.executeQuery(connectQuery);

            while (queryOutput.next()){
                String firstname = queryOutput.getString("first_name");
                String lastname = queryOutput.getString("second_name");
                int id=queryOutput.getInt("id");
                String listOut =  id+" "+ firstname + " " + lastname;
                accountListView.getItems().add(listOut);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        accountListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("ListView selection changed from oldValue = "
                        + oldValue + " to newValue = " + newValue);
                driver=newValue;
            }
        });
        String[] s=DigitalAnalysisController.readData(new File(".\\ddd_to_xml\\data\\driver\\analoguexml.xml"));
        textArea.setText(s[1]);
        breakTime.setText(String.valueOf(analysisCircle.blackImage.ktoraGodzina(AnalogueAnalysisController.sumBreak)));
        workTime.setText(String.valueOf(analysisCircle.blackImage.ktoraGodzina(AnalogueAnalysisController.sumWork)));
        dataPicker.setValue(LocalDate.now());
    }
    public void addStats() throws IOException {
        if(driver!=null) {
            Pattern p = Pattern.compile("[0-9]+");
            Matcher m = p.matcher(driver);
            String d="0";
            while(m.find()){
                d=m.group();
            }
            if(d!="0") {
                File dir = new File(".\\archiwum\\");
                String name_of_file=UUID.randomUUID().toString() + ".png";
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Files.copy(Path.of(AnalogueAnalysisController.file_name),
                        Path.of(".\\archiwum\\" + name_of_file));

                insertToDatabase(Integer.parseInt(d), dataPicker.getValue().toString(), LocalDate.now().toString(),
                        textArea.getText(), workTime.getText(),breakTime.getText(),
                        name_of_file , "analogowy", Integer.parseInt(sumRoad.getText()));
            }
        }
    }

    public void insertToDatabase(int driver_id,String date_work,String date_add,String work_info,String sumWork,
                                 String sumBreak,String file,String file_type,int sumRoad) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connectDB = databaseConnection.getDBConnection();

        try{
            Statement statement = connectDB.createStatement();

            int status = statement.executeUpdate(
                    "INSERT INTO stats (driver_id, date_work, date_add, work_info, sum_work, sum_break, file, file_type, sum_road)" +
                            " VALUES('"+driver_id+"','"+date_work+"','"+date_add+"','"+work_info+"','"+sumWork+"','"+
                            sumBreak+"','"+file+"','"+file_type+"','"+sumRoad+"')");

            if(status>0) {
                System.out.println("Add stats");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
