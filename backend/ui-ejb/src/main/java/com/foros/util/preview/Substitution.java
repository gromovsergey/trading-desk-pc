package com.foros.util.preview;

import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Substitution {
    private final String prefix;
    private final String token;
    private final String defaultValue;

    public Substitution(String prefix, String token, String defaultValue) {
        this.prefix = prefix;
        this.token = token;
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Substitution that = (Substitution) o;
        return Objects.equals(prefix, that.prefix) &&
                Objects.equals(token, that.token) &&
                Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, token, defaultValue);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("prefix", prefix)
                .append("token", token)
                .append("defaultValue", defaultValue)
                .toString();
    }

    static Substitution ptd(String prefix, String token, String defaultValue) {
        return new Substitution(prefix, token, defaultValue);
    }

    public static Substitution pt(String prefix, String token) {
        return new Substitution(prefix, token, null);
    }

    static Substitution td(String token, String defaultValue) {
        return new Substitution(null, token, defaultValue);
    }

    public static Substitution t(String token) {
        return new Substitution(null, token, null);
    }
}
