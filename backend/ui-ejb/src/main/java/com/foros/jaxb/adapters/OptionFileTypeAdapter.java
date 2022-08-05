package com.foros.jaxb.adapters;

import com.foros.model.template.OptionFileType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OptionFileTypeAdapter extends XmlAdapter<String, OptionFileType> {

    @Override
    public OptionFileType unmarshal(String v) throws Exception {
        OptionFileType ft = new OptionFileType();
        ft.setFileType(v);
        return ft;
    }

    @Override
    public String marshal(OptionFileType v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.getFileType();
    }
}
