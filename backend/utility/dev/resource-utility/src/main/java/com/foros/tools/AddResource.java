package com.foros.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class AddResource {
    private Args args;

    public void execute(Args args) throws IOException {
        this.args = args;

        if (args.getDir() == null || args.getAddDir() == null || args.getUntranslatedDir() == null || args.getLang() == null || args.getOut() == null) {
            args.printHelp();
            return;
        }

        doAdd();
    }

    private void doAdd() throws IOException {
        if (ResourceHelper.validateResources(args.getDir(), args)) {
            addResources();
        } else {
            System.out.println("Validation is failed!");
        }
    }

    /**
     * Add new resources. Save merged and sorted resources to output dir.
     */
    private void addResources() throws IOException {
        File addDir = new File(args.getAddDir());
        File untranslatedDir = new File(args.getUntranslatedDir());
        File dir = new File(args.getDir());

        File[] fromList = addDir.listFiles(ResourceHelper.filterForLang(args.getLang()));

        for (File fromFile : fromList) {
            System.out.println("Start adding properties to " + fromFile.getName() + " ... ");

            File engFile = new File(dir + "/" + fromFile.getName().substring(0, fromFile.getName().lastIndexOf("_" + args.getLang() + ".properties")) + ".properties");
            if (!engFile.exists()) {
                System.out.println("The english resource for " + fromFile.getName() + " does not exist!");
                continue;
            }

            File toFile = new File(dir + "/" + fromFile.getName());
            if (!toFile.exists()) {
                toFile.createNewFile();
            }

            File untranslatedFile = new File(untranslatedDir + "/" + fromFile.getName());
            Properties untranslatedProps = ResourceHelper.loadProps(untranslatedFile);
            Properties engProps = ResourceHelper.loadProps(engFile);

            ArrayList<String> toRows = ResourceHelper.readFileContent(toFile);
            HashMap props = new HashMap();
            for (String row : toRows) {
                String key = ResourceHelper.getKey(row);
                if (key != null && key.length() > 0) {
                    props.put(key, row);
                }
            }

            ArrayList<String> fromRows = ResourceHelper.readFileContent(fromFile);
            for (String row : fromRows) {
                String key = ResourceHelper.getKey(row);
                if (key != null && key.length() > 0 && untranslatedProps.getProperty(key) != null && engProps.getProperty(key) != null) {
                    if (untranslatedProps.getProperty(key).equals(engProps.getProperty(key))) {
                        props.put(key, row);
                    } else {
                        System.out.println("--- the property '" + key + "' was changed!");
                    }
                }
            }

            ArrayList<String> rows = ResourceHelper.readFileContent(engFile);
            StringBuffer buf = new StringBuffer();
            for (String row : rows) {
                String key = ResourceHelper.getKey(row);
                if (key == null || key.length() == 0) {
                    buf.append(row).append(ResourceUtil.LINE_SEPARATOR);
                } else if (props.containsKey(key)) {
                    buf.append(props.get(key).toString().trim()).append(ResourceUtil.LINE_SEPARATOR);
                }
            }

            FileOutputStream fos = new FileOutputStream(args.getOut() + "/" + fromFile.getName());
            fos.write(buf.toString().getBytes());
            fos.close();

            System.out.println("done");
        }
    }
}
