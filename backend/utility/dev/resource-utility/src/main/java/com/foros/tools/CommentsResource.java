package com.foros.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsResource {
    private static final String[] exclusions = new String[]{
            "LDAP", "URL", "ISP", "ID", "CCID", "RON", "CCID", "RON", "CPA", "CPM", "CPC",
            "eCPA", "eCPM", "CTR", "CSV", "Excel", "Webwise", "Discover", "WD",
            "Swift", "BACS", "XSLT", "kB", "Mb", "BIRT", "Rptdesign", "pdf",
            "FOROS", "Open Internet Exchange", "ARPM", "ARPU", "HTML", "RSS", "Atom",
            "IBAN", "MIME", "GMT", "CMP", "PWP", "HTTP", "HTTPS", "OK", "N/A", "ORACLE"
    };

    Map<String, String> langKeysMap;
    Map<String, List<String>> engValuesMap;
    Map<String, String> baseEntities;

    public CommentsResource(Args args)  throws IOException {
        File dir = new File(args.getDir());
        File[] langList = dir.listFiles(ResourceHelper.filterForLang(args.getLang()));
        File[] engList = dir.listFiles(ResourceHelper.filterForLang(Language.EN));

        langKeysMap = ResourceHelper.getMapByKey(langList);
        engValuesMap = ResourceHelper.getMapByValue(ResourceHelper.loadProps(engList));

        // Generate base entities map
        baseEntities = new HashMap<String, String>();
        Map<String, String> engKeysMap = ResourceHelper.getMapByKey(engList);
        for (Map.Entry<String, String> entry : engKeysMap.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith(".entityName") && langKeysMap.containsKey(key)) {
                baseEntities.put(entry.getValue(), langKeysMap.get(key));
            }
        }
    }

    public void addCommentsToRow(StringBuffer buf, String row) {
        String value = ResourceHelper.getValue(row).toLowerCase();

        if (!addExistingValuesComment(buf, value)) {
            addBaseEntitiesComment(buf, value);
        }
        addExclusionsComment(buf, value);
    }

    private boolean addExistingValuesComment(StringBuffer buf, String value) {
        // Search for existing values
        List<String> keysForValue = engValuesMap.get(value);
        if (keysForValue != null) {
            for (String keyForValue : keysForValue) {
                String existingValue = langKeysMap.get(keyForValue);
                if (existingValue != null && !existingValue.trim().isEmpty()) {
                    appendCommentStr(buf, "Possible translation is \"" + existingValue + "\"");
                    return true;
                }
            }
        }
        return false;
    }

    private void addBaseEntitiesComment(StringBuffer buf, String value) {
        for (Map.Entry<String, String> entry : baseEntities.entrySet()) {
            String key = entry.getKey();
            if (value.matches(".*\\b" + key.toLowerCase() + "\\b.*")) {
                appendCommentStr(buf, "Possible translation for \"" + key + "\" is \"" + entry.getValue() + "\"");
            }
        }
    }

    private static void addExclusionsComment(StringBuffer buf, String value) {
        String exclusionsStr = "";
        for (String exclusion : exclusions) {
            if (value.matches(".*\\b" + exclusion.toLowerCase() + "\\b.*")) {
                if (!exclusionsStr.isEmpty()) {
                    exclusionsStr += ", ";
                }
                exclusionsStr += "\"" + exclusion + "\"";
            }
        }

        if (!exclusionsStr.isEmpty()) {
            appendCommentStr(buf, exclusionsStr + (exclusionsStr.contains(", ")
                    ? " are exclusions. Please, don't translate them!"
                    : " is an exclusion. Please, don't translate it!"));
        }
    }

    private static void appendCommentStr(StringBuffer buf, String str) {
        buf.append("# ").append(str).append("\n");
    }

    public static String getMainExclusionsComment() {
        StringBuffer buf = new StringBuffer();
        appendCommentStr(buf, "!!!IMPORTANT!!! Please don't translate the words from the following exclusion list!");
        appendCommentStr(buf, "");
        appendCommentStr(buf, "Exclusions in translation:");
        appendCommentStr(buf, "    * LDAP");
        appendCommentStr(buf, "    * URL");
        appendCommentStr(buf, "    * ISP");
        appendCommentStr(buf, "    * ID");
        appendCommentStr(buf, "    * CCID");
        appendCommentStr(buf, "    * RON");
        appendCommentStr(buf, "    * CPA");
        appendCommentStr(buf, "    * CPM");
        appendCommentStr(buf, "    * CPC");
        appendCommentStr(buf, "    * eCPA");
        appendCommentStr(buf, "    * eCPM");
        appendCommentStr(buf, "    * CTR");
        appendCommentStr(buf, "    * CSV");
        appendCommentStr(buf, "    * Excel");
        appendCommentStr(buf, "    * copyright: \"A Service of © Target RTB. 2020\"");
        appendCommentStr(buf, "    * Webwise, Discover, WD");
        appendCommentStr(buf, "    * Swift, BACS (payment methods)");
        appendCommentStr(buf, "    * On, Off");
        appendCommentStr(buf, "    * XSLT");
        appendCommentStr(buf, "    * kB, Mb");
        appendCommentStr(buf, "    * BIRT");
        appendCommentStr(buf, "    * Rptdesign (it is a file format)");
        appendCommentStr(buf, "    * pdf");
        appendCommentStr(buf, "    * FOROS, Open Internet Exchange");
        appendCommentStr(buf, "    * ARPM (average revenue per millennium)");
        appendCommentStr(buf, "    * ARPU (average revenue per user)");
        appendCommentStr(buf, "    * OF (oracle finance)");
        appendCommentStr(buf, "    * HTML");
        appendCommentStr(buf, "    * RSS, Atom");
        appendCommentStr(buf, "    * IBAN");
        appendCommentStr(buf, "    * MIME (in Mime type)");
        appendCommentStr(buf, "    * GMT");
        appendCommentStr(buf, "    * CMP");
        appendCommentStr(buf, "    * PWP (Pay When Paid)");
        appendCommentStr(buf, "    * HTTP, HTTPS");
        appendCommentStr(buf, "    * OK");
        appendCommentStr(buf, "    * N/A");
        appendCommentStr(buf, "    * Oracle");
        appendCommentStr(buf, "Next words can be both translated and not translated:");
        appendCommentStr(buf, "    * VAT");
        appendCommentStr(buf, "    * BIC");
        appendCommentStr(buf, "    * Walled garden");
        appendCommentStr(buf, "    * AdOps");
        appendCommentStr(buf, "    * PO, FC (Purchase Order and Frequency Caps)");
        appendCommentStr(buf, "    * Newsgate");
        appendCommentStr(buf, "    * Discover Request Mapping(s)");
        appendCommentStr(buf, "");
        appendCommentStr(buf, "Also the text within {...}, ${...} or ##...## SHOULD NOT BE TRANSLATED OR CHANGED!");
        buf.append("\n");

        return buf.toString();
    }
}
