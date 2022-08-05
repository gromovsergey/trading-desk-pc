package com.foros.action.creative.display;

import com.foros.action.BaseActionSupport;
import com.foros.action.reporting.treeFilter.TreeFilterActionSupport.TreeFilterHelper;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.Status;
import com.foros.session.EntityTO;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.creative.CreativeService;
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

public class CreativeLinksAction extends BaseActionSupport implements AdvertiserSelfIdAware, RequestContextsAware {

    private static List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
        .add("groups[(#index)]", "addGroupError(groups[0])", "violation.message")
        .add("groups[(#index)](#path)", "addGroupError(groups[0])", "violation.message")
        .add("creatives[(#index)]", "addCreativeError(groups[0])", "violation.message")
        .add("creatives[(#index)](#path)", "addCreativeError(groups[0])", "violation.message")
        .add("frequencyCap(#path)", "addFrequencyCapError(groups[0])", "violation.message")
        .rules();
    private Long advertiserId;
    private boolean display;

    private List<Long> creativeIds = new ArrayList<>();
    private List<Long> groupIds = new ArrayList<>();
    private List<Long> campaignIds = new ArrayList<>();

    private Set<Long> groupIdsWithError = new LinkedHashSet<>();
    private List<EntityTO> groupsWithError;

    private Set<Long> creativeIdsWithError = new LinkedHashSet<>();
    private List<EntityTO> creativesWithError;

    @EJB
    private CampaignCreativeService campaignCreativeService;

    @EJB
    private CreativeService creativeService;

    @EJB
    private CampaignCreativeGroupService ccgService;

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(advertiserId);
    }

    @ReadOnly
    public String showGroups() {
        return SUCCESS;
    }

    public String save() {
        Set<Long> ccgIds = new LinkedHashSet<>(groupIds);
        for (Long campaignId : campaignIds) {
            List<TreeFilterElementTO> groups = ccgService.searchGroups(campaignId);
            for (TreeFilterElementTO group : groups) {
                if (Status.DELETED != group.getStatus()) {
                    ccgIds.add(group.getId());
                }
            }
        }
        groupIds = new ArrayList<>(ccgIds);

        campaignCreativeService.createAll(advertiserId, creativeIds, groupIds, isDisplay());
        return SUCCESS;
    }

    public List<Long> getSelectedIds() {
        return TreeFilterHelper.getSelectedIds(Collections.EMPTY_LIST);
    }

    public List<Long> getCreativeIds() {
        return creativeIds;
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public List<Long> getCampaignIds() {
        return campaignIds;
    }

    public void setCampaignIds(List<Long> campaignIds) {
        this.campaignIds = campaignIds;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String addGroupError(int index) {
        Long id = groupIds.get(index);
        groupIdsWithError.add(id);
        return "groups[" + id + "]";
    }

    public List<EntityTO> getGroupsWithError() {
        if (groupsWithError == null) {
            groupsWithError = groupIdsWithError.isEmpty() ? Collections.<EntityTO> emptyList() : ccgService.getIndexByIds(groupIdsWithError);
        }
        return groupsWithError;
    }

    public String addCreativeError(int index) {
        Long id = creativeIds.get(index);
        creativeIdsWithError.add(id);
        return "creatives[" + id + "]";
    }

    public List<EntityTO> getCreativesWithError() {
        if (creativesWithError == null) {
            creativesWithError = creativeIdsWithError.isEmpty() ? Collections.<EntityTO> emptyList() : creativeService.getIndexByIds(creativeIdsWithError);
        }
        return creativesWithError;
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

    public Map<String, Set<EntityTO>> getCreativesByError() {
        return getEntityErrors("creatives", getCreativesWithError());
    }

    public Map<String, Set<EntityTO>> getGroupsByError() {
        return getEntityErrors("groups", getGroupsWithError());
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

}
