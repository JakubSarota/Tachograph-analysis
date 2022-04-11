package com.example.tachographanalysis.database.driver.driverInfo;

import com.example.tachographanalysis.database.DatabaseConnection;
import com.example.tachographanalysis.database.driver.Driver;
import com.example.tachographanalysis.database.driver.driverInfo.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class showData {
    public static ObservableList<Data> data() throws SQLException {
        ObservableList<Data> dataList = FXCollections.observableArrayList();
        String query = "SELECT * FROM stats";
        ResultSet queryOutput = DatabaseConnection.exQuery(query);
        System.err.println(queryOutput);
        try {
            while (queryOutput.next()) {
                Integer queryDriverId = queryOutput.getInt("driver_id");
                String queryDataWork = queryOutput.getString("date_work");
                String queryDataAdd = queryOutput.getString("date_add");
                String querySumWork = queryOutput.getString("sum_work");
                String querySumBreak = queryOutput.getString("sum_break");
                String queryFile = queryOutput.getString("file");
                String queryFileType = queryOutput.getString("file_type");

                dataList.add(new Data(queryDriverId, queryDataWork, queryDataAdd, querySumWork, querySumBreak, queryFile, queryFileType));
                try {
                    if(queryOutput!=null) {
                        queryOutput.close();
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return dataList;
    }
}
