package com.foros.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class SearchResource {
    private Args args;
    private boolean oldDirSpecified = false;

    public void execute(Args args) throws IOException {
        this.args = args;

        if (args.getDir() == null || args.getLang() == null || args.getOut() == null) {
            args.printHelp();
            return;
        }

        if (args.getOldDir() != null) {
            oldDirSpecified = true;
        }

        doSearch();
    }

    private void doSearch() throws IOException {
        if (ResourceHelper.validateResources(args.getDir(), args)) {
            prepareResourcesForTranslation();
        } else {
            System.out.println("Validation is failed!");
        }
    }

    /**
     * Generates files for translation.
     */
    private void prepareResourcesForTranslation() throws IOException {
        CommentsResource commentsResource = new CommentsResource(args);

        Language lang = args.getLang();
        int notTranslated = 0;
        int translated = 0;

        Properties langOldProps = new Properties();
        Properties engOldProps = new Properties();
        if (oldDirSpecified) {
            langOldProps = ResourceHelper.readProps(args.getOldDir(), ResourceHelper.filterForLang(lang));
            engOldProps = ResourceHelper.readProps(args.getOldDir(), ResourceHelper.filterForLang(Language.EN));
        }

        File dir = new File(args.getDir());
        File[] engList = dir.listFiles(ResourceHelper.filterForLang(Language.EN));

        for (File engFile : engList) {
            List<String> result = new LinkedList<String>();
            ArrayList<String> engRows = ResourceHelper.readFileContent(engFile);

            File langFile = new File(dir + "/" + engFile.getName().substring(0, engFile.getName().lastIndexOf(".properties")) + "_" + lang + ".properties");
            if (langFile.exists()) {
                Properties langProps = ResourceHelper.loadProps(langFile);
                Properties engProps = ResourceHelper.loadProps(engFile);

                for (String row : engRows) {
                    String key = ResourceHelper.getKey(row);
                    if (key == null || key.length() == 0) {
                        continue;
                    }

                    boolean langPropIsEmpty = langProps.getProperty(key) == null ||
                            langProps.get(key).toString().trim().length() == 0 && engProps.get(key).toString().trim().length() != 0;
                    boolean oldPropIsNotUsable = !oldDirSpecified ||
                            langOldProps.get(key) == null || langOldProps.get(key).toString().trim().length() == 0 ||
                            !engProps.get(key).equals(engOldProps.get(key));
                    boolean propWasChanged = oldDirSpecified &&
                            !engProps.get(key).equals(engOldProps.get(key)) && langProps.get(key).equals(langOldProps.get(key));

                    if (langPropIsEmpty && oldPropIsNotUsable || propWasChanged) {
                        if (ResourceHelper.isExclusion(key)) {
                            continue;
                        }
                        result.add(row);
                        notTranslated++;
                    } else {
                        translated++;
                    }
                }
            } else {
                for (String row : engRows) {
                    String key = ResourceHelper.getKey(row);
                    if (key == null || key.length() == 0) {
                        continue;
                    }
                    result.add(row);
                    notTranslated++;
                }
            }

            if (!result.isEmpty()) {
                StringBuffer buf = new StringBuffer();
                for (String row : result) {
                    commentsResource.addCommentsToRow(buf, row);
                    buf.append(row).append(ResourceUtil.LINE_SEPARATOR);
                }

                FileOutputStream fos = new FileOutputStream(args.getOut() + "/" + langFile.getName());
                fos.write(commentsResource.getMainExclusionsComment().getBytes());
                fos.write(buf.toString().getBytes());
                fos.close();
            }
        }

        System.out.println("Translated: " + translated);
        System.out.println("New or changed: " + notTranslated);
    }
}