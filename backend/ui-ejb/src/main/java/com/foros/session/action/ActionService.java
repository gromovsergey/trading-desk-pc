package com.foros.session.action;

import com.foros.jaxb.adapters.CampaignGroupLink;
import com.foros.model.action.Action;
import com.foros.service.ByIdLocatorService;
import com.foros.session.EntityTO;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import org.joda.time.LocalDate;

@Local
public interface ActionService extends ByIdLocatorService<Action> {
    void create(Action entity);

    Action update(Action entity);

    void refresh(Long id);

    @Override
    Action findById(Long id);

    Action delete(Long id);

    Action undelete(Long id);

    List<TreeFilterElementTO> search(Long accountId);

    List<ActionTO> findByAccountIdAndDate(Long accountId, LocalDate fromDate, LocalDate toDate, boolean showDeleted);

    List<Action> findNonDeletedByAccountId(Long accountId);

    List<EntityTO> findEntityTOByMultipleParameters(Long accountId, Long campaignId, Long groupId, boolean showDeleted);

    boolean isLinked(Long actionId);

    Result<Action> get(ActionSelector selector);

    OperationsResult perform(Operations<Action> operations);

    Collection<CampaignGroupLink> getAssociations(Long actionId);
}
