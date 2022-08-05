package com.foros.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class ResourceHelper {
    /**
     * Find duplicates and not deleted keys for the given language in the given dir.
     *
     * @param dir directory to validate
     * @return true if validation passed, false in contrary
     * @throws java.io.IOException
     */
    public static boolean validateResources(String dir, Args args) throws IOException {
        File fdir = new File(dir);
        return validateDuplicates(fdir, args.getLang())
                && validateDuplicates(fdir, Language.EN)
                && findNotUsedKeys(fdir, args.getLang(), args.getOut());
    }

    /**
     * Find duplicates.
     * Resource is duplicated if its key occurs more then one time.
     *
     * @param dir directory to validate
     * @return true if there are no duplicates, false in contrary
     * @throws java.io.IOException
     */
    private static boolean validateDuplicates(File dir, Language lang) throws IOException {
        File[] fileList = dir.listFiles(filterForLang(lang));
        Map map = new HashMap();
        int duplicates = 0;

        for (File f : fileList) {
            ArrayList<String> rows = readFileContent(f);
            for (String row : rows) {
                String key = getKey(row);
                if (key == null || key.length() == 0) {
                    continue;
                }

                if (map.containsKey(key)) {
                    System.out.println("The same key " + key + " from file " + f.getName() + " is found in " + map.get(key));
                    duplicates++;
                } else {
                    map.put(key, f.getName());
                }
            }
        }

        System.out.println("Total duplicates for lang " + lang + ": " + duplicates);
        return duplicates == 0;
    }

    /**
     * Find not deleted resources. Remove not deleted resources and generate correct files.
     * Resources is not deleted if its key is not presented in english version
     *
     * @param dir directory to validate
     * @return true if there are no not deleted resources, false in contrary
     * @throws java.io.IOException
     */
    private static boolean findNotUsedKeys(File dir, Language lang, String out) throws IOException {
        File[] fileList = dir.listFiles(filterForLang(lang));
        int extraCount = 0;
        boolean extraKeysFound;

        for (File langFile : fileList) {
            extraKeysFound = false;
            ArrayList<String> rows = readFileContent(langFile);

            File engFile = new File(dir + "/" + langFile.getName().substring(0, langFile.getName().lastIndexOf("_" + lang + ".properties")) + ".properties");
            if (!engFile.exists()) {
                System.out.println("Found extra file " + langFile.getName());
                extraCount += rows.size();
                continue;
            }

            Properties engProps = loadProps(engFile);
            StringBuffer buf = new StringBuffer();
            for (String row : rows) {
                String key = getKey(row);
                if (key == null || key.length() == 0 || engProps.containsKey(key)) {
                    buf.append(row).append(ResourceUtil.LINE_SEPARATOR);
                } else {
                    System.out.println("Found extra key " + key + " in file " + langFile.getName());
                    extraCount++;
                    extraKeysFound = true;
                }
            }

            if (extraKeysFound) {
                FileOutputStream fos = new FileOutputStream(out + "/" + langFile.getName());
                fos.write(buf.toString().getBytes());
                fos.close();
            }
        }

        System.out.println("Total count of extra keys for lang " + lang + ": " + extraCount);
        return extraCount == 0;
    }

    /**
     * Collect all resources returned by FileFilter in one object
     */
    public static Properties readProps(String dirName, FileFilter ff) throws IOException {
        File dir = new File(dirName);
        Properties props = new Properties();

        File[] listOldEn = dir.listFiles(ff);
        for (File f : listOldEn) {
            props.putAll(loadProps(f));
        }

        return props;
    }

    public static Properties loadProps(File ... files) throws IOException {
        Properties p = new Properties();
        for (File file : files) {
            FileInputStream fis = new FileInputStream(file);
            p.load(fis);
            fis.close();
        }
        return p;
    }

    public static Map<String, String> getMapByKey(File... files) throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        for (File file : files) {
            List<String> rows = readFileContent(file);
            for (String row : rows) {
                String key = getKey(row);
                if (key != null) {
                    map.put(key, getValue(row));
                }
            }

        }
        return map;
    }

    public static Map<String, List<String>> getMapByValue(Properties props) throws IOException {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key).trim().toLowerCase();
            if (!value.isEmpty()) {
                List<String> keys = map.get(value);
                if (keys == null) {
                    keys = new ArrayList();
                    map.put(value, keys);
                }
                keys.add(key);
            }
        }
        return map;
    }

    /**
     * Check if the given key is an exclusion and shouldn't be translated
     *
     * @param key the key to check
     * @return true if the given key is an exclusion
     */
    public static boolean isExclusion(Object key) throws IOException {
        String keyStr = (String) key;
        if (keyStr.startsWith("user.language.") || keyStr.equals("form.copyright")) {
            return true;
        }
        return false;
    }

    /**
     * Create FileFilter which returns resource files for specified language
     *
     * @param lang en, ja, ru, ko, pt, zh, ro, tr
     * @return FileFilter for specified language
     */
    public static FileFilter filterForLang(final Language lang) {
        return new FileFilter() {
            public boolean accept(File pathname) {
                if (Language.EN == lang) {
                    return pathname.getName().endsWith("properties") && !pathname.getName().matches(".+_[A-Za-z]{2}\\.properties");
                } else {
                    return pathname.getName().endsWith(lang + ".properties");
                }
            }
        };
    }

    /**
     * Read content for the given file
     *
     * @param file the file to read content
     * @return file content
     */
    public static ArrayList<String> readFileContent(File file) throws IOException {
        FileReader fileReader = null;
        BufferedReader br = null;
        ArrayList<String> result = new ArrayList<String>();

        try {
            fileReader = new FileReader(file);
            br = new BufferedReader(fileReader);

            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }

            if (fileReader != null) {
                fileReader.close();
            }
        }

        return result;
    }

    /**
     * Search the key in the given string
     *
     * @param line the string to search the key in
     * @return the key found
     */
    public static String getKey(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.length() == 0 || trimmedLine.startsWith("#") || trimmedLine.startsWith("!")) {
            return null;
        }

        int pos = line.indexOf("=");
        if (pos == -1) {
            System.out.println("Can't find '=' in '" + line + "'");
            throw new IllegalArgumentException();
        }

        String value = line.substring(0, pos);
        return value.trim();
    }

    public static String getValue(String line) {
        String key = getKey(line);
        if (key != null) {
            return line.substring(getKey(line).length() + 1).trim();
        } else {
            return null;
        }
    }
}
