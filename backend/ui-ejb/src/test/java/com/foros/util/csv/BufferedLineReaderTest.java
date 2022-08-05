package com.foros.util.csv;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

@Category( Unit.class )
public class BufferedLineReaderTest {
    @Test
    public void lineReader() throws IOException {
        BufferedLineReader reader = new BufferedLineReader(new StringReader("12\n3\n"));

        assertEquals('1', reader.peek());
        assertEquals('1', reader.read());
        assertEquals(0, reader.getLineNumber());
        assertFalse(reader.isEndOfLine());
        assertFalse(reader.isEndOfFile());

        assertEquals('2', reader.read());
        try {
            reader.peek();
            fail();
        } catch (IllegalStateException e) {
        }

        assertEquals(0, reader.getLineNumber());
        assertTrue(reader.isEndOfLine());
        assertFalse(reader.isEndOfFile());

        assertEquals("\n", reader.readSeparators());
        assertEquals(1, reader.getLineNumber());
        assertFalse(reader.isEndOfLine());
        assertFalse(reader.isEndOfFile());
        assertNull(reader.readSeparators());
        assertEquals(1, reader.getLineNumber());

        assertEquals('3', reader.peek());
        assertEquals('3', reader.read());
        assertEquals(1, reader.getLineNumber());
        assertTrue(reader.isEndOfLine());
        assertFalse(reader.isEndOfFile());

        reader.nextLine();
        assertTrue(reader.isEndOfLine());
        assertTrue(reader.isEndOfFile());
    }
}
