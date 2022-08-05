package com.foros.model.channel.trigger;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public abstract class TriggerBase implements Serializable {

    protected String original;
    protected boolean isNegative;

    protected TriggerBase() {}

    protected TriggerBase(String original, boolean isNegative) {
        this.original = original == null ? "" : original.trim();
        this.isNegative = isNegative;
    }

    public String getOriginal() {
        return original;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public abstract String getNormalized();

    public abstract String getUINormalized();

    public abstract  String getQANormalized();

    public byte[] getBytes() {
        try {
            return original.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TriggerBase that = (TriggerBase) o;

        if (isNegative != that.isNegative) return false;
        if (original != null ? !original.equals(that.original) : that.original != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = original != null ? original.hashCode() : 0;
        result = 31 * result + (isNegative ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getQANormalized();
    }
}
