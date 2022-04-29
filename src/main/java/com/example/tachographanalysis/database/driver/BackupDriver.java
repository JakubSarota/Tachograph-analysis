package com.example.tachographanalysis.database.driver;

import com.example.tachographanalysis.database.DatabaseConnection;
import com.example.tachographanalysis.database.stats.AddStats;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;

import javax.swing.*;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackupDriver {
    @FXML
    private ListView accountListView;
    private String driver;

    @FXML
    protected void initialize() throws Exception {
        String connectQuery = "SELECT * FROM trash";
        try {
            ResultSet queryOutput = DatabaseConnection.exQuery(connectQuery);
            while (queryOutput.next()) {
                JSONObject json= new JSONObject(queryOutput.getString("value"));
                String firstname = json.getJSONObject("driver").getString("first_name");
                String lastname = json.getJSONObject("driver").getString("last_name");
                String id = json.getJSONObject("driver").getString("id");
                String id2 = String.valueOf(queryOutput.getInt("id"));
                String listOut = id + " " + firstname + " " + lastname+" "+id2;
                accountListView.getItems().add(listOut);
            }
            try {
                if (queryOutput != null) {
                    queryOutput.close();
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        accountListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                System.out.println("ListView selection changed from oldValue = "
//                        + oldValue + " to newValue = " + newValue);
                driver = newValue;
            }
        });
    }

    public void backUpData(MouseEvent mouseEvent) {
        if(driver!=null){
            Pattern p = Pattern.compile("[0-9]+");
            Matcher m = p.matcher(driver);
            String d="0";
            while(m.find()){
                d=m.group();
            }
            if(d!="0") {
                String connectQuery = "SELECT * FROM trash WHERE id='"+d+"'";
                try {
                    ResultSet queryOutput = DatabaseConnection.exQuery(connectQuery);
                    JSONObject json=null;
                    while (queryOutput.next()) {
                        json= new JSONObject(queryOutput.getString("value"));

                    }
                    try {
                        if (queryOutput != null) {
                            queryOutput.close();
                        }
                    } catch (Exception e) {
                    }
                    if(json!=null){
                        AddDriver.insertToDatabase(
                                json.getJSONObject("driver").getString("last_name"),
                                json.getJSONObject("driver").getString("first_name"),
                                json.getJSONObject("driver").getString("second_name"),
                                json.getJSONObject("driver").getString("email"),
                                json.getJSONObject("driver").getString("pesel"),
                                json.getJSONObject("driver").getString("city"),
                                json.getJSONObject("driver").getString("born_date"),
                                json.getJSONObject("driver").getString("country"),
                                json.getJSONObject("driver").getString("id_card")
                        );
                        String connectQuery2 = "SELECT id FROM driver WHERE last_name='"+json.getJSONObject("driver")
                                .getString("last_name")+"' AND id_card='"+json.getJSONObject("driver").getString("id_card")+"'" +
                                " AND pesel='"+json.getJSONObject("driver").getString("pesel")+"'";
                        try {
                            ResultSet queryOutput2 = DatabaseConnection.exQuery(connectQuery2);
                            int id=0;
                            while (queryOutput2.next()) {
                                id=queryOutput2.getInt("id");

                            }
                            if(id!=0){
                                for(int i=0;i<json.getJSONArray("stats").length();i++){

                                    String returnText=AddStats.insertToDatabase(id,
                                            json.getJSONArray("stats").getJSONObject(i).getString("date_work"),
                                            json.getJSONArray("stats").getJSONObject(i).getString("date_add"),
                                            json.getJSONArray("stats").getJSONObject(i).getString("work_info"),
                                            json.getJSONArray("stats").getJSONObject(i).getString("sum_work"),
                                            json.getJSONArray("stats").getJSONObject(i).getString("sum_break"),
                                            json.getJSONArray("stats").getJSONObject(i).getString("file"),
                                            json.getJSONArray("stats").getJSONObject(i).getString("file_type"),
                                            Integer.parseInt(json.getJSONArray("stats").getJSONObject(i).getString("sum_road")),
                                            json.getJSONArray("stats").getJSONObject(i).getString("date_work_end"),
                                            json.getJSONArray("stats").getJSONObject(i).getString("time_work_start"),
                                            json.getJSONArray("stats").getJSONObject(i).getString("time_work_end")
                                            );
                                }
                                JOptionPane.showMessageDialog(null, "Przywrócono");
                                DatabaseConnection.exQuery("DELETE FROM trash WHERE id='"+d+"'");
                            }
                            try {
                                if (queryOutput2 != null) {
                                    queryOutput2.close();
                                }
                            } catch (Exception e) {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
