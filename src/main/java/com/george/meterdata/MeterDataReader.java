package com.george.meterdatareader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.UUID;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.sql.Timestamp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MeterDataReader {
    public static void main(String[] args) {

        // Need to automate retrieval of file.
        String filePath = "src/main/resources/nem12sampledata.txt";
        List<String[]> FileContents = readFileAndParseCSV(filePath);

        List<MeterData> meterDataList = prepareMeterDataList(FileContents);

        for (MeterData item : meterDataList) {
            saveToDB(item.getNmi(), item.getTimestamp(), item.getConsumption());
        }
    }

    public static List<String[]> readFileAndParseCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Split the line by commas, and store the resulting array
                String[] values = line.split(",");
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static List<MeterData> prepareMeterDataList(List<String[]> receivedMeterDataList) {

        //TODO add in validation to ensure file and contents are valid

        List<MeterData> dataList = new ArrayList();

        String tempNmi = "";
        Date tempTimestamp = new Date(1);
        double tempConsumption = 0.0;
        String intervalFlag = "None";

        for (String[] row : receivedMeterDataList) {
            if(row[0].equals("200")) {
                try {
                    if (row[1] != tempNmi && tempNmi != "") {
                        MeterData meterData = new MeterData(tempNmi, tempTimestamp, tempConsumption);
                        dataList.add(meterData);
                        tempNmi = "";
                        tempTimestamp = null;
                        tempConsumption = 0.0;
                        intervalFlag = "None";
                    }
                    tempNmi = row[1];
                    tempTimestamp = convertToDate(row[9]);
                    intervalFlag = row[8];
                } catch (Exception e) {
                    System.out.println("An exception occurred attempting to assign nmi: " + e.getMessage());
                }
            }
            
            if(row[0].equals("300")) {
                try {
                    if(intervalFlag.equals("30")) {
                        tempConsumption += calculateThirtyMinuteConsumption(row);
                    }
                } catch (Exception e) {
                    System.out.println("An exception occurred attempting to calculate consumption: " + e.getMessage());
                }
            }
        }
        MeterData meterData = new MeterData(tempNmi, tempTimestamp, tempConsumption);
        dataList.add(meterData);

        return dataList;
    }

    public static double calculateThirtyMinuteConsumption(String[] inputString) {
        if (inputString == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }

        Double runningTotal = 0.0;

        int startIndex = 2;
        int endIndex = startIndex + 47;

        for (int i = startIndex; i <= endIndex; i++) {
            runningTotal += Double.parseDouble(inputString[i]);
        }

        return runningTotal;
    }

    public static Date convertToDate(String inputString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(inputString, formatter);
        Date date = Date.valueOf(localDate);

        return date;
    }

    public static void saveToDB(String sqlNmi, Date sqlDate, double sqlConsumption) {
        Dotenv dotenv = Dotenv.load();
        String dbPassword = dotenv.get("DB_PASSWORD");
        String dbUser = dotenv.get("DB_USER");
        String dbUrl = dotenv.get("DB_URL");
        String dbDb = dotenv.get("DB_DB");

        String url = "jdbc:"+dbUrl+"/"+dbDb;

        String insertSQL = "INSERT INTO meter_readings (nmi, timestamp, consumption) VALUES(?, ?, ?);";

        try {
            // Explicitly register the PostgreSQL driver
            Class.forName("org.postgresql.Driver");

            try (
                Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
                PreparedStatement statement = connection.prepareStatement(insertSQL);
            ) {
                statement.setString(1, sqlNmi);
                statement.setDate(2, sqlDate);
                statement.setDouble(3, sqlConsumption);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("A new row has been inserted successfully.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public static string 
}