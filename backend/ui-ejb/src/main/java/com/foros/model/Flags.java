package com.foros.model;

import java.io.Serializable;

public final class Flags implements Serializable {
    public static final Flags ZERO = new Flags();
    private int value;
    private int changes;

    public Flags() {
    }

    public Flags(int value) {
        this.value = value;
    }

    public int intValue() {
        return value;
    }

    public long longValue() {
        return value;
    }

    public boolean get(int mask) {
        return (value & mask) == mask;
    }

    public Flags set(int mask, boolean set) {
        return set(mask, set ? mask : 0x0);
    }

    public Flags set(Flags updated) {
        return set(updated.changes, updated.value);
    }

    public Flags set(int mask, int value) {
        Flags f = new Flags();
        f.value = (this.value & ~mask) | (value & mask);
        f.changes = changes | mask;
        return f;
    }

    public boolean isChanged(int mask) {
        return (changes & mask) == mask;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flags flags = (Flags) o;

        if (value != flags.value) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return value;
    }

    public boolean isChanged() {
        return changes != 0;
    }

    public static boolean isDifferent(Flags flags1, Flags flags2, int mask) {
        return ((flags1.value ^ flags2.value) & mask) != 0;
    }
}
