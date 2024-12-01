package com.george.meterdatareader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class MeterDataReaderTest {

    @Test
    void testCalculateThirtyMinuteConsumption() {
        // Input string with dummy data for testing
        String[] input = new String[99];
        for (int i = 0; i < input.length; i++) {
            input[i] = "1.5"; // Assign each element with the value 1.5
        }

        // Expected value: 48 slots * 1.5 = 72.0
        double expected = 48 * 1.5;

        // Call the method to test
        double result = MeterDataReader.calculateThirtyMinuteConsumption(input);

        // Assert that the result matches the expected value
        assertEquals(expected, result, "The calculated consumption should match the expected value.");
    }

    @Test
    void testCalculateThirtyMinuteConsumptionEmpty() {
        String[] input = null;
        assertThrows(IllegalArgumentException.class, () -> {
            MeterDataReader.calculateThirtyMinuteConsumption(input);
        });
    }

    @Test
    public void testConvertToDate() {
        String inputString = "20241130";
        Date result = MeterDataReader.convertToDate(inputString);

        LocalDate expectedLocalDate = LocalDate.parse(inputString, DateTimeFormatter.ofPattern("yyyyMMdd"));
        Date expectedDate = Date.valueOf(expectedLocalDate);
        assertEquals(expectedDate, result, "The date conversion did not work as expected.");
    }

    @Test
    public void testConvertToDateWithInvalidFormat() {

        String inputString = "2024-11-30"; // Incorrect format, should be "yyyyMMdd"

        try {
            MeterDataReader.convertToDate(inputString);
        } catch (Exception e) {
            assertEquals("Text '2024-11-30' could not be ", e.getMessage().substring(0, 31));
        }
    }

    @Test
    public void testReadFileAndParseCSVFileNotFound() {
        String invalidFilePath = "non_existent_file.csv";
        
        List<String[]> result = MeterDataReader.readFileAndParseCSV(invalidFilePath);
        
        assertTrue(result.isEmpty(), "The result should be empty for a non-existent file.");
    }
}
