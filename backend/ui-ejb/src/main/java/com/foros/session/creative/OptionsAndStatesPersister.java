package com.foros.session.creative;

import com.foros.config.ConfigService;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptGroupStatePK;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeOptionValuePK;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroupState;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionValueUtils;
import com.foros.session.ServiceLocator;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.template.OptionGroupStateHelper;
import com.foros.session.textad.TextAdImageUtil;
import com.foros.util.JpaCollectionMerger;
import com.foros.util.StringUtil;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;

public abstract class OptionsAndStatesPersister {

    public void prePersist(Creative creative) {
        if (creative.getId() != null) {
            for (CreativeOptGroupState groupState : creative.getGroupStates()) {
                CreativeOptGroupStatePK id = groupState.getId();
                if (id == null) {
                    id = new CreativeOptGroupStatePK();
                }
                id.setCreativeId(creative.getId());
            }
            for (CreativeOptionValue optionValue : creative.getOptions()) {
                CreativeOptionValuePK id = optionValue.getId();
                if (id == null) {
                    id = new CreativeOptionValuePK();
                }
                id.setCreativeId(creative.getId());
            }
        }

        Map<Long, OptionGroupState> statesByOptionId;
        if (creative.isTextCreative()) {
            Set<CreativeSize> usedSizes = ServiceLocator.getInstance().lookup(CampaignCreativeService.class).getEffectiveTagSizes(creative, creative.getAccount());
            statesByOptionId = OptionGroupStateHelper.getGroupsStatesByOptionId(
                creative.getGroupStates(), Collections.singletonList(creative.getTemplate()), usedSizes, OptionGroupType.Advertiser);
        } else {
            statesByOptionId = OptionGroupStateHelper.getGroupsStatesByOptionId(
                creative.getGroupStates(), creative.getTemplate(), creative.getSize(), OptionGroupType.Advertiser);
        }

        // creative options
        if (creative.getOptions() != null) {
            Set<CreativeOptionValue> prepersistedOptions = new LinkedHashSet<>();
            for (CreativeOptionValue optionValue : creative.getOptions()) {
                long optionId = optionValue.getOptionId();
                Option option = getEm().find(Option.class, optionId);
                optionValue.setOption(option);
                OptionGroupState state = statesByOptionId.get(optionValue.getOptionId());
                // Option belongs to disabled group, do not persist its value.
                if (!OptionGroupStateHelper.isGroupEnabled(option.getOptionGroup(), state)) {
                    continue;
                }

                if (creative.getId() != null) {
                    optionValue.setCreative(getEm().getReference(Creative.class, creative.getId()));
                    optionValue.setId(new CreativeOptionValuePK(creative.getId(), optionValue.getOption().getId()));
                } else {
                    optionValue.setCreative(creative);
                }
                prepareOptionValue(optionValue, creative.getAccount());

                // If user doesn't change the value from default, then we do not persist its value.
                if (OptionValueUtils.isDefaultValue(optionValue)) {
                    continue;
                }

                prepersistedOptions.add(optionValue);
            }
            creative.setOptions(prepersistedOptions);
        }
    }

    public void persist(Creative creative, Set<CreativeOptionValue> optionValues, Set<CreativeOptGroupState> states) {
        for (CreativeOptionValue opt : optionValues) {
            opt.setId(new CreativeOptionValuePK(creative.getId(), opt.getOption().getId()));
            getEm().persist(opt);
        }
        creative.setOptions(optionValues);

        for (CreativeOptGroupState state : states) {
            state.getId().setCreativeId(creative.getId());
            getEm().persist(state);
        }
        creative.setGroupStates(states);
    }

