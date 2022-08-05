package com.foros.action.fileman;

import com.foros.framework.ReadOnly;
import com.foros.util.StringUtil;

public class FileManagerChDirAction extends FileManagerActionSupport {

    private String nextDir;

    @ReadOnly
    public String chDir() {
        if (StringUtil.isPropertyEmpty(nextDir)) {
            return "redirectToRoot";
        }

        String newDir;

        if (nextDir.startsWith("../")) {
            try {
                int index = Integer.parseInt(nextDir.substring(3));
                String[] dirs = getListDir(getCurrDirStr());
                StringBuffer sb = new StringBuffer();

                for (int i = 0; i <= index; i++) {
                    sb.append(dirs[i]).append("/");
                }

                newDir = sb.toString();
            } catch (NumberFormatException e) {
                newDir = "";
            }
        } else {
            newDir = getCurrDirStr() + getNextDir() + "/";
        }

        setCurrDirStr(newDir);

        return SUCCESS;
    }

    public String getNextDir() {
        return nextDir;
    }

    public void setNextDir(String nextDir) {
        this.nextDir = nextDir;
    }

    private String[] getListDir(String path) {
        if (StringUtil.isPropertyEmpty(path)) {
            return new String[0];
        }

        return path.split("/");
    }
}
