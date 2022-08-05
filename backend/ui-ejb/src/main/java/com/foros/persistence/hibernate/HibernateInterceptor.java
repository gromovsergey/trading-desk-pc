package com.foros.persistence.hibernate;

import com.foros.changes.ChangesInterceptorHandler;
import com.foros.session.JdbcTemplateCleaner;
import com.foros.session.campaign.CampaignBudgetHibernateHandler;
import com.foros.session.channel.ChannelTriggersHibernateHandler;
import com.foros.session.channel.KeywordChannelsHibernateHandler;
import com.foros.session.channel.targeting.TargetingChannelsHibernateHandler;
import com.foros.session.status.DisplayStatusHandler;
import com.foros.util.command.AbstractNothingReturnHibernateWork;

import java.io.Serializable;

import javax.transaction.SystemException;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.impl.SessionImpl;
import org.hibernate.transaction.CMTTransaction;
import org.hibernate.type.Type;

public class HibernateInterceptor extends EmptyInterceptor {

    private ChangesInterceptorHandler changesHandler = new ChangesInterceptorHandler();
    private DisplayStatusHandler displayStatusHandler = new DisplayStatusHandler();
    private KeywordChannelsHibernateHandler keywordChannelsHibernateHandler = new KeywordChannelsHibernateHandler();
    private TargetingChannelsHibernateHandler targetingChannelsHibernateHandler = new TargetingChannelsHibernateHandler();
    private ChannelTriggersHibernateHandler channelTriggersHibernateHandler = new ChannelTriggersHibernateHandler();
    private CampaignBudgetHibernateHandler campaignBudgetHibernateHandler = new CampaignBudgetHibernateHandler();
    private EvictCacheHibernateInterceptor evictCacheInterceptor = new EvictCacheHibernateInterceptor();
    private PostgresChangesTrackerInterceptor postgresChangesTrackerInterceptor = new PostgresChangesTrackerInterceptor(evictCacheInterceptor);
    private JdbcTemplateCleaner jdbcTemplateCleaner = new JdbcTemplateCleaner();

    public ChangesInterceptorHandler getChangesHibernateInterceptor() {
        return changesHandler;
    }

    public DisplayStatusHandler getDisplayStatusHibernateInterceptor() {
        return displayStatusHandler;
    }

    public KeywordChannelsHibernateHandler getKeywordChannelsHibernateInterceptor() {
        return keywordChannelsHibernateHandler;
    }

    public TargetingChannelsHibernateHandler getTargetingChannelsHibernateHandler() {
        return targetingChannelsHibernateHandler;
    }

    public ChannelTriggersHibernateHandler getChannelTriggersHibernateInterceptor() {
        return channelTriggersHibernateHandler;
    }

    public CampaignBudgetHibernateHandler getCampaignBudgetHibernateInterceptor() {
        return campaignBudgetHibernateHandler;
    }

    public EvictCacheHibernateInterceptor getEvictCacheInterceptor() {
        return evictCacheInterceptor;
    }

    public JdbcTemplateCleaner getJdbcTemplateCleaner() {
        return jdbcTemplateCleaner;
    }

    @Override
    public boolean onFlushDirty(Object object, Serializable id, Object[] newValues, Object[] oldValues,
            String[] properties, Type[] types) {
        changesHandler.onFlushDirty(object, id, newValues, oldValues, properties, types);
        touchTagByEntity(object);

        return false;
    }

    @Override
    public boolean onSave(Object object, Serializable id, Object[] newValues, String[] properties, Type[] types) {
        changesHandler.onSave(object, id, newValues, properties, types);
        return false;
    }

    @Override
    public void onDelete(Object object, Serializable id, Object[] newValues, String[] properties, Type[] types) {
        changesHandler.onDelete(object, id, newValues, properties, types);
        touchTagByEntity(object);
    }

    private void touchTagByEntity(Object object) {
        evictCacheInterceptor.touchTagByEntity(object);
    }

    /**
     * See com.foros.persistence.hibernate.TransactionCompleteListener for details
     */
    @Override
    public void beforeTransactionCompletion(Transaction transaction) {
        try {
            processBeforeTransactionCompletion();
        } catch (Exception ex) {
            try {
                // it's kind of hack. we do it because CMTTransaction.begun is false at the time we rollback it.
                ((CMTTransaction)transaction).getTransaction().setRollbackOnly();
            } catch (SystemException e) {
                throw new TransactionException("Could not set transaction to rollback only", e);
            }
            throw new RuntimeException(ex);
        }
    }

    public void processBeforeTransactionCompletion() {
        keywordChannelsHibernateHandler.handle();
        targetingChannelsHibernateHandler.handle();
        campaignBudgetHibernateHandler.handle();
        changesHandler.processChanges();
        channelTriggersHibernateHandler.updateChannels();

        displayStatusHandler.processDisplayStatuses();
        postgresChangesTrackerInterceptor.evict();
        evictCacheInterceptor.evict();

        jdbcTemplateCleaner.clear();
    }

    public PostgresChangesTrackerInterceptor getPostgresChangesTrackerInterceptor() {
        return postgresChangesTrackerInterceptor;
    }

    /**
     * Abstract work for direct access at {@link ChangesInterceptorHandler}
     */
    public abstract static class AbstractHibernateInterceptorWork extends AbstractNothingReturnHibernateWork {
        @Override
        public void executeIt(Session session) {
            SessionImpl sessionImpl = (SessionImpl)session;

            HibernateInterceptor interceptor = (HibernateInterceptor) sessionImpl.getInterceptor();

            process(session, interceptor);
        }

        protected abstract void process(Session session, HibernateInterceptor interceptor);

    }


}
