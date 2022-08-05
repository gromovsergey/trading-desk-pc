package com.foros.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddUnsortedResource {
    private Args args;

    public void execute(Args args) throws IOException {
        this.args = args;

        if (args.getDir() == null || args.getAddDir() == null || args.getLang() == null || args.getOut() == null) {
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
        File dir = new File(args.getDir());
        Language lang = args.getLang();

        Map newProps = new HashMap();
        File[] fromFiles = addDir.listFiles(ResourceHelper.filterForLang(lang));
        for (File fromFile : fromFiles) {
            ArrayList<String> fromRows = ResourceHelper.readFileContent(fromFile);
            for (String row : fromRows) {
                String key = ResourceHelper.getKey(row);
                if (key != null && key.length() > 0) {
                    newProps.put(key, row);
                }
            }
        }

        File[] engList = dir.listFiles(ResourceHelper.filterForLang(Language.EN));
        for (File engFile : engList) {
            File toFile = new File(dir + "/" + engFile.getName().substring(0, engFile.getName().lastIndexOf(".properties")) + "_" + lang + ".properties");
            if (!toFile.exists()) {
                toFile.createNewFile();
            }

            Map props = new HashMap();
            ArrayList<String> toRows = ResourceHelper.readFileContent(toFile);
            for (String row : toRows) {
                String key = ResourceHelper.getKey(row);
                if (key != null && key.length() > 0) {
                    props.put(key, row);
                }
            }

            props.putAll(newProps);

            StringBuffer buf = new StringBuffer();
            ArrayList<String> rows = ResourceHelper.readFileContent(engFile);
            for (String row : rows) {
                String key = ResourceHelper.getKey(row);
                if (key == null || key.length() == 0) {
                    buf.append(row).append(ResourceUtil.LINE_SEPARATOR);
                } else if (props.containsKey(key)) {
                    buf.append(props.get(key).toString().trim()).append(ResourceUtil.LINE_SEPARATOR);
                }
            }

            FileOutputStream fos = new FileOutputStream(args.getOut() + "/" + toFile.getName());
            fos.write(buf.toString().getBytes());
            fos.close();

            System.out.println(toFile.getName() + " was updated");
        }
    }
}
