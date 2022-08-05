package com.foros.util.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

/**
 *
 * @author oleg_roshka
 */
public class ResourceHelper {
    private ResourceHelper() {
    }

    public static InputStream getResourceAsStream(String resourcePath) throws IOException {
        if (resourcePath == null) {
            throw new IllegalArgumentException("argument resourcePath must be defined");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(resourcePath);
    }

    public static File getResourceAsFile(String resourcePath) throws IOException {
        if (resourcePath == null) {
            throw new IllegalArgumentException("argument resourcePath must be defined");
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(resourcePath);
        File file = new File(url.getFile().replaceAll("%20", " "));

        return file;
    }

    public static String readContent(String resourcePath) throws IOException {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        String result;

        try {
            fileReader = new FileReader(getResourceAsFile(resourcePath));
            bufferedReader = new BufferedReader(fileReader);
            StringWriter stringWriter = new StringWriter();

            while (bufferedReader.ready()) {
                stringWriter.write(bufferedReader.read());
            }

            StringBuffer buffer = stringWriter.getBuffer();
            result = buffer.toString();
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }

            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return result;
    }
}
