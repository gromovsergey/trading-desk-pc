package com.foros.session.fileman;

import com.foros.model.fileman.FileInfo;

import java.io.*;

public abstract class ContentSourceSupport implements ContentSource {
    private long length;
    private String name;

    protected ContentSourceSupport(long length, String name) {
        this.length = length;
        this.name = name;
    }

    public long getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public static ContentSource create(final FileSystem fs, final String path) {
        FileInfo fileInfo = fs.getFileInfo(path);

        return new ContentSourceSupport(fileInfo.getLength(), fileInfo.getName()) {
            public InputStream getStream() throws IOException {
                return fs.readFile(path);
            }
        };
    }

    public static ContentSource create(final byte[] buf, String name) {
        return new ContentSourceSupport(buf.length, name) {
            public InputStream getStream() throws IOException {
                return new ByteArrayInputStream(buf);
            }
        };
    }

    public static ContentSource create(final File file) {
        FileInfo fileInfo = new FileInfo(file);

        return new ContentSourceSupport(fileInfo.getLength(), fileInfo.getName()) {
            public InputStream getStream() throws IOException {
                return new FileInputStream(file);
            }
        };
    }
}
