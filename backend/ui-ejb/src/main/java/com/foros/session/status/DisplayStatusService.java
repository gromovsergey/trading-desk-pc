package com.foros.session.status;

import com.foros.model.Identifiable;
import com.foros.model.account.Account;

import javax.ejb.Local;

/**
 * Display Status Service
 *
 */
@Local
public interface DisplayStatusService {
    /**
     * When something in entity is changed it is need to use this method to
     * recalculate display status
     *
     * @param entity
     *            changed entity
     */
    void update(Identifiable entity);

    /**
     * When something in entity is changed it is need to use this method to
     * recalculate display status
     *
     *
     * @deprecated Use update(entity) instead
     * @param entity
     *            changed entity
     * @param id
     *            it's id
     */
    @Deprecated
    void update(Object entity, Long id);

    void scheduleStatusEvictionOnCampaignCreditChange(Account account);
}
