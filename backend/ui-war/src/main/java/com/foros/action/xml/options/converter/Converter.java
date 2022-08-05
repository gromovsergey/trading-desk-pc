package com.foros.action.xml.options.converter;

import com.foros.util.NameValuePair;

/**
 * Convert T object to NamedTO
 * <p/>
 * Author: Boris Vanin
 * Date: 27.11.2008
 * Time: 12:00:04
 * Version: 1.0
 */
public interface Converter<T> {

    NameValuePair<String, String> convert(T value);

}
