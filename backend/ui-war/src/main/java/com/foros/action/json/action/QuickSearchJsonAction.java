package com.foros.action.json.action;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.model.quicksearch.QuickSearchResultItem;
import com.foros.model.quicksearch.Type;
import com.foros.session.quicksearch.QuickSearchService;

import java.util.Collection;
import java.util.Map;
import javax.ejb.EJB;

public class QuickSearchJsonAction extends AbstractXmlAction<QuickSearchResultContainer> {

    @EJB
    private QuickSearchService quickSearchService;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected QuickSearchResultContainer generateModel() throws ProcessException {
        Map<Type, Collection<QuickSearchResultItem>> results = quickSearchService.search(name);
        QuickSearchResultContainer container = new QuickSearchResultContainer(name, results);
        return container;
    }
}
