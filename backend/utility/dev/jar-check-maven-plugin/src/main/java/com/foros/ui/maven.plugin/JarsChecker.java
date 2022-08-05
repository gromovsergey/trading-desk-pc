package com.foros.ui.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;


/**
 * @goal check
 * @phase verify
 */
public class JarsChecker extends AbstractMojo
{
    private static Pattern javaArtifactIdPattern = Pattern.compile("^(.+?)-[\\d.-]{3}.*\\.[wej]ar$|^(.*?)\\.[wej]ar$");
    private static String[] extensions = { "jar", "war", "ear" };

    /**
     * @parameter
     */
    private String target;

    /**
     * @parameter
     */
    private String targetDirectory;

    private boolean stopBuild = false;
    private List<String> extractedDirectories = new LinkedList<>();
    private Map<String, JarEntryInfo> jarEntriesInfo = new HashMap<>();

    @Override
    public void execute() throws MojoExecutionException
    {
        getLog().info("JARs will be searched in following locations: target=" + target + ", targetDirectory=" + targetDirectory);

        try {
            Map<String, List<String>> includedJars = new HashMap<>();
            if (target != null) {
                checkIncludedJars(includedJars, target, "");
            }
            if (targetDirectory != null) {
                checkIncludedJarsInDirectory(includedJars, targetDirectory, "");
            }

            for (Map.Entry<String, List<String>> entry: includedJars.entrySet()) {
                List<String> sameJars = entry.getValue();
                if (sameJars.size() == 1 || haveDifferentClasses(sameJars, entry.getKey())) {
                    continue;
                }
                if (!stopBuild) {
                    stopBuild = true;
                    getLog().error("Several identical JARs were found");
                }
                StringBuilder entriesMsg = new StringBuilder();
                for (String jar: sameJars) {
                    entriesMsg.append(jar);
                    entriesMsg.append(" = ");
                }
                getLog().error(entriesMsg.toString());
            }
        } catch (Exception e) {
            stopBuild = true;
            getLog().error(e.toString());
        } finally {
            for (String directory: extractedDirectories) {
                new File(directory).delete();
            }
        }

        if (stopBuild) {
            throw new MojoExecutionException("Please fix errors listed above");
        }
    }

