package com.foros.util.mapper;

import com.foros.model.DisplayStatus;

/**
 * Author: Boris Vanin
 */
public class DisplayStatusMapper implements Mapper<DisplayStatus, Long, String> {

    public Pair<Long, String> item(DisplayStatus value) {
        return new LocalizeMapper<Long>().item(
                new Pair<Long, String>(value.getId(), value.getDescription())
        );
    }

}