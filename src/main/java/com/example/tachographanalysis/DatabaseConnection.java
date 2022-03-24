package com.example.tachographanalysis;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getDBConnection(){

        String url = "jdbc:sqlite:src/main/resources/com/example/tachographanalysis/tachograph-analysis";
        try{
            databaseLink = DriverManager.getConnection(url);
            System.out.println("Connected to SQLITE!");

        }catch (Exception e){
            e.printStackTrace();
        }
        return databaseLink;
    }
}