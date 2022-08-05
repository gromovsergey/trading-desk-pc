package com.foros.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class ResourceUtil {
    /**
     * Reads the contents of given InputStream.
     * The stream is read until EOF is reaches
     * NOTE: the method does not close the stream after reading
     * @param is - input stream to be read
     * @return - the String representation of data read from stream
     * @throws IOException - Exception is thrown when I/O error occurs while reading the stream
     */
    public static String readStreamToString(InputStream is) throws IOException {
        int count = 0;
        byte[] result = new byte[1024];

        do {
            int nextByte = is.read();

            if (nextByte == -1) {
                break;
            }

            if (count < result.length) {
                result[count++] = (byte) nextByte;
            } else {
                byte[] oldResult = result;
                result = new byte[2 * result.length];

                System.arraycopy(oldResult, 0, result, 0, oldResult.length);
                result[count++]= (byte) nextByte;
            }
        } while (true);

        return new String(result, 0, count, "UTF-8");
    }

    public static byte[] readStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
        try {
            IOUtils.copy(is, os);
            return os.toByteArray();
        } finally {
            IOUtils.closeQuietly(os);
        }
    }

}
