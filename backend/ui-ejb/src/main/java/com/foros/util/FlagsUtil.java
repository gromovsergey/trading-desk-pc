package com.foros.util;

public class FlagsUtil {
    public static boolean get(long flags, long mask) {
        return (flags & mask) == mask;
    }

    public static long set(long flags, long mask, boolean set) {
        return set ? flags | mask : flags & ~mask;
    }
}
