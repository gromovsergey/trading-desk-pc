package com.foros.service.timed;

import com.foros.monitoring.TimedManagerServiceM;
import com.foros.monitoring.StatusHelper;
import com.foros.util.PersistenceUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;

@Startup
@Singleton(name="TimedManagerService")
public class TimedManagerServiceBean implements TimedManagerService, TimedManagerServiceM {
    private static final Logger logger = Logger.getLogger(TimedManagerServiceBean.class.getName());
    private String instanceId;

    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @PostConstruct
    public void init() {
        try {
            instanceId = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Can't fetch instance id", e);
        }
    }

    @Override
    public <T> boolean canProcess(final Class<T> service, final Long period) {
        String serviceName = service.getName();
        try {
            return canProcessImpl(serviceName, period);
        } catch (OptimisticLockException e) {
            logger.log(Level.INFO, "Service " + serviceName + ", instance " + instanceId + ": Optimistic Lock.");
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Service " + serviceName + ", instance " + instanceId + ": exception occurred", t);
        }

        return false;
    }

    public <T> boolean canProcessImpl(final String serviceName, final Long period) {
        ServiceModel model = findModel(serviceName);
        if (model == null) {
            return createModel(serviceName);
        }

        if (model.getInstanceId().equals(instanceId)) {
            return updateSelfModel(model, serviceName);
        }

        if (!StatusHelper.isLastProcessedTimeInThreshold(period, model.getVersion().getTime())) {
            return updateOtherModel(model, serviceName);
        }

        return false;
    }

    private ServiceModel findModel(final String serviceName) {
        try {
            return em.find(ServiceModel.class, serviceName);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    private boolean createModel(final String serviceName) {
        try {
            ServiceModel model = new ServiceModel();
            model.setServiceId(serviceName);
            model.setInstanceId(instanceId);

            em.persist(model);
            em.flush();

            logger.log(Level.INFO, "Service {0} is running on {1} instance.", new Object[]{serviceName, instanceId});

            return true;
        } catch (EntityExistsException e) {
            logger.log(Level.INFO, "Some instance already captured service {0}. Instance {1} will sleep.", new Object[]{serviceName, instanceId});
            return false;
        }
    }

    private boolean updateSelfModel(ServiceModel model, final String serviceName) {
        PersistenceUtils.performHibernateLock(em, model);
        logger.log(Level.INFO, "Service {0} is running on {1} instance.", new Object[]{serviceName, instanceId});

        return true;
    }

    private boolean updateOtherModel(ServiceModel model, final String serviceName) {
        String prevSrvId = model.getServiceId();
        model.setInstanceId(instanceId);

        em.merge(model);
        em.flush();
        logger.log(Level.INFO, "Service {0} will be realized by {1} instance. Previous instance {2} doesn't run this service at the expected time.",
            new Object[]{serviceName, instanceId, prevSrvId});

        return true;
    }

    @Override
    public String getStatus() {
        StringBuilder result = new StringBuilder();

        List<ServiceModel> modelList = em.createNamedQuery("ServiceModel.findAll").getResultList();
        for (ServiceModel model: modelList) {
            if (result.length() != 0) {
                result.append("\n");
            }
            result.append(model.getServiceId());
            result.append(": ");
            result.append(model.getInstanceId());
            result.append(" [");
            result.append(model.getVersion());
            result.append("]");
        }

        return result.toString();
    }
}
