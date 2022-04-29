package com.example.tachographanalysis.database.driver;

import com.example.tachographanalysis.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowList {
    public static ObservableList<Driver> driversList() throws SQLException {
        ObservableList<Driver> driversList = FXCollections.observableArrayList();
        driversList.clear();
        String query = "SELECT * FROM driver";
        ResultSet queryOutput = DatabaseConnection.exQuery(query);

        try {
            while (queryOutput.next()) {
                Integer queryId = queryOutput.getInt("id");
                String queryFirstName = queryOutput.getString("first_name");
                String querySecondName = queryOutput.getString("second_name");
                String queryLastName = queryOutput.getString("last_name");
                String queryEmail = queryOutput.getString("email");
                String queryPesel = queryOutput.getString("pesel");
                String queryCity = queryOutput.getString("city");
                String  queryBorn = queryOutput.getString("born_date");
                String queryCountry = queryOutput.getString("country");
                String queryCard = queryOutput.getString("id_card");
                String queryLCard = queryOutput.getString("license_drive");
                driversList.add(new Driver(queryId, queryFirstName, querySecondName,
                        queryLastName, queryEmail, queryPesel, queryCity, queryBorn,
                        queryCountry, queryCard,queryLCard));

            }
        } catch (Exception e) { }
        try {
            if(queryOutput!=null) {
                queryOutput.close();
            }
        } catch (Exception e) { }
//        System.out.println(driversList);
        return driversList;
    }

}