    private void prepareOptionValue(CreativeOptionValue optionValue, AdvertiserAccount account) {
        if (optionValue.getCreative().isTextCreative() && TextCreativeOption.IMAGE_FILE.getToken().equals(optionValue.getOption().getToken())) {
            if (StringUtil.isPropertyNotEmpty(optionValue.getValue())) {
                String fileName = TextAdImageUtil.getResizedFilePath(getConfig(), account, optionValue.getValue());
                optionValue.setValue(fileName);
                getCampaignCreativeService().updateImagePreview(account, fileName);
            }
        } else {
            OptionValueUtils.prepareOptionValue(optionValue, account);
        }
    }

    protected abstract ConfigService getConfig();

    protected abstract CampaignCreativeService getCampaignCreativeService();

    protected abstract EntityManager getEm();

    /**
     * Options which values MUST NOT affect on creative.version (https://jira.ocslab.com/browse/OUI-28925)
     * @return Set of mentioned creative options' tokens
     */
    protected abstract Set<String> getSilentlyUpdatedOptionTokens();

    public void merge(final Creative existingCreative, final Creative updatedCreative) {
        updatedCreative.unregisterChange("options");
        final Boolean[] onlySilentOptionsChanged = new Boolean[]{ Boolean.TRUE };
        (new JpaCollectionMerger<CreativeOptionValue>(existingCreative.getOptions(), updatedCreative.getOptions()) {
            private void inspectOptionValueChange(CreativeOptionValue value) {
                if (!getSilentlyUpdatedOptionTokens().contains(value.getOption().getToken())) {
                    onlySilentOptionsChanged[0] = Boolean.FALSE;
                }
            }

            @Override
            protected boolean add(CreativeOptionValue updated) {
                if (updated.getOption().isInternalUse() && isExternal()) {
                    return false;
                }

                boolean result = super.add(updated);
                if (result) {
                    inspectOptionValueChange(updated);
                }
                return result;
            }

            @Override
            protected Object getId(CreativeOptionValue cov, int index) {
                return cov.getOption().getId();
            }

            @Override
            protected void update(CreativeOptionValue persistent, CreativeOptionValue updated) {
                if (persistent.getOption().isInternalUse() && isExternal()) {
                    return;
                }

                updated.setId(persistent.getId());
                if (updated.getVersion() == null) {
                    updated.setVersion(persistent.getVersion());
                }
                super.update(persistent, updated);
                if (persistent.isChanged()) {
                    inspectOptionValueChange(persistent);
                    existingCreative.registerChange("options");
                }
            }

            @Override
            protected boolean delete(CreativeOptionValue persistent) {
                if (persistent.getOption().isInternalUse() && isExternal()) {
                    return false;
                }

                Option option = persistent.getOption();
                boolean needDelete = !updatedCreative.getSize().getHiddenOptions().contains(option)
                        && !updatedCreative.getTemplate().getHiddenOptions().contains(option)
                        && super.delete(persistent);
                if (needDelete) {
                    inspectOptionValueChange(persistent);
                } else {
                    updatedCreative.getOptions().add(persistent);
                }
                return needDelete;
            }

            @Override
            protected EntityManager getEM() {
                return getEm();
            }

        }).merge();
        if (onlySilentOptionsChanged[0]) {
            existingCreative.unregisterChange("options");
        }

        updatedCreative.unregisterChange("groupStates");
        (new JpaCollectionMerger<CreativeOptGroupState>(existingCreative.getGroupStates(), updatedCreative.getGroupStates()) {
            @Override
            protected Object getId(CreativeOptGroupState cs, int index) {
                return cs.getId().getOptionGroupId();
            }

            @Override
            protected void update(CreativeOptGroupState persistent, CreativeOptGroupState updated) {
                updated.setId(persistent.getId());
                if (updated.getVersion() == null) {
                    updated.setVersion(persistent.getVersion());
                }
                super.update(persistent, updated);
                if (persistent.isChanged()) {
                    existingCreative.registerChange("groupStates");
                }
            }

            @Override
            protected EntityManager getEM() {
                return getEm();
            }

        }).merge();

    }

    protected abstract boolean isExternal();
}
