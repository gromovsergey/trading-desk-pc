package com.foros.action.action;

import com.foros.action.BaseActionSupport;
import com.foros.action.reporting.treeFilter.TreeFilterActionSupport.TreeFilterHelper;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.session.EntityTO;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.util.StringUtil;
import com.foros.util.comparator.StatusNameTOComparator;
import com.foros.util.context.RequestContexts;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.ejb.EJB;

public class ActionLinksAction extends BaseActionSupport implements AdvertiserSelfIdAware, RequestContextsAware {
    private Long advertiserId;

    private List<Long> conversionIds = new ArrayList<>();
    private List<Long> groupIds = new ArrayList<>();
    private List<Long> campaignIds = new ArrayList<>();

    private Set<Long> groupIdsWithError = new LinkedHashSet<>();
    private List<EntityTO> groupsWithError;

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private CampaignCreativeGroupService ccgService;

    public String addGroupError(int index) {
        Long id = groupIds.get(index);
        groupIdsWithError.add(id);
        return "groups[" + id + "]";
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public List<Long> getCampaignIds() {
        return campaignIds;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        List<ConstraintViolationRule> rules = new ConstraintViolationRulesBuilder()
            .add("groups[(#index)]", "addGroupError(groups[0])", "violation.message")
            .add("groups[(#index)](#path)", "addGroupError(groups[0])", "violation.message")
            .rules();
        return rules;
    }

    public List<Long> getConversionIds() {
        return conversionIds;
    }

    public Map<String, Set<EntityTO>> getCreativesByError() {
        return getEntityErrors("creatives", null);
    }

    private Map<String, Set<EntityTO>> getEntityErrors(String path, List<EntityTO> listTo) {
        Map<String, Set<EntityTO>> map = new TreeMap<>(StringUtil.getLexicalComparator());
        Map<String, List<String>> fieldErrors = getFieldErrors();

        for (EntityTO to : listTo) {
            List<String> errors = fieldErrors.get(path + "[" + to.getId() + "]");
            for (String error : errors) {
                Set<EntityTO> tos = map.get(error);
                if (tos == null) {
                    tos = new TreeSet<>(StatusNameTOComparator.INSTANCE);
                    map.put(error, tos);
                }
                tos.add(to);
            }
        }
        return map;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public Map<String, Set<EntityTO>> getGroupsByError() {
        return getEntityErrors("groups", getGroupsWithError());
    }

    public List<EntityTO> getGroupsWithError() {
        if (groupsWithError == null) {
            groupsWithError = groupIdsWithError.isEmpty() ? Collections.<EntityTO> emptyList() : ccgService.getIndexByIds(groupIdsWithError);
        }
        return groupsWithError;
    }

    public List<Long> getSelectedIds() {
        return TreeFilterHelper.getSelectedIds(Collections.EMPTY_LIST);
    }

    public String save() {
        Set<Long> ccgIds = new LinkedHashSet<>(groupIds);
        for (Long campaignId : campaignIds) {
            List<TreeFilterElementTO> groups = ccgService.searchGroups(campaignId);
            for (TreeFilterElementTO group : groups) {
                ccgIds.add(group.getId());
            }
        }
        groupIds = new ArrayList<>(ccgIds);
        campaignCreativeGroupService.linkConversions(groupIds, conversionIds);

        return SUCCESS;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public void setCampaignIds(List<Long> campaignIds) {
        this.campaignIds = campaignIds;
    }

    public void setConversionIds(List<Long> conversionIds) {
        this.conversionIds = conversionIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    @ReadOnly
    public String showGroups() {
        return SUCCESS;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(advertiserId);
    }
}
