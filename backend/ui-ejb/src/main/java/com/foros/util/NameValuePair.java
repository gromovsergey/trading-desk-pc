package com.foros.util;

import java.util.Comparator;

public class NameValuePair<NAME, VALUE> {
    private NAME name;
    private VALUE value;

    public NameValuePair() {
    }

    public NameValuePair(NAME name, VALUE value) {
        this.name = name;
        this.value = value;
    }

    public NAME getName() {
        return name;
    }

    public void setName(NAME name) {
        this.name = name;
    }

    public VALUE getValue() {
        return value;
    }

    public void setValue(VALUE value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NameValuePair)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        return this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "Name = " + name + " value = " + value;
    }

    public static final Comparator<NameValuePair<String, String>> nameValuePairComparatorByName = new Comparator<NameValuePair<String, String>>() {
        @Override
        public int compare(NameValuePair<String, String> pair1, NameValuePair<String, String> pair2) {
            return pair1.getName().compareTo(pair2.getName());
        }
    };
}
