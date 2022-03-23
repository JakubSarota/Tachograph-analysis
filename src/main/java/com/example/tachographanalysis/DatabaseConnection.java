package com.example.tachographanalysis;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getDBConnection(){

        String url = "jdbc:sqlite:D:/Programy/sqlite3/tachograph-analysis";
        try{
            databaseLink = DriverManager.getConnection(url);
            System.out.println("Connected to SQLITE!");

        }catch (Exception e){
            e.printStackTrace();
        }
        return databaseLink;
    }
}