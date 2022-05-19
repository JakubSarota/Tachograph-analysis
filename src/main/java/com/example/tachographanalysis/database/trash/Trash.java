package com.example.tachographanalysis.database.trash;

import com.example.tachographanalysis.database.DatabaseConnection;
import com.example.tachographanalysis.database.driver.Driver;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Trash {
    public static void deleteUserWithData(Driver driver) throws SQLException {
        JSONObject json = new JSONObject();
        Map<String,String> map=new HashMap<String,String>();
        map.put("id",String.valueOf(driver.getId()));
        map.put("first_name",driver.getFname());
        map.put("second_name",driver.getSname());
        map.put("last_name",driver.getLname());
        map.put("email",driver.getEmail());
        map.put("pesel",driver.getPesel());
        map.put("city",driver.getCity());
        map.put("born_date",driver.getBorn());
        map.put("country",driver.getCountry());
        map.put("id_card",driver.getCard());
        map.put("license_drive",driver.getLCard());
        json.put("driver", map);
        String query = "SELECT * FROM stats WHERE driver_id='"+driver.getId()+"'";
        ResultSet queryOutput =  DatabaseConnection.exQuery(query);
        JSONArray jsonArray=new JSONArray();
        while (queryOutput.next()){
            Map<String,String> stats=new HashMap<String,String>();
            stats.put("date_work",queryOutput.getString("date_work"));
            stats.put("date_add",queryOutput.getString("date_add"));
            stats.put("work_info",queryOutput.getString("work_info"));
            stats.put("sum_work",queryOutput.getString("sum_work"));
            stats.put("sum_break",queryOutput.getString("sum_break"));
            stats.put("file",queryOutput.getString("file"));
            stats.put("file_type",queryOutput.getString("file_type"));
            stats.put("sum_road",queryOutput.getString("sum_road"));
            stats.put("date_work_end",queryOutput.getString("date_work_end"));
            stats.put("time_work_start",queryOutput.getString("time_work_start"));
            stats.put("time_work_end",queryOutput.getString("time_work_end"));
            jsonArray.put(stats);
        }
        json.put("stats",jsonArray);

        int status =  DatabaseConnection.exUpdate(
                "INSERT INTO trash (date_add, value)" +
                        " VALUES('" + LocalDate.now().toString() + "','" + json + "')");

        if(queryOutput!=null) {
            queryOutput.close();
        }
    }
    public static void deleteWhenTimeOut() throws SQLException {
        DatabaseConnection.exUpdate("DELETE FROM trash WHERE date_add <= DateTime('Now', 'LocalTime', '-30 Day')");
    }
}
