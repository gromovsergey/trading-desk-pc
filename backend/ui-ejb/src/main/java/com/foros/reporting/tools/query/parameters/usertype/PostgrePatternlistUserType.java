package com.foros.reporting.tools.query.parameters.usertype;

import java.util.List;

public class PostgrePatternlistUserType {
    private String name;
    private List<String> patterns;

    public PostgrePatternlistUserType(String name, List<String> patterns) {
        this.name = name;
        this.patterns = patterns;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(\"");
        sb.append(name);
        sb.append("\", ");
        sb.append(patternsToString());
        sb.append(")");

        return sb.toString();
    }

    private String patternsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");

        boolean appendComma = false;
        for (String pattern: patterns) {
            if (appendComma) {
                sb.append("\\, ");
            }
            sb.append("\"");
            sb.append(pattern);
            sb.append("\"");
            appendComma = true;
        }

        sb.append(" }");
        return sb.toString();
    }
}
