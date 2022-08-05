package app.programmatic.ui.quick_search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import app.programmatic.ui.quick_search.model.QuickSearchResult;
import app.programmatic.ui.quick_search.model.TypeResults;
import app.programmatic.ui.quick_search.service.QuickSearchService;

import java.util.List;

@RestController
public class QuickSearchController {
    @Autowired
    private QuickSearchService quickSearchService;

    @RequestMapping(method = RequestMethod.GET, path = "/rest/quick_search", produces = "application/json")
    public List<TypeResults> quickSearch(@RequestParam(value = "quick_search") String searchText) {
        QuickSearchResult results = quickSearchService.search(searchText);
        return results.getTypes();
    }

}
