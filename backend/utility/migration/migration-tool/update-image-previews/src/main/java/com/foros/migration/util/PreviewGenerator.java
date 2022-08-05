package com.foros.migration.util;

import com.foros.model.template.OptionValueUtils;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.SharedFileOutputStream;
import com.foros.session.fileman.SimplePathProvider;
import com.foros.session.textad.TextAdImageUtil;
import com.foros.util.ImageUtil;
import org.apache.commons.io.DirectoryWalker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

public class PreviewGenerator extends DirectoryWalker {
    private static final Logger logger = Logger.getLogger(PreviewGenerator.class.getName());

    private static final List<String> IMAGE_TYPES = Arrays.asList("bmp", "gif", "jpeg", "png");

    private PathProvider resizedPathProvider;
    private FilePath path = new FilePath();

    public PreviewGenerator() {
        super(new ExcludeTempFileFilter(), -1);
    }

    @Override
    protected void handleDirectoryStart(File directory, int depth, Collection results) throws IOException {
        path.push(directory.getName());
    }

    @Override
    protected void handleFile(File file, int depth, Collection results) {
        String resizedName = TextAdImageUtil.getResizedFileName(file.getName());
        File resized = resizedPathProvider.getNested(path.getString(), OnNoProviderRoot.AutoCreate).getPath(resizedName);

        createPreview(file, resized);
    }

    @Override
    protected void handleDirectoryEnd(File directory, int depth, Collection results) throws IOException {
        path.pop();
    }

    public void walk(File dir) throws IOException {
        resizedPathProvider = new SimplePathProvider(dir, IMAGE_TYPES)
                .getNested(OptionValueUtils.IMAGE_RESIZED_FOLDER, OnNoProviderRoot.AutoCreate);
        walk(dir, new ArrayList());
    }

    private void createPreview(File imageFile, File resizedFile) {
        if (!resizedFile.exists() || resizedFile.lastModified() < imageFile.lastModified()) {
            try {
                BufferedImage bi = ImageIO.read(imageFile);
                if (bi == null) {
                    logger.warning("Unable to create preview for image file: " + imageFile.getPath() + ". Unsupported Image Type.");
                    return;
                }
                BufferedImage resizedBi = ImageUtil.getScaledSaveProportions(bi, 110, 80);
                ImageIO.write(resizedBi, "png", resizedFile);
            } catch (Exception e) {
                logger.severe("Unable to create preview for image file: " + imageFile.getPath() + ". " + e.getMessage());
            }
        }
    }

    private static class FilePath extends Stack<String> {
        private String getString() {
            String res = "";
            Iterator<String> iterator = this.iterator();
            iterator.next(); // first level is excessive
            while (iterator.hasNext()) {
                res += "/" + iterator.next();
            }
            return res;
        }
    }

    private static class ExcludeTempFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            if (SharedFileOutputStream.isTempFileName(pathname.getName()) ||
                    pathname.getName().equals(OptionValueUtils.IMAGE_RESIZED_FOLDER) ||
                    pathname.getName().equals(OptionValueUtils.HTML_FOLDER)) {
                return false;
            }
            return true;
        }
    }
}
