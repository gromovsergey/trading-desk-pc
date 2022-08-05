package com.foros.session.admin.wdRequestMapping;

import com.foros.cache.NamedCO;
import com.foros.model.admin.WDRequestMapping;
import com.foros.service.ByIdLocatorService;

import java.util.List;
import javax.ejb.Local;

@Local
public interface WDRequestMappingService extends ByIdLocatorService<WDRequestMapping> {
    List<WDRequestMapping> findAll();

    public void create(WDRequestMapping entity);

    public WDRequestMapping update(WDRequestMapping entity);

    public void delete(Long id);

    void refresh(Long id);

    @Override
    WDRequestMapping findById(Long id);
    
    List<NamedCO<Long>> getIndex();
}
