package com.foros.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PairsResource {
    private Args args;

    public void execute(Args args) throws IOException {
        this.args = args;

        if (args.getDir() == null || args.getOut() == null || args.getLang() == null) {
            args.printHelp();
            return;
        }

        generatePairs();
    }

    private void generatePairs() throws IOException {
        File dir = new File(args.getDir());

        File[] engList = dir.listFiles(ResourceHelper.filterForLang(Language.EN));
        StringBuffer buf = new StringBuffer();

        for (File engFile : engList) {
            File langFile = new File(dir + "/" + engFile.getName().substring(0, engFile.getName().lastIndexOf(".properties")) + "_" + args.getLang() + ".properties");
            if (!langFile.exists()) {
                continue;
            }

            buf.append("##############################################").append(ResourceUtil.LINE_SEPARATOR);
            buf.append("# RESOURCE FILE: ").append(engFile.getName()).append(ResourceUtil.LINE_SEPARATOR);
            buf.append("##############################################").append(ResourceUtil.LINE_SEPARATOR);

            ArrayList<String> langRows = ResourceHelper.readFileContent(langFile);
            HashMap props = new HashMap();
            for (String row : langRows) {
                String key = ResourceHelper.getKey(row);
                if (key != null && key.length() > 0) {
                    props.put(key, row);
                }
            }

            ArrayList<String> engRows = ResourceHelper.readFileContent(engFile);
            for (String row : engRows) {
                String key = ResourceHelper.getKey(row);
                if (key == null || key.length() == 0) {
                    buf.append(row).append(ResourceUtil.LINE_SEPARATOR);
                } else if (props.containsKey(key)) {
                    buf.append(props.get(key).toString().trim());
                    buf.append("  (en: \"").append(row.substring(key.length() + 1).trim()).append("\")").append(ResourceUtil.LINE_SEPARATOR);
                }
            }
        }

        FileOutputStream fos = new FileOutputStream(args.getOut() + "/" + args.getLang() + "-translations.properties");
        fos.write(buf.toString().getBytes());
        fos.close();

        System.out.println("Properties were added to " + args.getLang() + "-translations.properties");

        return;
    }
}
