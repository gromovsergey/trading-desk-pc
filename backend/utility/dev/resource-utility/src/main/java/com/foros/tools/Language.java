package com.foros.tools;

public enum Language {
    EN, RU, KO, JA, PT, ZH, RO, TR;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
