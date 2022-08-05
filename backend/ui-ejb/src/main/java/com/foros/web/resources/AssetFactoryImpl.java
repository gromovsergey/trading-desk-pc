package com.foros.web.resources;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class AssetFactoryImpl implements AssetFactory {

    private static final Logger logger = Logger.getLogger(AssetFactoryImpl.class.getName());

    private ServletContext servletContext;
    private String context;
    private String contentType;

    public AssetFactoryImpl(ServletContext servletContext, String context, String contentType) {
        this.servletContext = servletContext;
        this.context = context;
        this.contentType = contentType;
    }

    @Override
    public Asset createAsset(String name, long version) {
        String realPath = servletContext.getRealPath(context + "/" + name);

        // todo: check for ..
        if (realPath.contains("..")) {
            return null;
        }

        File file = new File(realPath);

        if (!file.exists()) {
            logger.warning("Asset " + name + " creation failed, file not found: " + file.getAbsolutePath());

            return null;
        } else if (file.isDirectory()) {
            if (needToConsolidate(file)) {
                try {
                    logger.info("Consolidating asset: " + name);

                    return new BufferedAsset(name, version, contentType, consolidate(file));
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Can't create asset " + name, e);
                    return null;
                }
            } else {
                logger.warning("Directory without consolidation metainfo: " + name);
                return null;
            }
        } else {
            logger.info("Creating simple asset: " + name);

            return new AssetImpl(name, version, contentType, file);
        }
    }

    private File getOrderFile(File file) {
        return new File(file, "order.txt");
    }

    private boolean needToConsolidate(File file) {
        return getOrderFile(file).exists();
    }

    private byte[] consolidate(File file) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        consolidate(file, result);
        return result.toByteArray();
    }

    private void consolidate(File source, OutputStream target) throws IOException {
        List<String> fileNames = FileUtils.readLines(getOrderFile(source));

        BufferedOutputStream out = new BufferedOutputStream(target);

        try {
            for (String fileName : fileNames) {
                out.write(("\n\n/*//LFLF// " + fileName + " LFLF*/\n").getBytes());
                FileInputStream in = new FileInputStream(new File(source, fileName));

                try {
                    IOUtils.copy(in, out);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}
