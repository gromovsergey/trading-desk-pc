package com.foros.validation.code;

import org.apache.commons.lang.StringEscapeUtils;

public class ForosErrorsPrinter {
    public static void main(String[] args) {
        printCodes(InputErrors.values(), "Parse Errors", 10000, 100);
        printCodes(BusinessErrors.values(), "Business", 100000, 1000);
    }

    private static void printCodes(ForosError[] values, String categoryName, int lvl1, int lvl2) {
        // markup to be pasted to https://confluence.ocslab.com/display/TDOC/Error+Codes
        System.out.println();
        System.out.println("<tr><td><h3>" + categoryName + "</h3></td><td></td><td></td></tr>");
        System.out.println();

        for (ForosError value : values) {
            System.out.print("<tr><td>");

            // name
            String level = level(lvl1, lvl2, value);

            System.out.print(String.format("<%s>", level));
            System.out.print(value.name());
            System.out.print(String.format("</%s>", level));
            System.out.print("</td><td>");
            // code
            System.out.print(value.getCode());
            System.out.print("</td><td>");
            if (value.getUrl() != null) {
                System.out.print(String.format("<a href=\"%s\">%s</a>", value.getUrl(), StringEscapeUtils.escapeHtml(value.getText())));
            } else if (value.getText() != null){
                System.out.print(StringEscapeUtils.escapeHtml(value.getText()));
            }
            // description
            System.out.print("</td></tr>");
            System.out.println();
        }
    }

    private static String level(int lvl1, int lvl2, ForosError value) {
        if (value.getCode() % lvl1 == 0) {
            return "h6";
        } else if (value.getCode() % lvl2 == 0) {
            return "p";
        } else {
            return "li";
        }
    }
}
