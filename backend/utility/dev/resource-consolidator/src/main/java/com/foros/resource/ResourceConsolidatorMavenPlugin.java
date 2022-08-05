package com.foros.resource;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal consolidate
 */
public class ResourceConsolidatorMavenPlugin extends AbstractMojo {

    private static ValueStack valueStack = new MockStack();

    /**
     * @parameter
     */
    private String resourcesDirectory;
    private File resources;

    /**
     * @parameter
     */
    private String outputDirectory;
    private File output;

    /**
     * @parameter
     */
    private String consolidateFileNamePrefix;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        processParameters();

        removeOldFiles();

        try {
            getLog().info("[ Consolidating resources: " + resourcesDirectory +" -> " + outputDirectory + "/" + consolidateFileNamePrefix + "_*.properties ]");

            File[] resources = findResources();

            getLog().info("Found " + resources.length + " resources.");

            int processed = processResources(resources);

            getLog().info(processed + " resource lines was bundled.");

            removeResourcesDirectory();

            // creating file for default en locale, this file is to skip resource for system locale when user locale is en_XX
            // standart resource finding algorithm is: resource for user locale, resource for system locale, default resource
            // this resource may be empty - US english is default resource, see applicationResource.properties
            createDefaultResource();

            getLog().info("[ consolidating done ]");
        } catch (Exception e) {
            throw new MojoExecutionException("Can't process resources.", e);
        }
    }

    private void removeResourcesDirectory() {
        if (this.resources.list().length == 0) {
            if (this.resources.delete()) {
                getLog().info("Source resources directory " + resourcesDirectory + " is empty and will be removed.");
            }
        }
    }

    private void removeOldFiles() throws MojoExecutionException {
        try {
            File[] files = output.listFiles(new OldResourcesFilenameFilter());

            if (files == null) {
                throw new IOException();
            }

            if (files.length > 0) {
                for (File file : files) {
                    if (!file.delete()) {
                        throw new IOException("Can't delete file: " + file.getAbsolutePath());
                    } else {
                        getLog().debug("Removed old resource file: " + file.getName());
                    }
                }

                getLog().info("Removed " + files.length + " old resource files.");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Removing old files failed.", e);
        }
    }

    private void processParameters() throws MojoExecutionException {
        assertNotNull(consolidateFileNamePrefix, "consolidateFileNamePrefix");

        assertNotNull(resourcesDirectory, "resourcesDirectory");
        this.resources = new File(resourcesDirectory);
        assertDirectory(this.resources);

        assertNotNull(outputDirectory, "outputDirectory");
        this.output = new File(outputDirectory);
        this.output.mkdirs();
    }

    private File[] findResources() {
        return resources
                .listFiles(new ResourcesFileFilter());
    }

    private void createDefaultResource() throws IOException {
        File resource = createLocaledResource(consolidateFileNamePrefix, "en");

        if (!resource.createNewFile()) {
            throw new IOException("Can't create default resource: " + resource.getAbsolutePath());
        } else {
            getLog().info("Created default resource: " + resource.getName());
        }
    }

    private int processResources(File[] resources) throws IOException {
        int counter = 0;

        FileWriterRegistry fileWriterRegistry = new FileWriterRegistry();
        try {
            for (File resourceFile : resources) {
                counter += processResource(fileWriterRegistry, resourceFile);
            }
        } finally {
            IOUtils.closeQuietly(fileWriterRegistry);
        }

        return counter;
    }

    private int processResource(FileWriterRegistry fileWriterRegistry, File resourceFile) throws IOException {
        int counter = 0;

        String locale = fetchLocale(resourceFile.getName());

        File outputFile = createLocaledResource(consolidateFileNamePrefix, locale);

        FileReader source = new FileReader(resourceFile);
        FileWriter target = fileWriterRegistry.get(outputFile);

        getLog().debug("\t" + resourceFile.getName() + " -> " + outputFile.getName());

        try {
            Iterator<String> iterator = IOUtils.lineIterator(source);

            while (iterator.hasNext()) {
                String result = prepare(locale, iterator.next());

                if (result != null) {
                    target.write(result + "\n");
                    counter++;
                }
            }
        } finally {
            target.flush();
            IOUtils.closeQuietly(source);
        }

        if (!resourceFile.delete()) {
            throw new IOException("Can't delete resource " + resourceFile.getAbsolutePath());
        }

        return counter;
    }

    private String prepare(String locale, String line) {
        if (line.startsWith("#") || line.trim().isEmpty()) {
            return null;
        }

        String result = formatDecimals(locale, line);

        if (hasParameters(result)) {
            result = line.replaceAll("[']", "''");
        }

        return result;
    }

    private boolean hasParameters(String line) {
        // remove Struts 2 params
        line = TextParseUtil.translateVariables(line, valueStack);
        // remove quotes to avoid MessageFormat quote rules
        line = line.replace("'", "");
        try {
            return new MessageFormat(line).getFormats().length > 0;
        } catch (RuntimeException e) {
            getLog().debug("Can't process line: " + line);
            throw e;
        }
    }

    private File createLocaledResource(String name, String locale) {
        return new File(this.output.getAbsolutePath()
                + File.separator
                + name
                + (locale != null ? "_" + locale : "")
                + ".properties");
    }

    private String fetchLocale(String fileName) {
        int start = fileName.indexOf('_');
        int end = fileName.lastIndexOf(".properties");

        return start != -1 ? fileName.substring(start + 1, end) : null;
    }

    private void assertNotNull(String parameter, String name) throws MojoExecutionException {
        if (parameter == null) {
            throw new MojoExecutionException("Please specify " + name + " parameter");
        }
    }

    private void assertDirectory(File directory) throws MojoExecutionException {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new MojoExecutionException(directory + " is not a directory");
        }
    }

    private String formatDecimals(String localeString, String line) {
        if (line.matches(".*\\$\\{number\\(.*\\)\\}.*")) {

            Locale locale = createLocaleByString(localeString);

            char decimalSeparator = new DecimalFormatSymbols(locale).getDecimalSeparator();

            Set<String> args = fetchNumberExpressionArgs(line);

            for (String arg : args) {
                if (arg.matches("[0-9]+\\.[0-9]+")) {
                    BigDecimal decimal = parseBigDecimal(arg);

                    int scale = arg.length() - arg.indexOf('.') - 1;
                    NumberFormat nf = NumberFormat.getInstance(locale);
                    nf.setMinimumFractionDigits(scale);

                    line = line.replaceAll("\\$\\{number\\(" + arg.replaceAll("\\.", "\\\\.") + "\\)\\}", nf.format(decimal));
                } else if (arg.matches("\\#+\\.\\#+")) {
                    line = line.replaceAll("\\$\\{number\\(" + arg.replaceAll("\\.", "\\\\.") + "\\)\\}", arg.replace('.', decimalSeparator));
                } else {
                    throw new RuntimeException("Incorrect decimal parameter in: " + arg);
                }
            }
        }

        return line;
    }

    private Set<String> fetchNumberExpressionArgs(String line) {
        Set<String> args = new HashSet<String>();

        int start = 0, end = 0;

        do {
            start = line.indexOf("${number(", end);
            end = line.indexOf(")}", start);
            if (start > 0 && end > 0) {
                args.add(line.substring(start + 9, end));
            }
        } while (start > 0);

        return args;
    }

    private BigDecimal parseBigDecimal(String arg) {
        BigDecimal decimal;
        try {
            decimal = new BigDecimal(arg);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Incorrect decimal parameter in: " + arg, e);
        }
        return decimal;
    }

    private Locale createLocaleByString(String localeString) {
        if (localeString == null || localeString.isEmpty()) {
            return new Locale("en");
        }

        return new Locale(localeString);
    }

    private class ResourcesFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            String name = file.getName();

            if (!name.endsWith(".properties") || name.startsWith("ValidationMessages") || file.isDirectory()) {
                getLog().warn("File " + name + " is skipped.");
                return false;
            }

            return true;
        }

    }

    private class OldResourcesFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith(consolidateFileNamePrefix);
        }
    }

}
