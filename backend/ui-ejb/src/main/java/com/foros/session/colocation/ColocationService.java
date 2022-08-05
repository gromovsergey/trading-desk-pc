package com.foros.session.colocation;

import com.foros.model.isp.Colocation;
import com.foros.session.EntityTO;
import com.foros.session.query.PartialList;

import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

@Local
public interface ColocationService {

    List<EntityTO> getIndex(Long accountId);

    List<EntityTO> getByAccountIds(Collection<Long> accountIds);

    Collection<ColocationTO> search(Long accountId);

    PartialList<Colocation> get(ColocationSelector selector);

    Colocation find(Long id);

    Colocation view(Long id);

    Colocation create(Colocation colocation);

    Colocation update(Colocation colocation);

    void delete(Long id);

    void undelete(Long id);

    // void refresh(Long id);
}
