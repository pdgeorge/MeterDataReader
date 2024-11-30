package com.george.meterdatareader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.UUID;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MeterDataReader {
    public static void main(String[] args) {

        String sample_nmi = "1234567890";
        String sample_timestamp = "2024-11-28 14:30:00";
        double sample_consumption = 150.75;

        saveToDB(sample_nmi, sample_timestamp, sample_consumption);
    }

    public static void saveToDB(String nmi, String timestamp, double consumption) {
        Dotenv dotenv = Dotenv.load();
        String dbPassword = dotenv.get("DB_PASSWORD");
        String dbUser = dotenv.get("DB_USER");

        String url = "jdbc:postgresql://localhost:5432/postgres";

        String insertSQL = "INSERT INTO meter_readings (nmi, timestamp, consumption) VALUES(?, ?, ?);";

        try {
            // Explicitly register the PostgreSQL driver
            Class.forName("org.postgresql.Driver");

            try (
                Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
                PreparedStatement statement = connection.prepareStatement(insertSQL)
            ) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date parsedDate = dateFormat.parse(timestamp);
                Timestamp parsed_timestamp = new Timestamp(parsedDate.getTime());

                statement.setString(1, nmi);
                statement.setTimestamp(2, parsed_timestamp);
                statement.setDouble(3, consumption);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("A new row has been inserted successfully.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}