package com.foros.session.channel.service;

import com.foros.model.channel.ChannelVisibility;
import com.foros.model.security.User;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.session.security.UserService;

import java.util.Collection;

public class ChannelsFilter {
    private static final String DO_NOT_FILTER = "1=1";
    private static final String EMPTY_FILTER = "1=2";

    private ChannelVisibilityCriteria criteria;
    private String channelAlias;
    private String accountAlias;
    private boolean useJpa = false;
    private QueryHelper helper;

    private UserService userService;

    public ChannelsFilter(String channelAlias, String accountAlias, UserService userService) {
        this.channelAlias = channelAlias;
        this.accountAlias = accountAlias;
        this.userService = userService;
    }

    public ChannelsFilter(String channelAlias, UserService userService) {
        this.channelAlias = channelAlias;
        this.userService = userService;
    }

    public boolean isUseJpa() {
        return useJpa;
    }

    public void setUseJpa(boolean useJpa) {
        this.useJpa = useJpa;
    }

    public ChannelVisibilityCriteria getCriteria() {
        return criteria;
    }


    public String buildFilterClause(ChannelVisibilityCriteria criteria) {
        this.criteria = criteria;

        return "(" + filterVisibility() + " and " + filterAccess() + ")";
    }

    private String filterVisibility() {
        if (criteria == null) {
            criteria = ChannelVisibilityCriteria.ALL;
        }

        if (criteria == ChannelVisibilityCriteria.ALL) {
            return DO_NOT_FILTER;
        }

        if (criteria == ChannelVisibilityCriteria.NONE) {
            return EMPTY_FILTER;
        }

        return getHelper().createVisibilityClause(criteria);
    }

    private String filterAccess() {
        if (!criteria.getVisibilities().contains(ChannelVisibility.PRI)) {
            return DO_NOT_FILTER;
        }

        Long privateAccountManagerId = null;

        User currentUser = userService.getMyUser();
        if (currentUser.getRole().isAdvertiserAccountManager() || currentUser.getRole().isCMPAccountManager()) {
            privateAccountManagerId = currentUser.getId();
        }

        if (privateAccountManagerId == null) {
            // can see all private channels
            return DO_NOT_FILTER;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("(");
        sql.append(getHelper().createVisibilityClause(ChannelVisibilityCriteria.NON_PRIVATE));
        sql.append(" or ");
        sql.append(getHelper().createAccountManagerClause(privateAccountManagerId));
        sql.append(")");
        return sql.toString();
    }

    private QueryHelper getHelper() {
        if (helper == null) {
            if (useJpa) {
                helper = new JpaQueryHelper();
            } else {
                helper = new NativeQueryHelper();
            }
        }
        return helper;
    }

    private interface QueryHelper {
        String createAccountManagerClause(Long privateAccountManagerId);
        String createVisibilityClause(ChannelVisibilityCriteria visibilityCriteria);
    }

    private class NativeQueryHelper implements QueryHelper {
        @Override
        public String createAccountManagerClause(Long privateAccountManagerId) {
            return accountAlias + ".ACCOUNT_MANAGER_ID=" + privateAccountManagerId;
        }

        @Override
        public String createVisibilityClause(ChannelVisibilityCriteria visibilityCriteria) {
            return formatINClause(channelAlias + ".VISIBILITY", visibilityCriteria.getVisibilities());
        }
    }

    private class JpaQueryHelper implements QueryHelper {
        @Override
        public String createAccountManagerClause(Long privateAccountManagerId) {
            return accountAlias + ".accountManager.id=" + privateAccountManagerId;
        }

        @Override
        public String createVisibilityClause(ChannelVisibilityCriteria visibilityCriteria) {
            return formatINClause(channelAlias + ".visibility", visibilityCriteria.getVisibilities());
        }
    }

    private static String formatINClause(String fieldName, Collection<ChannelVisibility> visibilities) {
        StringBuilder res = new StringBuilder();
        if (!visibilities.isEmpty()) {
            res.append('(').append(fieldName).append(" in (");

            boolean firstElement = true;
            for (ChannelVisibility visibility : visibilities) {
                if (!firstElement) {
                    res.append(',');
                }
                res.append('\'');
                res.append(String.valueOf(visibility));
                res.append('\'');
                firstElement = false;
            }

            res.append("))");
        }

        return res.toString();
    }
}