    private void checkIncludedJars(Map<String, List<String>> includedJars, String archiveName, String pathPrefix) throws ZipException, IOException {
        getLog().debug("Processing WAR/EAR file: " + archiveName);

        ZipFile targetArchive = new ZipFile(archiveName);
        try {
            Enumeration<? extends ZipEntry> entries = targetArchive.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                Path zipEntryFullPath = Paths.get(zipEntry.getName());
                String zipEntryFileName = zipEntryFullPath.getFileName().toString();

                if (zipEntry.getName().endsWith(".war") || zipEntry.getName().endsWith(".ear")) {
                    processWar(includedJars, archiveName, pathPrefix, zipEntry.getName(), zipEntryFileName);
                } else if (zipEntry.getName().endsWith(".jar")) {
                    processJar(includedJars, archiveName, pathPrefix, zipEntry.getName(), zipEntryFileName);
                }
            }
        } finally {
            targetArchive.close();
        }
    }

    static private String joinPath(String path1, String path2) {
        return path1.isEmpty() ? path2 : path2.isEmpty() ? path1 : path1 + File.separator + path2;
    }

    private void processWar(Map<String, List<String>> includedJars, String archiveName, String pathPrefix,
            String zipEntryRelativePath, String zipEntryFileName) throws ZipException, IOException {
        String newArchiveDirectoryPath = Paths.get(archiveName).getParent().toString();
        String newArchiveFileName = zipEntryFileName + "-" + System.currentTimeMillis();
        String newArchivePath = joinPath(newArchiveDirectoryPath, newArchiveFileName);
        new net.lingala.zip4j.core.ZipFile(archiveName).extractFile(zipEntryRelativePath, newArchiveDirectoryPath, null, newArchiveFileName);
        try {
            checkIncludedJars(includedJars, newArchivePath, joinPath(pathPrefix, zipEntryFileName));
        } finally {
            extractedDirectories.add(newArchivePath);
        }
    }

    private void processJar(Map<String, List<String>> includedJars, String archiveName, String pathPrefix,
            String zipEntryRelativePath, String zipEntryFileName) throws ZipException, IOException {
        String jarRelativePath = joinPath(pathPrefix, zipEntryRelativePath);
        getLog().debug( "Jar is found: " + jarRelativePath);

        Matcher matcher = javaArtifactIdPattern.matcher(zipEntryFileName);
        if (!matcher.matches()) {
            getLog().error("Unexpected JAR name format: " + jarRelativePath);
            stopBuild = true;
            return;
        }

        String key = (matcher.group(1) != null ? matcher.group(1) : matcher.group(2));
        List<String> sameEntries = includedJars.get(key);
        if (sameEntries == null) {
            sameEntries = new ArrayList<>();
            includedJars.put(key, sameEntries);
        }
        sameEntries.add(jarRelativePath);
        jarEntriesInfo.put(key + jarRelativePath, new JarEntryInfo(archiveName, zipEntryRelativePath, zipEntryFileName));
    }

    private void checkIncludedJarsInDirectory(Map<String, List<String>> includedJars, String directoryPath, String pathPrefix) throws ZipException, IOException {
        getLog().debug( "Processing directory: " + directoryPath);

        Collection<File> files = FileUtils.listFiles(new File(directoryPath), extensions, true);

        for (File file: files) {
            if (file.getName().endsWith(".war") || file.getName().endsWith(".ear")) {
                processWar(includedJars, directoryPath, pathPrefix, file.getPath(), file.getName());
            } else if (file.getName().endsWith(".jar")) {
                processJar(includedJars, null, pathPrefix, file.getPath(), file.getName());
            }
        }
    }

    private boolean haveDifferentClasses(List<String> jarEntries, String jarKey) throws ZipException, IOException {
        Set<String> allClasses = new HashSet<>();

        for (String jarEntry: jarEntries) {
            List<String> jarClasses = findAllClassesInJar(jarEntriesInfo.get(jarKey + jarEntry));

            for (String jarName: jarClasses) {
                if (allClasses.contains(jarName)) {
                    return false;
                }
                allClasses.add(jarName);
            }
        }

        if (getLog().isDebugEnabled()) {
            StringBuilder msg = new StringBuilder();
            for (String jarEntry: jarEntries) {
                if (msg.length() == 0) {
                    msg.append("Following JARs with the same name contain different classes, so ignoring them: ");
                } else {
                    msg.append(", ");
                }
                msg.append(jarEntry);
            }
            getLog().debug(msg.toString());
        }

        return true;
    }

    private List<String> findAllClassesInJar(JarEntryInfo jarInfo) throws ZipException, IOException {
        String jarPath = jarInfo.extract();
        List<String> classes = new LinkedList<>();
        ZipFile jarArchive = new ZipFile(jarPath);
        try {
            Enumeration<? extends ZipEntry> entries = jarArchive.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();

                if (zipEntry.getName().endsWith(".class")) {
                    classes.add(zipEntry.getName());
                }
            }
        } finally {
            jarArchive.close();
        }

        return classes;
    }

    private class JarEntryInfo {
        private String archiveName;
        private String zipEntryRelativePath;
        private String zipEntryFileName;

        private JarEntryInfo(String archiveName, String zipEntryRelativePath, String zipEntryFileName) {
            this.archiveName = archiveName;
            this.zipEntryRelativePath = zipEntryRelativePath;
            this.zipEntryFileName = zipEntryFileName;
        }

        /**
         * @return real path to extracted directory
         */
        public String extract() throws ZipException {
            String jarArchiveFilePath = archiveName == null ? zipEntryRelativePath : null;
            if (archiveName != null) {
                String jarArchiveDirectoryPath = Paths.get(archiveName).getParent().toString();
                String jarArchiveFileName = zipEntryFileName + "-" + System.currentTimeMillis();
                jarArchiveFilePath = joinPath(jarArchiveDirectoryPath, jarArchiveFileName);
                new net.lingala.zip4j.core.ZipFile(archiveName).extractFile(zipEntryRelativePath, jarArchiveDirectoryPath, null, jarArchiveFileName);
                extractedDirectories.add(jarArchiveFilePath);
            }
            return jarArchiveFilePath;
        }
    }
}
