package com.foros.action;

import com.foros.util.StringUtil;
import java.io.Serializable;

public class IdForm implements Serializable {
    private String id;

    public IdForm() {
    }

    public IdForm(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = StringUtil.isPropertyEmpty(id) ? null : id;
    }
}
