package com.foros.session.admin.globalParams;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.FrequencyCap;
import com.foros.model.admin.GlobalParam;
import com.foros.model.admin.WDFrequencyCapWrapper;
import com.foros.model.currency.Source;
import com.foros.model.security.ActionType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;
import com.foros.session.security.AuditService;
import com.foros.util.EntityUtils;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.StringUtils;

@Stateless(name = "GlobalParamsService")
@Interceptors({RestrictionInterceptor.class, PersistenceExceptionInterceptor.class, ValidationInterceptor.class})
public class GlobalParamsServiceBean implements GlobalParamsService {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;
    
    @EJB
    private AuditService auditService;

    @EJB
    private CurrencyExchangeService currencyExchangeService;

    @Override
    public GlobalParam find(String name) {
        return em.find(GlobalParam.class, name);
    }

    @Override
    public GlobalParam findByValue(String value) {
        try {
            return (GlobalParam) em.createNamedQuery("GlobalParam.findByValue")
                    .setParameter("value", value)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @Restrict(restriction = "GlobalParams.view")
    public GlobalParam view(String name) {
        return find(name);
    }

    @Override
    @Restrict(restriction = "GlobalParams.update")
    public void updateExchangeRateUpdate(GlobalParam exchangeRateUpdate) {
        boolean isValueChanged = updateInternal(exchangeRateUpdate);
        if (isValueChanged) {
            Source source = Source.valueOf(exchangeRateUpdate.getValue());
            currencyExchangeService.switchExchangeUpdateTo(source);
        }
    }

    @Override
    @Restrict(restriction = "GlobalParams.update")
    public void updateWDTagMapping(GlobalParam wdTagMapping) {
        updateInternal(wdTagMapping);
    }

    private boolean updateInternal(GlobalParam param) {
        GlobalParam existingParam = em.find(GlobalParam.class, param.getName());
        boolean isValueChanged = true;
        if (existingParam != null) {
            if (StringUtils.equals(existingParam.getValue(), param.getValue())) {
                isValueChanged = false;
            }
            em.merge(param);
        } else {
            em.persist(param);
        }
        return isValueChanged;
    }

    @Override
    @Restrict(restriction = "WDFrequencyCaps.view")
    public FrequencyCap getWDFrequencyCap(String name) {
        GlobalParam idHolder = find(name);

        if (StringUtil.isPropertyEmpty(idHolder.getValue())) {
            return null;
        }

        long id = Long.parseLong(idHolder.getValue());
        FrequencyCap freqCap = em.find(FrequencyCap.class, id);
        
        return freqCap;
    }

    @Override
    @Restrict(restriction = "WDFrequencyCaps.update")
    @Validate(validation = "FrequencyCap.updateWDFrequencyCaps",
            parameters = {"#eventsFrequencyCap", "#categoriesFrequencyCap", "#channelsFrequencyCap"})
    @Interceptors({CaptureChangesInterceptor.class})
    public void updateWDFrequencyCaps(FrequencyCap eventsFrequencyCap,
                                      FrequencyCap categoriesFrequencyCap,
                                      FrequencyCap channelsFrequencyCap) {
        WDFrequencyCapWrapper wdFrequencyCapWrapper = new WDFrequencyCapWrapper();

        Map<String, FrequencyCap> elementsToRemove = new HashMap<String, FrequencyCap>();

        FrequencyCap existingEventsFrequencyCap =
                updateWDFrequencyCapInternal(eventsFrequencyCap, GlobalParamsService.WD_FREQ_CAP_EVENT, elementsToRemove);
        FrequencyCap existingCategoriesFrequencyCap =
                updateWDFrequencyCapInternal(categoriesFrequencyCap, GlobalParamsService.WD_FREQ_CAP_CATEGORY, elementsToRemove);
        FrequencyCap existingChannelsFrequencyCap =
                updateWDFrequencyCapInternal(channelsFrequencyCap, GlobalParamsService.WD_FREQ_CAP_CHANNEL, elementsToRemove);

        wdFrequencyCapWrapper.setEventsFrequencyCap(existingEventsFrequencyCap);
        wdFrequencyCapWrapper.setCategoriesFrequencyCap(existingCategoriesFrequencyCap);
        wdFrequencyCapWrapper.setChannelsFrequencyCap(existingChannelsFrequencyCap);

        Map<String, FrequencyCap> resElementsToRemove = new HashMap<String, FrequencyCap>();
        for (Map.Entry<String, FrequencyCap> entry : elementsToRemove.entrySet()) {
            if (entry.getKey().equals(GlobalParamsService.WD_FREQ_CAP_EVENT)) {
                resElementsToRemove.put("eventsFrequencyCap", entry.getValue());
            } else if (entry.getKey().equals(GlobalParamsService.WD_FREQ_CAP_CHANNEL)) {
                resElementsToRemove.put("channelsFrequencyCap", entry.getValue());
            } else if (entry.getKey().equals(GlobalParamsService.WD_FREQ_CAP_CATEGORY)) {
                resElementsToRemove.put("categoriesFrequencyCap", entry.getValue());
            }
        }

        wdFrequencyCapWrapper.setElementsToRemove(resElementsToRemove);

        auditService.audit(wdFrequencyCapWrapper, ActionType.UPDATE);
        em.flush();
    }

    private FrequencyCap updateWDFrequencyCapInternal(FrequencyCap cap, String name, Map<String, FrequencyCap> elementsToRemove) {
        // Set empty elements to null to prevent excessive audit log data
        if (cap.getPeriodSpan() != null && cap.getPeriodSpan().getValue() == null) {
            cap.setPeriodSpan(null);
        }
        
        if (cap.getWindowLengthSpan() != null && cap.getWindowLengthSpan().getValue() == null) {
            cap.setWindowLengthSpan(null);
        }

        if (cap.getId() == null && cap.isZeroOrNull()) { // no existing value and no new value, so do nothing
            return null;
        } else if (cap.getId() == null) { // empty existing value, not empty new value, so add new entry
            em.persist(cap);
            em.flush();
            find(name).setValue(cap.getId().toString());

            return cap;
        } else if (cap.isZeroOrNull()) { // not empty existing value, empty new value, so remove entry
            FrequencyCap frequencyCap = em.find(FrequencyCap.class, cap.getId());
            elementsToRemove.put(name, frequencyCap);
            em.remove(frequencyCap);
            find(name).setValue(null);

            return null;
        } else { // update not empty existing value to not empty new value
            FrequencyCap existingCap = em.merge(cap);

            return existingCap;
        }
    }

    @Override
    @Restrict(restriction = "FraudConditions.view")
    public GlobalParam getUserInactivityTimeout() {
        GlobalParam param = find(USER_INACTIVITY_TIMEOUT);
        if (param == null) {
            throw new RuntimeException("Global parameter 'USER_INACTIVITY_TIMEOUT' not found.");
        }
        return param;
    }

    @Override
    @Restrict(restriction = "FraudConditions.update")
    public GlobalParam updateUserInactivityTimeout(GlobalParam userInactivityTimeOut) {
        GlobalParam param = find(GlobalParamsService.USER_INACTIVITY_TIMEOUT);
        param.setValue(userInactivityTimeOut.getValue());
        EntityUtils.checkEntityVersion(param, userInactivityTimeOut.getVersion());
        return param;
    }
}
