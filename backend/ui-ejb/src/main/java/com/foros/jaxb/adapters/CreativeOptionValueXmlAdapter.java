package com.foros.jaxb.adapters;

import com.foros.model.EntityBase;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.template.Option;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CreativeOptionValueXmlAdapter extends XmlAdapter<CreativeOptionValueXmlAdapter.IdTokenValue, CreativeOptionValue> {
    @Override
    public IdTokenValue marshal(CreativeOptionValue v) throws Exception {
        IdTokenValue res = new IdTokenValue();
        res.setId(v.getOptionId());
        res.setToken(v.getOption().getToken());
        res.setValue(v.getValue());
        return res;
    }

    @Override
    public CreativeOptionValue unmarshal(IdTokenValue v) throws Exception {
        CreativeOptionValue res = new CreativeOptionValue();
        Option option = new Option(v.getId());
        option.setToken(v.getToken());
        res.setOption(option);
        res.setValue(v.getValue());
        return res;
    }

    @XmlType
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class IdTokenValue extends EntityBase {
        private Long id;
        private String token;
        private String value;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
