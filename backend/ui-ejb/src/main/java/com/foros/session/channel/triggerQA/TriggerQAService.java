package com.foros.session.channel.triggerQA;

import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.channel.exceptions.UpdateException;
import com.foros.util.jpa.DetachedList;

import java.util.List;
import javax.ejb.Local;

@Local
public interface TriggerQAService {

    DetachedList<TriggerQATO> search(TriggerQASearchParameters searchParameter);

    Result<TriggerQATO> get(TriggerQASelector selector);

    /**
     * Submit the list of edited triggers. Id and channel assignment are ignored in
     * TriggerQATO. In spite of actual data update version field is be updated for all submitted
     * triggers to "rotate" triggers for search.
     * @param triggers list of triggers
     * @throws com.foros.session.channel.exceptions.UpdateException if update failed
     */
    public void update(List<TriggerQATO> triggers) throws UpdateException;

    public OperationsResult perform(Operations<TriggerQATO> triggerOperations) throws UpdateException;

}
