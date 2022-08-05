package com.foros.session.account;

import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.security.AccountRole;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;
import com.foros.validation.constraint.IdCollectionConstraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AccountSelector implements Selector<Account> {

    @IdCollectionConstraint
    private List<Long> accountIds;
    private List<AccountRole> roles;
    private List<Status> statuses;
    private List<String> countryCodes;
    private Long internalAccountId;
    private Paging paging;

    public List<Long> getAccountIds() {
        return accountIds;
    }

    public List<AccountRole> getRoles() {
        return roles;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public List<Status> getExcludedStatuses() {
        if (statuses.isEmpty()) {
            return Collections.emptyList();
        }
        List<Status> excludedStatuses = new ArrayList<>(Arrays.asList(Status.values()));
        excludedStatuses.removeAll(statuses);
        return excludedStatuses;
    }

    public List<String> getCountryCodes() {
        return countryCodes;
    }

    @Override
    public Paging getPaging() {
        return paging;
    }

    public Long getInternalAccountId() {
        return internalAccountId;
    }

    public static class Builder {
        private List<Long> accountIds = new ArrayList<>();
        private List<AccountRole> roles = new ArrayList<>();
        private List<Status> statuses = new ArrayList<>();
        private List<String> countryCodes = new ArrayList<>();
        private Long internalAccountId;
        private Paging paging;

        public Builder accountIds(List<Long> accountIds) {
            this.accountIds.clear();
            this.accountIds.addAll(accountIds);
            return this;
        }

        public Builder accountIds(Long... accountIds) {
            accountIds(accountIds != null ? Arrays.asList(accountIds) : Collections.<Long>emptyList());
            return this;
        }

        public Builder roles(List<AccountRole> roles) {
            this.roles.addAll(roles);
            return this;
        }

        public Builder statuses(List<Status> statuses) {
            this.statuses.clear();
            this.statuses.addAll(statuses);
            return this;
        }

        public Builder statuses(Status... statuses) {
            statuses(statuses != null ? Arrays.asList(statuses) : Collections.<Status>emptyList());
            return this;
        }

        public Builder excludedStatuses(Status... excludedStatuses) {
            List<Status> statuses = new ArrayList<>(Arrays.asList(Status.values()));
            if (excludedStatuses != null) {
                statuses.removeAll(Arrays.asList(excludedStatuses));
            }
            statuses(statuses);
            return this;
        }

        public Builder countryCodes(List<String> countryCodes) {
            this.countryCodes.clear();
            this.countryCodes.addAll(countryCodes);
            return this;
        }

        public Builder countryCodes(String... countryCodes) {
            countryCodes(countryCodes != null ? Arrays.asList(countryCodes) : Collections.<String>emptyList());
            return this;
        }

        public Builder internalAccountId(Long internalAccountId) {
            this.internalAccountId = internalAccountId;
            return this;
        }

        public Builder paging(Paging paging) {
            this.paging = paging;
            return this;
        }

        public AccountSelector build() {
            return new AccountSelector(this);
        }

        public Builder roles(AccountRole... roles) {
            if (roles == null) {
                this.roles = new ArrayList<>();
            } else {
                this.roles.addAll(Arrays.asList(roles));
            }
            return this;
        }
    }

    private AccountSelector(Builder builder) {
        this.accountIds = builder.accountIds;
        this.roles = builder.roles;
        this.statuses = builder.statuses;
        this.countryCodes = builder.countryCodes;
        this.internalAccountId = builder.internalAccountId;
        this.paging = builder.paging;
    }
}
