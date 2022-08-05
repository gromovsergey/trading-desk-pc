package com.foros.action.xml.options;

import com.foros.action.xml.options.annotation.AccountId;
import com.foros.action.xml.options.converter.NamedTOConverter;
import com.foros.action.xml.options.filter.OptionStatusFilter;
import com.foros.security.principal.SecurityContext;
import com.foros.session.EntityTO;
import com.foros.session.colocation.ColocationService;
import com.foros.util.EntityUtils;

import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;

public class ColocationsByIdXmlAction extends AbstractOptionsByAccountAction<EntityTO> {

    @EJB
    private ColocationService colocationService;

    private String ispId;

    public ColocationsByIdXmlAction() {
        super(new NamedTOConverter(false), new OptionStatusFilter(SecurityContext.isInternal()));
    }

    @AccountId
    public String getIspId() {
        return ispId;
    }

    public void setIspId(String ispId) {
        this.ispId = ispId;
    }

    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        List<EntityTO> list = colocationService.getIndex(accountId);
        return EntityUtils.sortByStatus(list);
    }
}
