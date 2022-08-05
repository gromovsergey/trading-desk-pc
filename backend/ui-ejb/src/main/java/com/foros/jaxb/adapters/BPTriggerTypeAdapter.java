package com.foros.jaxb.adapters;

import com.foros.model.channel.trigger.TriggerType;

import java.util.Arrays;
import java.util.Collection;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BPTriggerTypeAdapter extends XmlAdapter<String, Character> {

    private static final Collection<Character> ALLOWED_VALUES = Arrays.asList(
            TriggerType.PAGE_KEYWORD.getLetter(),
            TriggerType.SEARCH_KEYWORD.getLetter(),
            TriggerType.URL.getLetter(),
            TriggerType.URL_KEYWORD.getLetter()
    );

    public BPTriggerTypeAdapter() {
    }

    @Override
    public Character unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }

        if (v.length() != 1) {
            throw new IllegalArgumentException();
        }

        char ch = v.charAt(0);

        if (!ALLOWED_VALUES.contains(ch)) {
            throw new IllegalArgumentException();
        }

        return ch;
    }

    @Override
    public String marshal(Character v) throws Exception {
        return v == null ? null : v.toString();
    }
}