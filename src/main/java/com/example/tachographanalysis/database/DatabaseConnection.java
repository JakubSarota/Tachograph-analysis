package com.example.tachographanalysis.database;

import java.sql.*;

public class DatabaseConnection {
    public Connection databaseLink;
    public Connection getDBConnection(){
        String url = "jdbc:sqlite:src/main/resources/com/example/tachographanalysis/tachograph-analysis";
        try{
            databaseLink = DriverManager.getConnection(url);
        }catch (Exception e){
            e.printStackTrace();
        }
        return databaseLink;
    }
    public static ResultSet exQuery(String query) throws SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getDBConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs;
    }

    public static int exUpdate(String query) throws SQLException {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getDBConnection();
        Statement stmt = connection.createStatement();
        int rs = stmt.executeUpdate(query);
        return rs;
    }
}