package com.foros.framework.support;

import java.util.TimeZone;

/**
 * Used for two way convertions of date-time fields dependent on time zone.
 *
 * @see com.foros.framework.conversion.TimeZoneDateTimeConverter
 */
public interface TimeZoneAware {
    TimeZone getTimeZone();
}
