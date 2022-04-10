package com.example.tachographanalysis.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowList {
    public static ObservableList<Driver.Drivers> driversList() throws SQLException {
        ObservableList<Driver.Drivers> driversList = FXCollections.observableArrayList();
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
                driversList.add(new Driver.Drivers(queryId, queryFirstName, querySecondName, queryLastName, queryEmail, queryPesel, queryCity, queryBorn, queryCountry, queryCard));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return driversList;
    }

}