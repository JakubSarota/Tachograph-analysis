package com.example.tachographanalysis.database.stats;

import com.example.tachographanalysis.AnalogueAnalysisController;
import com.example.tachographanalysis.DigitalAnalysisController;
import com.example.tachographanalysis.analogueAnalysis.AnalysisCircle;
import com.example.tachographanalysis.database.DatabaseConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddStats {

    @FXML
    private DatePicker dataPicker;
    @FXML
    private ListView accountListView;
    @FXML
    private ListView timeListView;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField breakTime;
    @FXML
    private TextField workTime;
    @FXML
    private TextField sumRoad;
    private String driver;
    private String time_start;
    private ArrayList<String> time_arr=new ArrayList<String>();
    @FXML
    private Text returnInfo;
    @FXML
    protected void initialize() throws Exception {
        String connectQuery = "SELECT id,first_name,last_name, last_name FROM driver";
        try{
            ResultSet queryOutput = DatabaseConnection.exQuery(connectQuery);
            while (queryOutput.next()){
                String firstname = queryOutput.getString("first_name");
                String lastname = queryOutput.getString("last_name");
                int id = queryOutput.getInt("id");
                String listOut =  id+" "+ firstname + " " + lastname;
                accountListView.getItems().add(listOut);
            }
            try {
                if(queryOutput!=null) {
                    queryOutput.close();
                }
            } catch (Exception e) {}
        } catch (Exception e){
            e.printStackTrace();
        }
        accountListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                System.out.println("ListView selection changed from oldValue = "
//                        + oldValue + " to newValue = " + newValue);
                driver=newValue;
            }
        });
        String[] s=DigitalAnalysisController.readData(new File(".\\ddd_to_xml\\data\\driver\\analoguexml.xml"));
        textArea.setText(s[1].substring(s[1].indexOf("Dzień pracy:")+12));
        breakTime.setText(String.valueOf(AnalysisCircle.blackImage.ktoraGodzina(AnalogueAnalysisController.sumBreak)));
        workTime.setText(String.valueOf(AnalysisCircle.blackImage.ktoraGodzina(AnalogueAnalysisController.sumWork)));
        dataPicker.setValue(LocalDate.now());
        String l=textArea.getText();
        l=l.replace("\t","");
        l=l.replace("\n","");
        String[] ll=l.split(" ",-1);
        String lastActivity="";
        int licznik=0;
        for (String lll:
                ll) {
            if(lll.equals("Work")||lll.equals("Break"))
                lastActivity=lll;
            if(lll.indexOf(":")!=-1&&lll.indexOf(":")!=lll.length()-1){
                if(lastActivity.equals("Work"))
                    timeListView.getItems().add(lll);
                time_arr.add(lll);
                licznik++;
            }
        }
        timeListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                System.out.println("ListView selection changed from oldValue = "
//                        + oldValue + " to newValue = " + newValue);
                time_start=newValue;
            }
        });
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
                int indexWork=0;
                int indexWorkEnd=time_arr.size()-1;
                if(time_start==null) {
                    returnInfo.setText("Zaznacz godzinę rozpoczęcia pracy");
                } else {
                    for (int t=0;t<time_arr.size();t++) {
                        if(time_start.equals(time_arr.get(t))) {
                            indexWork = t;
                            if(indexWork!=0)
                                indexWorkEnd=t-1;
                        }
                    }
                }
                returnInfo.setText(insertToDatabase(Integer.parseInt(d), dataPicker.getValue().toString(), LocalDate.now().toString(),
                    textArea.getText(), workTime.getText(), breakTime.getText(),
                    name_of_file, "analogowy", Integer.parseInt(sumRoad.getText()),
                        dataPicker.getValue().plusDays(1).toString(), time_start, time_arr.get(indexWorkEnd)));
            }
        }else{
            returnInfo.setText("Nie wybrano kierowcy");
        }
    }

    public static String insertToDatabase(int driver_id, String date_work, String date_add, String work_info, String sumWork,
                                          String sumBreak, String file, String file_type, int sumRoad, String date_work_end,
                                          String time_work_start,String time_work_end) {

        try{
            String query = "SELECT * FROM stats WHERE date_work='"+date_work+"' AND driver_id='"+driver_id+"'";
            System.out.println(query);
            ResultSet queryOutput =  DatabaseConnection.exQuery(query);
            System.out.println(queryOutput);
            System.out.println(queryOutput==null);
            if(!queryOutput.next()) {
                    int status = DatabaseConnection.exUpdate(
            "INSERT INTO stats (driver_id, date_work, date_add, work_info, sum_work, sum_break, file, file_type, " +
                    "sum_road, date_work_end, time_work_start, time_work_end)" +
                  " VALUES('" + driver_id + "','" + date_work + "','" + date_add + "','" + work_info + "','" + sumWork + "','" +
                  sumBreak + "','" + file + "','" + file_type + "','" + sumRoad + "','" + date_work_end + "','" +
                    time_work_start + "','" + time_work_end + "')");

                if(queryOutput!=null) {
                    queryOutput.close();
                }
                    if (status > 0) {
                        return "Dodano";
                    }
            }else{
                if(queryOutput!=null) {
                    queryOutput.close();
                }
                return "Istnieją już statystyki dla tego kierowcy tego dnia";
            }
            if(queryOutput!=null) {
                queryOutput.close();
            }
        } catch (SQLException throwables) {
//            throwables.printStackTrace();
        }
        return "Nie udało się dodać";
    }
}
