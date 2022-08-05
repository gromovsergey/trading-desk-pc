package com.foros.util.mapper;

import com.foros.util.StringUtil;

/**
 * Author: Boris Vanin
*/
public class LocalizeMapper<K> implements Mapper<Pair<K, String>, K, String> {

    public Pair<K, String> item(Pair<K, String> value) {
        return new Pair<K, String>(value.getLeftValue(), StringUtil.getLocalizedString(value.getRightValue()));
    }

}