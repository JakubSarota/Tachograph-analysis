package com.example.tachographanalysis.database.driver.driverInfo;

import com.example.tachographanalysis.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class showData {
    static int idDriverData;
    public static int getIdDriverData(int id) {
        return idDriverData = id;
    }

    public static ObservableList<Data> data() throws SQLException {
        ObservableList<Data> dataList = FXCollections.observableArrayList();
        String query = "SELECT * FROM stats WHERE driver_id='"+idDriverData+"'";
        ResultSet queryOutput = DatabaseConnection.exQuery(query);

        try {
            while (queryOutput.next()) {
                Integer queryId = queryOutput.getInt("id");
                Integer queryDriverId = queryOutput.getInt("driver_id");
                String queryDateWork = queryOutput.getString("date_work");
                String queryWorkInfo = queryOutput.getString("work_info");
                String queryDateAdd = queryOutput.getString("date_add");
                String querySumWork = queryOutput.getString("sum_work");
                String querySumBreak = queryOutput.getString("sum_break");
                String queryFile = queryOutput.getString("file");
                String queryFileType = queryOutput.getString("file_type");
                Integer querySumRoad = queryOutput.getInt("sum_road");
                dataList.add(new Data(queryId, queryDriverId, queryDateWork, queryDateAdd, queryWorkInfo, querySumWork, querySumBreak, queryFile, queryFileType, querySumRoad));
            }
        } catch (Exception e) { }
        try {
            if(queryOutput!=null) {
                queryOutput.close();
            }
        } catch (Exception e) { }
//        System.out.println(dataList);
        return dataList;
    }
}
