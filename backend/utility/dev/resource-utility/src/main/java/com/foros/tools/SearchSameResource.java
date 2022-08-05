package com.foros.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SearchSameResource {
    private Args args;

    public void execute(Args args) throws IOException {
        this.args = args;

        if (args.getDir() == null || args.getOut() == null || args.getLang() == null) {
            args.printHelp();
            return;
        }

        if (ResourceHelper.validateResources(args.getDir(), args)) {
            generate();
        } else {
            System.out.println("Validation is failed!");
        }
    }

    private void generate() throws IOException {
        File dir = new File(args.getDir());

        File[] engList = dir.listFiles(ResourceHelper.filterForLang(Language.EN));
        Properties engProperties = ResourceHelper.loadProps(engList);
        Map<String, List<String>> valuesMap = ResourceHelper.getMapByValue(engProperties);

        File[] langList = dir.listFiles(ResourceHelper.filterForLang(args.getLang()));
        Properties langProperties = ResourceHelper.loadProps(langList);

        Map<String, String> langRows = new HashMap<String, String>();
        for (File langFile : langList) {
            List<String> rows = ResourceHelper.readFileContent(langFile);
            for (String row : rows) {
                String key = ResourceHelper.getKey(row);
                if (key == null) {
                    continue;
                }
                langRows.put(key, row);
            }
        }

        StringBuffer buf = new StringBuffer();
        for (Map.Entry<String, List<String>> entry : valuesMap.entrySet()) {
            List<String> keys = entry.getValue();
            if (keys.size() > 1) {
                boolean fail = false;

                String value = null;
                for (String key : keys) {
                    if (langProperties.get(key) != null) {
                        String currValue = (langProperties.getProperty(key)).trim().toUpperCase();
                        if (value == null) {
                            value = currValue;
                        }
                        if (!value.equalsIgnoreCase(currValue)) {
                            fail = true;
                            break;
                        }
                    }
                }

                if (fail) {
                    for (String key : keys) {
                        if (langRows.get(key) != null) {
                            buf.append("# ").append(engProperties.get(key)).append(ResourceUtil.LINE_SEPARATOR);
                            buf.append(langRows.get(key)).append(ResourceUtil.LINE_SEPARATOR);
                        }
                    }
                    buf.append(ResourceUtil.LINE_SEPARATOR);
                }
            }
        }

        FileOutputStream fos = new FileOutputStream(args.getOut() + "/values_" + args.getLang() + ".properties");
        fos.write(buf.toString().getBytes());
        fos.close();

        System.out.println("Properties were added to " + "values_" + args.getLang() + ".properties");

        return;
    }
}