package com.foros.session.site;

import com.foros.model.site.WDTag;

import javax.ejb.Local;
import java.util.List;

@Local
public interface WDTagService {
    WDTag view(Long id);

    WDTag find(Long id);

    List<WDTagTO> findBySite(Long siteId);

    Long create(WDTag entity);

    void update(WDTag entity);

    void delete(Long id);

    void undelete(Long id);
}
