package com.foros.session.admin.searchEngine;

import com.foros.model.admin.SearchEngine;

import java.util.List;
import javax.ejb.Local;

@Local
public interface SearchEngineService {

    List list();
    
    Long create(SearchEngine searchEngine);
    
    void update(SearchEngine searchEngine);
    
    void delete(Long id);

    SearchEngine findById(Long id);
}
