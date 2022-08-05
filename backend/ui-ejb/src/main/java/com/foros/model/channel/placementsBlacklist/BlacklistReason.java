package com.foros.model.channel.placementsBlacklist;

import com.foros.util.FlagsUtil;
import com.foros.util.StringUtil;

import java.util.EnumSet;
import java.util.Set;

public enum BlacklistReason {
    LOW_CTR("lowCTR", 'C', 0x01),
    LOW_VIEWABILITY("lowViewability", 'V', 0x02),
    HIGH_FRAUD("highFraud", 'F', 0x04),
    INAPPROPRIATE_CONTENT("inappropriateContent", 'X', 0x08);

    private static final String BASE_KEY = "admin.placementsBlacklist.reason.";

    private String key;
    private Character code;
    private long bit;

    BlacklistReason(String key, Character code, long bit) {
        this.key = key;
        this.code = code;
        this.bit = bit;
    }

    public String getName() {
        return StringUtil.getLocalizedString(BASE_KEY + key);
    }

    public Character getCode() {
        return code;
    }

    public long getBit() {
        return bit;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static BlacklistReason valueOf(Character code) {
        BlacklistReason[] values = BlacklistReason.values();
        for (BlacklistReason reason : values) {
            if (reason.getCode().equals(code)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("No enum constant mapped to character " + code);
    }

    public static Set<BlacklistReason> bitsToSet(Long bits) {
        if (bits == null) {
            return null;
        }

        BlacklistReason[] values = BlacklistReason.values();

        EnumSet<BlacklistReason> result = EnumSet.noneOf(BlacklistReason.class);
        for (BlacklistReason reason : values) {
            if (FlagsUtil.get(bits, reason.getBit())) {
                result.add(reason);
            }
        }

        return result;
    }

    public static long setToBits(Set<BlacklistReason> list) {
        long result = 0;
        if (list != null) {
            for (BlacklistReason reason : list) {
                result |= FlagsUtil.set(result, reason.getBit(), true);
            }
        }
        return result;
    }
}
