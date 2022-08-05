package com.foros.util;

/**
 * Author: Boris Vanin
 */
public class ConditionStringBuilder {

    private StringBuilder builder;
    private boolean appendNewLineSymbol = false;

    public ConditionStringBuilder(String string) {
        this.builder = new StringBuilder(string);
    }

    public ConditionStringBuilder(boolean appendNewLineSymbol, String string) {
        this.appendNewLineSymbol = appendNewLineSymbol;
        this.builder = new StringBuilder(string);
    }

    public ConditionStringBuilder() {
        this.builder = new StringBuilder();
    }

    public ConditionStringBuilder append(char ch) {
        builder.append(ch);
        return this;
    }

    public ConditionStringBuilder append(String string) {
        this.builder.append(string);

        if (appendNewLineSymbol) {
            this.builder.append("\n");
        }

        return this;
    }

    public ConditionStringBuilder append(boolean appendThis, String string) {
        if (appendThis) {
            append(string);
        }

        return this;
    }

    public ConditionStringBuilder append(boolean condition, String ifTrue, String ifFalse) {
        if (condition) {
            append(ifTrue);
        } else {
            append(ifFalse);
        }

        return this;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
    
}
