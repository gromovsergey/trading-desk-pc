package com.foros.action.xml.generator;

import com.foros.action.xml.model.FileInfo;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 15:24:43
 * Version: 1.0
 */
public class FileUniqueGenerator implements Generator<FileInfo> {

    public String generate(FileInfo fileInfo) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<FILE_MANAGER ");
        if (!fileInfo.getFileName().endsWith(".zip") && fileInfo.isDir()) {
            xml.append(" doesFolderExist=\"").append(true).append("\"");
        }

        xml.append(" confirmMessage=\"").append(StringEscapeUtils.escapeXml(fileInfo.getConfirmMessage())).append("\" ");
        xml.append("doesFileExist=\"").append(fileInfo.isExists()).append("\"/>");

        return xml.toString();
    }

}