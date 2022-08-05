package com.foros.session.template;

import com.foros.model.creative.CreativeSize;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupState;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.Template;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OptionGroupStateHelper {

    /**
     * Returns a map with option.id mappad to corresponding group state
     */
    public static Map<Long, OptionGroupState> getGroupsStatesByOptionId(Set<? extends OptionGroupState> groupStates, Collection<Option> options) {

        Map<Long, OptionGroupState> groupToState = new HashMap<Long, OptionGroupState>();
        for (OptionGroupState groupState : groupStates) {
            groupToState.put(groupState.getGroupId(), groupState);
        }

        Map<Long, OptionGroupState> optionToState = new HashMap<Long, OptionGroupState>();
        for (Option option : options) {
            OptionGroupState state = groupToState.get(option.getOptionGroup().getId());
            if (state == null) {
                state = createStubGroupState(option);
            }
            optionToState.put(option.getId(), state);
        }

        return optionToState;
    }


    /**
     * Retrieves all options from template and size and places them into map against each group state
     * Returns a map with optionValue.id mapped to corresponding group state.
     */
    public static Map<Long, OptionGroupState> getGroupsStatesByOptionId(Set<? extends OptionGroupState> groupStates, Template template, CreativeSize size, OptionGroupType optionGroupType) {
        Set<Option> options = new HashSet<Option>();
        if (template != null) {
            switch (optionGroupType) {
            case Advertiser:
                options.addAll(template.getAdvertiserOptions());
                break;
            case Publisher:
                options.addAll(template.getPublisherOptions());
                break;
            }
        }

       if (size != null)  {
            switch (optionGroupType) {
            case Advertiser:
                options.addAll(size.getAdvertiserOptions());
                break;
            case Publisher:
                options.addAll(size.getPublisherOptions());
                break;
            }
       }

        return getGroupsStatesByOptionId(groupStates, options);
    }

    public static Map<Long, OptionGroupState> getGroupsStatesByOptionId(Set<? extends OptionGroupState> groupStates, Collection<CreativeTemplate> templates, Collection<CreativeSize> sizes, OptionGroupType optionGroupType) {
        Map<Long, OptionGroupState> groupToState = new HashMap<Long, OptionGroupState>();
        for (Template template : templates) {
            groupToState.putAll(getGroupsStatesByOptionId(groupStates, template, null, optionGroupType));
        }

        for (CreativeSize size : sizes) {
            groupToState.putAll(getGroupsStatesByOptionId(groupStates, (CreativeTemplate) null, size, optionGroupType));
        }

        return groupToState;

    }

    public static boolean isGroupEnabled(OptionGroup optionGroup, OptionGroupState groupState) {
        return groupState != null ? groupState.getEnabled() :
                OptionGroup.Availability.DISABLED_BY_DEFAULT != optionGroup.getAvailability();
    }


    private static OptionGroupState createStubGroupState(final Option option) {
         return  new OptionGroupState() {
             @Override
             public Boolean getEnabled() {
                 return option.getOptionGroup().getAvailability() == OptionGroup.Availability.ALWAYS_ENABLED ||
                         option.getOptionGroup().getAvailability() == OptionGroup.Availability.ENABLED_BY_DEFAULT;
             }

             @Override
             public Boolean getCollapsed() {
                 return option.getOptionGroup().getCollapsability() != OptionGroup.Collapsability.NOT_COLLAPSIBLE &&
                         option.getOptionGroup().getCollapsability() != OptionGroup.Collapsability.EXPANDED_BY_DEFAULT;
             }

             @Override
             public Long getGroupId() {
                 return null;
             }
        };
    }
}
