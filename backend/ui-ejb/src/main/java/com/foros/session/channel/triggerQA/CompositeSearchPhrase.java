package com.foros.session.channel.triggerQA;

import java.util.ArrayList;
import java.util.List;

public class CompositeSearchPhrase extends SearchPhraseBase {

    private List<SearchPhraseBase> phraseList = new ArrayList<SearchPhraseBase>();

    CompositeSearchPhrase(TriggerQAType type) {
        super(type);
    }

    public void add(SearchPhraseBase phrase) {
        phraseList.add(phrase);
    }

    public List<SearchPhraseBase> getPhraseList() {
        return phraseList;
    }

    @Override
    public List<String> getSearchPhrases() {
        List<String> res = new ArrayList<>();
        for (SearchPhraseBase searchPhrase : phraseList) {
            List<String> searchPhrases = searchPhrase.getSearchPhrases();
            if (searchPhrases == null) {
                return null;
            }
            res.addAll(searchPhrases);
        }
        return res;
    }
}
