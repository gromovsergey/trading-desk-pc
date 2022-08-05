package com.foros.session.channel.triggerQA;

import com.foros.util.StringUtil;
import com.foros.util.TriggerUtil;
import com.foros.util.url.TriggerQANormalization;

import java.util.ArrayList;
import java.util.List;

public class TriggerQASearchPhrase extends SearchPhraseBase {
    protected String normalizedPhrase;

    private TriggerQASearchPhrase(String normalizedPhrase, TriggerQAType type) {
        this(type);
        this.normalizedPhrase = normalizedPhrase;
    }

    private TriggerQASearchPhrase(TriggerQAType type) {
        super(type);
    }

    @Override
    public List<String> getSearchPhrases() {
        List<String> phrases = new ArrayList<>();
        if (normalizedPhrase != null) {
            phrases.add(normalizedPhrase);
        }

        return phrases;
    }

    public static SearchPhraseBase create(String countryCode, String phrase, TriggerQAType type) {
        switch (type) {
        case URL:
            String normalizedPhrase = tryNormalize(countryCode, phrase, type);
            if (StringUtil.isPropertyEmpty(normalizedPhrase)) {
                return createEmptyTriggerQASearchPhrase(type);
            }
            return new TriggerQASearchPhrase(normalizedPhrase, type);
        case KEYWORD:
            return createKeywordsSearchPhrase(countryCode, phrase, type);
        default:
            throw new IllegalArgumentException("Only specific trigger types are allowed to create search phrase filter");
        }
    }

    private static SearchPhraseBase createKeywordsSearchPhrase(String countryCode, String phrase, TriggerQAType type) {
        List<String> phraseList = TriggerUtil.splitPhrase(phrase);
        if (phraseList.isEmpty()) {
            return createEmptyTriggerQASearchPhrase(type);
        }

        CompositeSearchPhrase compositeSearchPhrase = new CompositeSearchPhrase(type);
        for (String string : phraseList) {
            String normalizedPhrase;
            try {
                normalizedPhrase = tryNormalize(countryCode, string, type);
            } catch (Exception e) {
                continue;
            }
            if (StringUtil.isPropertyEmpty(normalizedPhrase)) {
                continue;
            }
            normalizedPhrase = StringUtil.unquote(normalizedPhrase, false);
            compositeSearchPhrase.add(new TriggerQASearchPhrase(normalizedPhrase, type));
        }

        return compositeSearchPhrase;
    }

    private static SearchPhraseBase createEmptyTriggerQASearchPhrase(TriggerQAType type) {
        return new TriggerQASearchPhrase(type);
    }

    private static String tryNormalize(String countryCode, String searchPhrase, TriggerQAType type) {
        if (StringUtil.isPropertyEmpty(searchPhrase)) {
            return null;
        }

        if (searchPhrase != null && searchPhrase.startsWith("-")) {
            searchPhrase = searchPhrase.substring(1);
        }

        return TriggerQANormalization.normalizeTrigger(countryCode, type, searchPhrase);
    }

}
