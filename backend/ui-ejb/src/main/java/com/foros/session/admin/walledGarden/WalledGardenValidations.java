package com.foros.session.admin.walledGarden;

import com.foros.model.account.AgencyAccount;
import com.foros.model.account.MarketplaceType;
import com.foros.model.account.PublisherAccount;
import com.foros.model.admin.WalledGarden;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class WalledGardenValidations {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private WalledGardenService walledGardenService;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) WalledGarden walledGarden) {
        AgencyAccount agency = walledGarden.getAgency() != null && walledGarden.getAgency().getId() != null ?
                em.find(AgencyAccount.class, walledGarden.getAgency().getId()) : null;
        PublisherAccount publisher = walledGarden.getPublisher() != null && walledGarden.getPublisher().getId() != null ?
                em.find(PublisherAccount.class, walledGarden.getPublisher().getId()) : null;

        if (agency != null && publisher != null) {
            validateAccounts(context, agency, publisher);
        }

        validateMarketplaceTypes(context, walledGarden);
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) WalledGarden walledGarden) {
        WalledGarden existingWalledGarden = em.find(WalledGarden.class, walledGarden.getId());
        AgencyAccount agency = existingWalledGarden.getAgency();
        PublisherAccount publisher = existingWalledGarden.getPublisher();

        validateAccounts(context, agency, publisher);
        validateMarketplaceTypes(context, walledGarden);
    }

    private void validateAccounts(ValidationContext context, AgencyAccount agency, PublisherAccount publisher) {
        if (!agency.getCountry().equals(publisher.getCountry())) {
            context.addConstraintViolation("WalledGarden.validation.country");
            return;
        }

        if (!walledGardenService.validateAgencyAccountType(agency.getAccountType())) {
            context.addConstraintViolation("WalledGarden.validation.agency.accounttype");
        }
    }

    private void validateMarketplaceTypes(ValidationContext context, WalledGarden walledGarden) {
        MarketplaceType agencyMarketplaceType = walledGarden.getAgencyMarketplaceType();
        MarketplaceType publisherMarketplaceType = walledGarden.getPublisherMarketplaceType();

        if (agencyMarketplaceType == null || agencyMarketplaceType == MarketplaceType.NOT_SET) {
            context
                .addConstraintViolation("WalledGarden.validation.marketplace")
                .withPath("agencyMarketplace");
        }

        if (publisherMarketplaceType == null || publisherMarketplaceType == MarketplaceType.NOT_SET) {
            context
                .addConstraintViolation("WalledGarden.validation.marketplace")
                .withPath("publisherMarketplace");
        }
    }
}
