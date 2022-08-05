package com.foros.session.fileman;

public class ImageDimensionException extends FileManagerException implements FilePathException {

    private final String path;
    public ImageDimensionException(String fileName, String path, long threshold) {
        super("Can't to upload image '" + fileName + "' with dimensions more than " + threshold + " px");
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }
}
