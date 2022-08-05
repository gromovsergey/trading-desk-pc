package com.foros.session.account;

import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;
import com.foros.validation.constraint.NotNullConstraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountInAgencySelector implements Selector<Account> {

    @NotNullConstraint
    private Long agencyId;
    private List<Status> statuses;
    private Paging paging;

    public Long getAgencyId() {
        return agencyId;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    @Override
    public Paging getPaging() {
        return paging;
    }

    public static class Builder {
        private Long agencyId;
        private List<Status> statuses = new ArrayList<>();
        private Paging paging;

        public Builder agencyId(Long agencyId) {
            this.agencyId = agencyId;
            return this;
        }

        public Builder statuses(List<Status> statuses) {
            this.statuses.addAll(statuses);
            return this;
        }

        public Builder statuses(Status... statuses) {
            if (statuses == null) {
                this.statuses = new ArrayList<>();
            } else {
                this.statuses.addAll(Arrays.asList(statuses));
            }
            return this;
        }

        public Builder paging(Paging paging) {
            this.paging = paging;
            return this;
        }

        public AccountInAgencySelector build() {
            return new AccountInAgencySelector(this);
        }
    }

    private AccountInAgencySelector(Builder builder) {
        this.agencyId = builder.agencyId;
        this.statuses = builder.statuses;
        this.paging = builder.paging;
    }
}
