package com.foros.session.creative;

import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.CreativeSizeExpansion;
import com.foros.model.security.AccountType;
import com.foros.service.ByIdLocatorService;
import com.foros.session.EntityTO;

import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

@Local
public interface CreativeSizeService extends ByIdLocatorService<CreativeSize> {
    void create(CreativeSize entity);

    CreativeSize update(CreativeSize entity);

    CreativeSize createCopy(Long id);

    void refresh(Long id);

    @Override
    CreativeSize findById(Long id);

    List<CreativeSize> findAll();

    Collection<CreativeSizeTO> findAll(boolean visibleOnly);

    List<EntityTO> getIndex();

    void delete(Long id);

    void undelete(Long id);

    List<CreativeSize> findByAccountType(AccountType accType);

    CreativeSize findTextSize();

    List<CreativeSizeTO> findAvailableSizes(Long accountTypeId);

    List<CreativeSize> findAllNotDeleted();

    long countCreativesByExpansions(Long sizeId, Collection<CreativeSizeExpansion> expansions);

    long countExpandableCreatives(Long sizeId);

    long countExpandableTags(Long sizeId);

    Long findTextSizeId();

    List<CreativeSize> findByAccountTypeAndSizeType(Long accountTypeId, Long sizeTypeId);

    CreativeSize findByName(String sizeName);
}
