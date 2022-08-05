package com.foros.action.xml.options.converter;

import com.foros.util.NameValuePair;
import com.foros.util.PairUtil;

/**
 * Author: Boris Vanin
 * Date: 27.11.2008
 * Time: 12:27:57
 * Version: 1.0
 */
public class NameValuePairConverter implements Converter<NameValuePair<String, String>> {

    private boolean concatForValue = false;

    public NameValuePairConverter(boolean concatForValue) {
        this.concatForValue = concatForValue;
    }

    public NameValuePair<String, String> convert(NameValuePair<String, String> o) {
        if (!concatForValue) {
            return o;
        } else {
            String name = o.getName();
            String value = o.getValue();
            return new NameValuePair<String, String>(name, concatForValue ? PairUtil.createAsString(value, name) : value);
        }
    }

}