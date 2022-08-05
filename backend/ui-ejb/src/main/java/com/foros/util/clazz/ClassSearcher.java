package com.foros.util.clazz;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassSearcher {
    private static final Logger logger = Logger.getLogger(ClassSearcher.class.getName());

    private String rootPackageName;
    private boolean recursive;

    public ClassSearcher(String rootPackageName, boolean recursive) {
        this.recursive = recursive;
        this.rootPackageName = rootPackageName;
    }

    public Set<Class> search(ClassNameFilter classNameFilter, ClassFilter classFilter) throws Exception {
        Set<Class> classes = new HashSet<Class>();
        loadClasses(classNameFilter, classFilter, classes, rootPackageName);
        return classes;
    }

    public Set<Class> search(ClassFilter classFilter) throws Exception {
        Set<Class> classes = new HashSet<Class>();
        loadClasses(null, classFilter, classes, rootPackageName);
        return classes;
    }

    private void loadClasses(ClassNameFilter classNameFilter,
                             ClassFilter classFilter,
                             Set<Class> classes,
                             String pckgname) throws ClassNotFoundException, IOException {
        // Get a File object for the package
        File directory;
        String separator = System.getProperty("file.separator");
        String packageResourceName = pckgname.replace(".", separator);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urlEnumeration = classLoader.getResources(packageResourceName);
        while (urlEnumeration.hasMoreElements()) {
            String packagePath = urlEnumeration.nextElement().getFile();
            directory = new File(packagePath.replace("%20", " ").replace("%5c", separator));
            parseDirectory(classNameFilter, classFilter, classes, pckgname, directory);
        }
    }

    private void parseDirectory(ClassNameFilter classNameFilter, ClassFilter classFilter, Set<Class> classes,
                                String pckgname, File directory) throws ClassNotFoundException, IOException {
        if (directory.exists()) {
            // Get the list of the files contained in the package
            File[] files = directory.listFiles();
            for (File file : files) {
                // we are only interested in .class files
                String fileName = file.getName();
                if (fileName.endsWith(".class")) {
                    // removes the .class extension
                    String className = pckgname + '.' + fileName.substring(0, fileName.length() - 6);
                    try {
                        if (classNameFilter == null || classNameFilter.accept(className)) {
                            Class<?> clazz = Class.forName(className);
                            if (classFilter.accept(clazz)) {
                                classes.add(clazz);
                            }
                        }
                    } catch (Throwable ex) {
                        String msg = "Can't load class " + className + " from file " + file.getAbsolutePath();
                        logger.log(Level.SEVERE, msg);
                        throw new ClassNotFoundException(msg, ex);
                    }
                }

                if (file.isDirectory() && recursive) {
                    loadClasses(classNameFilter, classFilter, classes, pckgname + "." + fileName);
                }
            }
        }
    }
}
