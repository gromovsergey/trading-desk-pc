package com.foros.action.campaign.campaignGroup;

import com.foros.model.ExtensionProperty;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.util.OrderedEntry;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SaveKeywordsAction extends EditSaveKeywordsActionBase {
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("\r\n|\n");
    private static final ExtensionProperty<Long> ROW_NUMBER = new ExtensionProperty<Long>(Long.class);

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("ccgKeywords[(#index)].(#path)", "'keywordsText'", "fieldError(groups[0], groups[1], violation.message)")
            .add("ccgKeywords", "'keywordsText'", "violation.message")
            .rules();

    private List<OrderedEntry<String>> errors;

    private List<CCGKeyword> keywords;

    public String update() {
        errors = new LinkedList<OrderedEntry<String>>();

        keywords = parseKeywords();
        if (!errors.isEmpty()) {
            processErrors();
            return INPUT;
        }

        ccgKeywordService.update(keywords, id, ccgVersion);
        return SUCCESS;
    }

    public String fieldError(int index, String path, String message) {
        String propertyName = getText("ccgKeyword." + path);
        Long rowNumber = keywords.get(index).getProperty(ROW_NUMBER);
        return getText("errors.fieldError.withLineNumber", Arrays.asList(rowNumber, propertyName, message));
    }

    private void processErrors() {
        Collections.sort(errors);
        for (OrderedEntry<String> error : errors) {
            addFieldError("keywordsText", error.getEntry());
        }
    }

    private List<CCGKeyword> parseKeywords() {
        NumberFormat nf = CurrentUserSettingsHolder.getNumberFormat();

        List<CCGKeyword> keywords = new LinkedList<CCGKeyword>();
        if (keywordsText != null && keywordsText.length() > 0) {
            String[] rows = NEWLINE_PATTERN.split(keywordsText);

            for (int i = 0; i < rows.length; i++) {
                String row = rows[i];
                row = row.replace('\t', ' ').trim();
                String[] values = row.split("\\*\\*");

                String originalKeyword = null;
                BigDecimal maxCpcBid = null;
                String clickUrl = null;
                KeywordTriggerType triggerType = null;

                if (values.length >= 1) {
                    originalKeyword = values[0].trim();
                }

                if (values.length >= 2 && values[1].trim().length() > 0) {
                    try {
                        ParsePosition pos = new ParsePosition(0);
                        String trimmedValue = values[1].trim();
                        Number parsedValue = nf.parse(trimmedValue, pos);

                        if (pos.getIndex() != trimmedValue.length()) {
                            throw new NumberFormatException();
                        }

                        maxCpcBid = BigDecimal.valueOf(parsedValue.doubleValue());
                    } catch (NumberFormatException e) {
                        String error = getText("errors.fieldError.withLineNumber", new String[]{
                                String.valueOf(i + 1),
                                getText("ccgKeyword.maxCpcBid"),
                                getText("errors.keyword.invalidCPC")});
                        errors.add(new OrderedEntry<String>(i + 1, error));
                    }
                }

                if (values.length >= 3 && values[2].trim().length() > 0) {
                    try {
                        triggerType = KeywordTriggerType.byName(values[2].trim());
                    } catch (Exception e) {
                        String error = getText("errors.fieldError.withLineNumber", new String[]{
                                String.valueOf(i + 1),
                                getText("ccgKeyword.type"),
                                getText("errors.keyword.invalidType")});
                        errors.add(new OrderedEntry<String>(i + 1, error));
                    }
                }

                if (values.length == 4 && values[3].trim().length() > 0) {
                    clickUrl = values[3].trim();
                }

                if (values.length > 4) {
                    String error = getText("errors.lineNumber", new String[]{
                            String.valueOf(i + 1),
                            getText("errors.invalidInput")});
                    errors.add(new OrderedEntry<String>(i + 1, error));
                }

                if (triggerType != null) {
                    keywords.add(createCCGKeyword(originalKeyword, maxCpcBid, clickUrl, triggerType, i + 1));
                } else {
                    keywords.add(createCCGKeyword(originalKeyword, maxCpcBid, clickUrl, KeywordTriggerType.PAGE_KEYWORD, i + 1));
                    keywords.add(createCCGKeyword(originalKeyword, maxCpcBid, clickUrl, KeywordTriggerType.SEARCH_KEYWORD, i + 1));
                }
            }
        }

        return keywords;
    }

    private CCGKeyword createCCGKeyword(String originalKeyword, BigDecimal maxCpcBid, String clickUrl,
                                        KeywordTriggerType triggerType, long rowNumber) {
        CCGKeyword ccgKeyword = new CCGKeyword();
        ccgKeyword.setCreativeGroup(new CampaignCreativeGroup(id));
        ccgKeyword.setOriginalKeyword(originalKeyword);
        ccgKeyword.setMaxCpcBid(maxCpcBid);
        ccgKeyword.setClickURL(clickUrl);
        ccgKeyword.setTriggerType(triggerType);
        ccgKeyword.setProperty(ROW_NUMBER, rowNumber);

        return ccgKeyword;
    }

    @Override
    public Map<String, List<String>> getFieldErrors() {
        Map<String, List<String>> fieldErrors = super.getFieldErrors();

        if (fieldErrors.containsKey("keywordsText")) {
            LinkedHashSet<String> unique = new LinkedHashSet<String>(fieldErrors.get("keywordsText"));
            fieldErrors.put("keywordsText", new ArrayList<String>(unique));
        }

        return fieldErrors;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }
}
