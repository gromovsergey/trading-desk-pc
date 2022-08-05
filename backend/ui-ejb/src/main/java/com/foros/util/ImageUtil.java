package com.foros.util;

import com.foros.session.fileman.FileUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class ImageUtil {

    public static BufferedImage getScaledSaveProportions(BufferedImage img,
                                                         int maxWidth,
                                                         int maxHeight) {
        float originalWidth = img.getWidth();
        float originalHeight = img.getHeight();

        float widthRatio = originalWidth / maxWidth;
        float heightRatio = originalHeight / maxHeight;

        int width;
        int height;
        if (widthRatio > heightRatio) {
            width = maxWidth;
            height = (int) (originalHeight / originalWidth * maxWidth);
        } else {
            width = (int) (originalWidth / originalHeight * maxHeight);
            height = maxHeight;
        }

        BufferedImage bi = getScaled(img, width, height);
        bi = fillWithTransparent(bi, maxWidth, maxHeight);
        return bi;
    }

    private static BufferedImage getScaled(BufferedImage img,
                                          int targetWidth,
                                          int targetHeight) {
        BufferedImage result = img;
        int w = img.getWidth();
        int h = img.getHeight();

        if (w <= targetWidth && h <= targetHeight) {
            return result;
        }

        int imageType = (img.getTransparency() == Transparency.OPAQUE ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);

        do {
            if (w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage scaled = new BufferedImage(w, h, imageType);
            Graphics2D g2 = scaled.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(result, 0, 0, w, h, null);
            g2.dispose();
            result = scaled;
        } while (w != targetWidth || h != targetHeight);

        return result;
    }

    private static BufferedImage fillWithTransparent(BufferedImage img,
                                                     int targetWidth,
                                                     int targetHeight) {
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, targetWidth, targetHeight);
        g2.drawImage(img, (targetWidth - img.getWidth()) / 2, (targetHeight - img.getHeight()) / 2, null);
        g2.dispose();

        return scaled;
    }

    public static Dimension getImageDimensions(File file) throws IOException {
        Dimension result = null;
        String suffix = FileUtils.getExtension(file.getPath());
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);

        if (iter.hasNext()) {
            ImageInputStream stream = null;
            ImageReader reader = iter.next();
            try {
                stream = new FileImageInputStream(file);
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                result = new Dimension(width, height);
            } finally {
                if (stream != null) {
                    stream.close();
                }
                reader.dispose();
            }
        }

        return result;
    }
}