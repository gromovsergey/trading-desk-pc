package com.foros.util;

import com.foros.util.csv.CsvReader;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

@Category( Unit.class )
public class CsvTest {
    @Test
    public void channelListLoad() throws Exception {
        String csvData = "Audi,,\"Audi AND ANY A4 A5 RS6\"\nBMW,\"\"\"BMW X5\"\",\n\"\"BMW X3\"\"\",\"BWM AND ANY X5 X3\"";
        CsvReader csvReader = new CsvReader(new StringReader(csvData));

        csvReader.readRecord();
        int columnCount = csvReader.getColumnCount();

        assertEquals(3, columnCount);
        assertEquals("Audi", csvReader.get(0));
        assertEquals("", csvReader.get(1));
        assertEquals("Audi AND ANY A4 A5 RS6", csvReader.get(2));

        csvReader.readRecord();
        columnCount = csvReader.getColumnCount();
        assertEquals(3, columnCount);
        assertEquals("BMW", csvReader.get(0));
        assertEquals("\"BMW X5\",\n\"BMW X3\"", csvReader.get(1));
        assertEquals("BWM AND ANY X5 X3", csvReader.get(2));
    }
}
