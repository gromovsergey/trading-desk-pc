package com.foros.resource;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileWriterRegistry implements Closeable {

    private Map<String, FileWriter> registry = new HashMap<String, FileWriter>();

    public FileWriter get(File file) throws IOException {
        FileWriter writer = registry.get(file.getAbsolutePath());

        if (writer == null) {
            writer = new FileWriter(file);
            registry.put(file.getAbsolutePath(), writer);
        }

        return writer;
    }

    public void close() throws IOException {
        for (FileWriter writer : registry.values()) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

}
