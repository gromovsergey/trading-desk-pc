package com.foros.model.fileman;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "fileList")
@XmlType(propOrder = {
        "files"
})
public class FileList {
    private List<String> files;

    public FileList() {
    }

    public FileList(List<String> files) {
        this.files = files;
    }

    @XmlElement(name = "file")
    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
