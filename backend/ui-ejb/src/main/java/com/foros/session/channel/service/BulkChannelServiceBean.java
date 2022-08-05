package com.foros.session.channel.service;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelNamespace;
import com.foros.model.channel.ExpressionChannel;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.db.DBConstraint;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.channel.AdvertisingChannelQueryImpl;
import com.foros.util.PersistenceUtils;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.hibernate.FlushMode;

@Stateless(name = "BulkChannelService")
@Interceptors({RestrictionInterceptor.class,  ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class BulkChannelServiceBean implements BulkChannelService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;
    
    @EJB
    private ExpressionChannelService expressionChannelService;
    
    @EJB
    private BehavioralChannelService behavioralChannelService;

    @EJB
    private AudienceChannelService audienceChannelService;
    
    @EJB
    private QueryExecutorService executorService;
    
    @EJB
    private ValidationService validationService;

    private ChannelOperationsPreprocessor preprocessor = new ChannelOperationsPreprocessor() {
        @Override
        protected EntityManager getEm() {
            return em;
        }
    };

    @Override
    @Interceptors({CaptureChangesInterceptor.class})
    @Validate(validation = "Operations.integrity", parameters = {"#channelOperations", "'channel'"})
    public OperationsResult perform(Operations<Channel> channelOperations) throws Exception {
        // to prevent Hibernate doing auto-flush
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.MANUAL);

        fetch(channelOperations);

        preprocessor.preProcess(channelOperations);

        validationService.validate("BulkChannel.merge", channelOperations).throwIfHasViolations();
        
        List<Long> result = new ArrayList<Long>();
        for (Operation<Channel> channelMergeOperation : channelOperations.getOperations()) {
            result.add(processMergeOperation(channelMergeOperation));
        }

        try {
            em.flush();
        } catch (PersistenceException e) {
            if (DBConstraint.CHANNEL_NAME.match(e)) {
                validationService.validateInNewTransaction("BulkChannel.countryNameConstraintViolations", ChannelNamespace.ADVERTISING, channelOperations)
                        .throwIfHasViolations();
            }
            throw e;
        }

        // let's Hibernate do rest of the job
        PersistenceUtils.getHibernateSession(em).setFlushMode(FlushMode.AUTO);
        
        return new OperationsResult(result);
    }
    
    private void fetch(Operations<Channel> channelOperations) {
        List<Long> channelIds = prepareChannelIds(channelOperations);
        if (!channelIds.isEmpty()) {
            new AdvertisingChannelQueryImpl()
                .matchedIds(channelIds)
                .asBean()
                .executor(executorService)
                .list();
        }
    }

    private List<Long> prepareChannelIds(Operations<Channel> channelOperations) {
        List<Long> channelIds = new ArrayList<Long>();
        for (Operation<Channel> operation : channelOperations.getOperations()) {
            if (operation != null && operation.getEntity() != null && operation.getEntity().getId() != null) {
                channelIds.add(operation.getEntity().getId());
            }
        }
        return channelIds;
    }
    
    private Long processMergeOperation(Operation<Channel> mergeOperation) throws Exception {
        Channel channel = mergeOperation.getEntity();
        
        if (channel instanceof ExpressionChannel) {
            switch (mergeOperation.getOperationType()) {
            case CREATE:
                return expressionChannelService.createBulk((ExpressionChannel) channel);
            case UPDATE:
                return expressionChannelService.updateBulk((ExpressionChannel) channel);
            }
        } else if (channel instanceof BehavioralChannel) {
            switch (mergeOperation.getOperationType()) {
            case CREATE:
                return behavioralChannelService.createBulk((BehavioralChannel) channel);
            case UPDATE:
                return behavioralChannelService.updateBulk((BehavioralChannel) channel);
            }
        } else if (channel instanceof AudienceChannel) {
            switch (mergeOperation.getOperationType()) {
                case CREATE:
                    return audienceChannelService.createBulk((AudienceChannel) channel);
                case UPDATE:
                    return audienceChannelService.updateBulk((AudienceChannel) channel);
            }
        }

        throw new RuntimeException(mergeOperation.getOperationType() + " not supported!");
    }

}
