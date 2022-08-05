package com.foros.rs.resources.account;

import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountInAgencySelector;
import com.foros.session.account.AccountSelector;
import com.foros.session.account.AccountSelector.Builder;
import com.foros.session.account.AccountService;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.finance.AdvertisingFinanceService;
import com.foros.session.security.AdvertiserInAgency;
import com.foros.session.security.ExtensionAccountTO;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.constraint.violation.parsing.ParseErrorsSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Path("/account/")
public class AccountResource {

    @EJB
    private AccountService accountService;

    @EJB
    private AdvertisingFinanceService advertisingFinanceService;

    @EJB
    private CurrentUserService currentUserService;

    @GET
    public ExtensionAccountTO getById(@QueryParam("id") Long agencyId) {
        Account account = accountService.view(agencyId);
        Long agId = null;
        if (account instanceof AdvertiserAccount) {
            if (((AdvertiserAccount) account).getAgency() != null) {
                agId = ((AdvertiserAccount) account).getAgency().getId();
            }
        }
        ExtensionAccountTO accountTO = new ExtensionAccountTO(account.getId(), account.getName(), account.getStatus().getLetter(), account.getRole(),
            account.getCountry().getCountryCode(), account.getDisplayStatusId(), account.getFlags(), account.getCurrency().getCurrencyCode(),
            agId);
        return accountTO;
    }

    @GET
    @Path("/searchInAgency/")
    public Result<ExtensionAccountTO> getByAgencyId(@QueryParam("agency.id") Long agencyId,
            @QueryParam("statuses") List<Status> statuses,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
            ) {

        AccountInAgencySelector.Builder builder = new AccountInAgencySelector.Builder()
            .agencyId(agencyId).statuses(statuses)
            .paging(getPaging(pagingFirst, pagingCount));

        return (Result<ExtensionAccountTO>) accountService.getExtensionAccountTOByAgency(builder.build());

    }

    private Paging getPaging(Integer pagingFirst, Integer pagingCount) {
        Paging paging = new Paging(pagingFirst, pagingCount);
        if (paging.getCount() > Paging.MAX_PAGE_SIZE) {
            paging.setCount(Paging.MAX_PAGE_SIZE);
        }
        return paging;
    }

    @GET
    @Path("/search/")
    public Result<ExtensionAccountTO> get(
            @QueryParam("ids") List<Long> accountIds,
            @QueryParam("roles") List<AccountRole> roles,
            @QueryParam("statuses") List<Status> statuses,
            @QueryParam("countryCodes") List<String> countryCodes,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
            ) {
        Builder builder = new AccountSelector.Builder();

        builder.accountIds(accountIds)
            .roles(roles)
            .statuses(statuses)
            .countryCodes(countryCodes)
            .paging(getPaging(pagingFirst, pagingCount));

        Result<ExtensionAccountTO> accounts = accountService.get(builder.build());
        return accounts;
    }

    @GET
    @Path("/searchAdvertiserByName/")
    public Result<ExtensionAccountTO> get(@QueryParam("name") String name) {
        Result<ExtensionAccountTO> accounts = accountService.getAdvertiserAccounts(name);
        return accounts;
    }

    @POST
    @Path("/advertiserInAgency/")
    public OperationsResult perform(Operations<AdvertiserInAgency> operations) {
        ParseErrorsSupport.throwIfAnyErrorsPresent(operations);

        List<Long> result = new ArrayList<>();
        for (Operation<AdvertiserInAgency> operation : operations.getOperations()) {
            AdvertiserInAgency advertiserInAgency = operation.getEntity();

            AdvertiserAccount advertiserAccount = new AdvertiserAccount();
            advertiserAccount.setId(advertiserInAgency.getId());
            advertiserAccount.setName(advertiserInAgency.getName());
            advertiserAccount.setLegalName(advertiserInAgency.getName());
            advertiserAccount.setStatus(advertiserInAgency.getStatus());

            AgencyAccount agencyAccount;
            try {
                agencyAccount = accountService.findAgencyAccount(advertiserInAgency.getAgency().getId());
                advertiserAccount.setAgency(agencyAccount);
                advertiserAccount.setCurrency(agencyAccount.getCurrency());
            } catch (Exception e) {
                throw ConstraintViolationException.newBuilder("errors.entity.notFound")
                        .withPath("agency.id")
                        .withError(BusinessErrors.ENTITY_NOT_FOUND)
                        .withValue(advertiserInAgency.getAgency() != null ? advertiserInAgency.getAgency().getId() : null)
                        .build();
            }

            switch (operation.getOperationType()) {
                case CREATE:
                    advertiserAccount.getFinancialSettings().setAccount(advertiserAccount);
                    advertiserAccount.getFinancialSettings().setCommission(advertiserInAgency.getCommission());
                    advertiserAccount.getFinancialSettings().unregisterChange("account");
                    // ToDo: get rid of pre-defined hard-coded values:
                    advertiserAccount.getFinancialSettings().getData().setPrepaidAmount(BigDecimal.valueOf(500000));

                    Long accountId = accountService.addAdvertiser(advertiserAccount);
                    result.add(accountId);
                    break;

                case UPDATE:
                    advertiserAccount.setFinancialSettings(
                            advertisingFinanceService.getFinancialSettings(advertiserAccount.getId()));
                    advertiserAccount.getFinancialSettings().setCommission(advertiserInAgency.getCommission());
                    accountService.updateAdvertiser(advertiserAccount);
                    result.add(advertiserAccount.getId());
                    break;
            }
        }
        return new OperationsResult(result);
    }

}
