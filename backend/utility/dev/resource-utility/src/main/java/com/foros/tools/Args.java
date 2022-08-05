package com.foros.tools;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

public class Args {
    private Command util;
    private String dir;
    private String oldDir;
    private String addDir;
    private String untranslatedDir;
    private String out;
    private Language lang;

    private String helpMessage;

    public Args(String helpMessage) {
        this.helpMessage = helpMessage;
    }

    public void parse(String[] args) throws Exception {
        Iterator<String> it = Arrays.asList(args).iterator();

        if (it.hasNext()) {
            parseUtil(it.next());
        } else {
            throw new Exception("Please specify the parameters list");
        }

        while (it.hasNext()) {
            String arg = it.next();

            if (arg.startsWith("dir=")) {
                dir = arg.substring("dir=".length());
                validateDir(dir);
                continue;
            }

            if (arg.startsWith("old-dir=")) {
                oldDir = arg.substring("old-dir=".length());
                validateDir(oldDir);
                continue;
            }

            if (arg.startsWith("add-dir=")) {
                addDir = arg.substring("add-dir=".length());
                validateDir(addDir);
                continue;
            }

            if (arg.startsWith("untranslated-dir=")) {
                untranslatedDir = arg.substring("untranslated-dir=".length());
                validateDir(untranslatedDir);
                continue;
            }

            if (arg.startsWith("out=")) {
                out = arg.substring("out=".length());
                validateOutputDir();
                continue;
            }

            if (arg.startsWith("lang=")) {
                String lang = arg.substring("lang=".length());
                parseLang(lang);
                continue;
            }
        }
    }

    private void parseUtil(String utilName) throws Exception {
        try {
            util = Command.valueOf(utilName.toUpperCase());
        } catch (IllegalArgumentException eae) {
            throw new Exception("Wrong parameter util: " + utilName);
        }
    }

    private void parseLang(String langName) throws Exception {
        try {
            lang = Language.valueOf(langName.toUpperCase());
        } catch (IllegalArgumentException eae) {
            throw new Exception("Wrong parameter lang: " + langName);
        }

        if (lang == Language.EN) {
            throw new Exception("Parameter lang can't be en");
        }
    }

    private void validateOutputDir() throws Exception {
        File saveTo = new File(getOut());
        if (!saveTo.exists()) {
            if (!saveTo.mkdir()) {
                throw new Exception("Can't create output directory '" + saveTo + "'");
            }
        } else if (!saveTo.isDirectory()) {
            throw new Exception("'" + saveTo + "' for output doesn't exist or not a directory");
        }
    }

    private void validateDir(String dir) throws Exception {
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()) {
            throw new Exception("Directory '" + file + "' doesn't exist");
        }
    }

    public String getDir() {
        return dir;
    }

    public String getOldDir() {
        return oldDir;
    }

    public String getAddDir() {
        return addDir;
    }

    public String getUntranslatedDir() {
        return untranslatedDir;
    }

    public Language getLang() {
        return lang;
    }

    public String getOut() {
        return out;
    }

    public Command getUtil() {
        return util;
    }

    public void printHelp() {
        System.out.println(helpMessage);
    }
}
