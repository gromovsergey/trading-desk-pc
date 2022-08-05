package app.programmatic.ui.angular;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AngularHelper {
    private static final String SRC_ANGULAR_BASE_PATH = "src/main/webapp/.base";
    private static final String PACKAGED_ANGULAR_BASE_PATH = "classpath:static/.base";

    public static ContentLocation getDevAngularIndexHtmlLocation() {
        ContentLocation tmp = getDevAngularFilesLocation();
        if (!tmp.exists()) {
            return tmp;
        }

        String indexPath = tmp.getPath() + "index.html";
        if (new DefaultResourceLoader().getResource(indexPath).exists()) {
            return new ContentLocation(indexPath, tmp.isOnFileSystem());
        }

        return new ContentLocation();
    }

    public static ContentLocation getDevAngularFilesLocation() {
        File srcBaseFile = new File(SRC_ANGULAR_BASE_PATH);
        if (srcBaseFile.exists()) {
            try (   FileInputStream is = new FileInputStream(srcBaseFile);
                    InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(isr)) {
                return new ContentLocation("file:///" +
                                           srcBaseFile.getParentFile().getAbsolutePath() + File.separator +
                                           reader.readLine().trim() + File.separator,
                                           true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Resource resource = new DefaultResourceLoader().getResource(PACKAGED_ANGULAR_BASE_PATH);
        if (resource.exists()) {
            try (   InputStreamReader isr = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(isr)) {
                return new ContentLocation("classpath:/static/" + reader.readLine().trim() + "/", false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new ContentLocation();
    }

    public static class ContentLocation {
        private final String path;
        private final boolean onFileSystem;
        private final boolean exists;

        public ContentLocation() {
            this.path = null;
            this.onFileSystem = false;
            this.exists = false;
        }

        public ContentLocation(String path, boolean isOnFileSystem) {
            this.path = path;
            this.onFileSystem = isOnFileSystem;
            this.exists = true;
        }

        public String getPath() {
            return path;
        }

        public boolean isOnFileSystem() {
            return onFileSystem;
        }

        public boolean exists() {
            return exists;
        }
    }
}
