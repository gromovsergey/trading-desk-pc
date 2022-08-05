package com.foros.cache.generic;

import org.joda.time.ReadableDuration;

public interface ExpirationTimeCalculator {
    ReadableDuration getExpirationTime();
}
