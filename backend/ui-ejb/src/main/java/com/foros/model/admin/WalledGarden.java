package com.foros.model.admin;

import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.MarketplaceType;
import com.foros.model.account.PublisherAccount;
import com.foros.security.AccountRole;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "WALLEDGARDEN")
@NamedQueries({
    @NamedQuery(name = "WalledGarden.findAll", query = "from WalledGarden"),
    @NamedQuery(name = "WalledGarden.findByAdvertiser",
        query = "select wg from WalledGarden wg, AdvertiserAccount a" +
                " where a.id = :advertiserAccountId and a.agency.id = wg.agency.id"),
    @NamedQuery(name = "WalledGarden.findByPublisher",
        query = "select wg from WalledGarden wg" +
                " where wg.publisher.id = :publisherAccountId"),
    @NamedQuery(name = "WalledGarden.findAllWithDependencies",
        query = "select wg from WalledGarden wg left join fetch wg.publisher wgp" +
                " left join fetch wg.agency wga left join fetch wgp.country wgpCountry"),
    @NamedQuery(name = "WalledGarden.findWithDependancesByCountryCode", 
        query = "select wg from WalledGarden wg left join fetch wg.publisher wgp" +
                " left join fetch wg.agency wga left join fetch wgp.country wgpCountry" +
                " where wgpCountry.countryCode=:countryCode"),
    @NamedQuery(name = "WalledGarden.getPublishersCount",
        query = "select count(wg) from WalledGarden wg" +
                " where wg.publisher.id = :publisherAccountId"),
    @NamedQuery(name = "WalledGarden.getAgencyCount",
        query = "select count(wg) from WalledGarden wg" +
                " where wg.agency.id = :agencyAccountId"),
    @NamedQuery(name = "WalledGarden.getAdvertisersCount",
        query = "select count(wg) from WalledGarden wg, AdvertiserAccount a" +
                " where a.id = :advertiserId and a.agency.id = wg.agency.id"),
    @NamedQuery(name = "WalledGarden.getAgencyAccountTypeCount",
        query = "select count(wg) from WalledGarden wg" +
                " where wg.agency.accountType.id = :agencyAccountTypeId")
})
public class WalledGarden extends VersionEntityBase implements Identifiable, Serializable {
    private enum AccountReference {AGENCY, PUBLISHER}

    public static final String FIND_FREE_AGENCY_ACCOUNTS = 
        "select a.account_id, a.name, a.status, a.role_id, a.country_code, a.display_status_id, a.flags " +
        "from Account a " +
        "where " +
        "a.status <> 'D' and " +
        "a.role_id=" + AccountRole.AGENCY.getId() + " and " +
        "a.country_code=:countryCode and " +
        "a.account_id not in (select wg.agency_account_id from walledgarden wg) and " +
        "0=(select count(ccgtype.accounttype_ccgtype_id) " +
        "   from accounttype_ccgtype ccgtype " +
        "   where " +
        "       ccgtype.account_type_id=a.account_type_id and (" +
        "       ccgtype.rate_type='CPA' or " +
        "       ccgtype.ccg_type='T'))";
    
    public static final String FIND_FREE_PUBLISHER_ACCOUNTS = 
        "select a.account_id, a.name, a.status, a.role_id, a.country_code, a.display_status_id, a.flags " +
        "from Account a " +
        "where " +
        "a.status <> 'D' and " +
        "a.role_id=" + AccountRole.PUBLISHER.getId() + " and " +
        "a.country_code=:countryCode and " +
        "a.account_id not in (select wg.pub_account_id from walledgarden wg)";
    
    @SequenceGenerator(name = "WalledGardenGen", sequenceName = "WALLEDGARDEN_WG_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WalledGardenGen")
    @Column(name = "WG_ID", nullable = false)
    private Long id;

    @RequiredConstraint
    @HasIdConstraint
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "PUB_ACCOUNT_ID")
    private PublisherAccount publisher;

    @RequiredConstraint
    @HasIdConstraint
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "AGENCY_ACCOUNT_ID")
    private AgencyAccount agency;
    
    @Column(name = "PUB_MARKETPLACE")
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.account.MarketplaceType"),
            @Parameter(name = "nullValue", value = "NOT_SET")
    })
    private MarketplaceType publisherMarketplaceType = MarketplaceType.NOT_SET;
    
    @Column(name = "AGENCY_MARKETPLACE")
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.account.MarketplaceType"),
            @Parameter(name = "nullValue", value = "NOT_SET")
    })
    private MarketplaceType agencyMarketplaceType = MarketplaceType.NOT_SET;
    
    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }
    
    public PublisherAccount getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherAccount publisher) {
        this.publisher = publisher;
        this.registerChange("publisher");
    }

    public AgencyAccount getAgency() {
        return agency;
    }

    public void setAgency(AgencyAccount agency) {
        this.agency = agency;
        this.registerChange("agency");
    }

    public MarketplaceType getPublisherMarketplaceType() {
        return publisherMarketplaceType;
    }

    public void setPublisherMarketplaceType(MarketplaceType marketplaceType) {
        this.publisherMarketplaceType = marketplaceType;
        this.registerChange("publisherMarketplaceType");
    }
    
    public MarketplaceType getAgencyMarketplaceType() {
        return agencyMarketplaceType;
    }

    public void setAgencyMarketplaceType(MarketplaceType marketplaceType) {
        this.agencyMarketplaceType = marketplaceType;
        this.registerChange("agencyMarketplaceType");
    }

    public MarketplaceTypeTO getAgencyMarketplace() {
        return new MarketplaceTypeTO(AccountReference.AGENCY);
    }

    public MarketplaceTypeTO getPublisherMarketplace() {
        return new MarketplaceTypeTO(AccountReference.PUBLISHER);
    }

    private class MarketplaceTypeTO {
        private AccountReference accountReference;

        private MarketplaceTypeTO(AccountReference accountReference) {
            this.accountReference = accountReference;
        }

        public boolean isExWG() {
            switch (accountReference) {
                case AGENCY:
                    return agencyMarketplaceType != null && agencyMarketplaceType.isInFOROS();
                case PUBLISHER:
                    return publisherMarketplaceType != null && publisherMarketplaceType.isInFOROS();
                default:
                    throw new IllegalArgumentException();
            }
        }

        public void setExWG(boolean exWG) {
            switch (accountReference) {
                case AGENCY:
                    agencyMarketplaceType = adjustInFOROSMarketplaceType(agencyMarketplaceType, exWG);
                    registerChange("agencyMarketplaceType");
                    break;
                case PUBLISHER:
                    publisherMarketplaceType = adjustInFOROSMarketplaceType(publisherMarketplaceType, exWG);
                    registerChange("publisherMarketplaceType");
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private MarketplaceType adjustInFOROSMarketplaceType(MarketplaceType type, boolean inFOROS) {
            if (type == null) {
                return inFOROS ? MarketplaceType.FOROS : MarketplaceType.NOT_SET;
            } else {
                boolean inWG = type.isInWG();
                return MarketplaceType.byFlags(inFOROS, inWG);
            }
        }

        public boolean isInWG() {
            switch (accountReference) {
                case AGENCY:
                    return agencyMarketplaceType != null && agencyMarketplaceType.isInWG();
                case PUBLISHER:
                    return publisherMarketplaceType != null && publisherMarketplaceType.isInWG();
                default:
                    throw new IllegalArgumentException();
            }
        }

        public void setInWG(boolean inWG) {
            switch (accountReference) {
                case AGENCY:
                    agencyMarketplaceType = adjustInWGMarketplaceType(agencyMarketplaceType, inWG);
                    registerChange("agencyMarketplaceType");
                    break;
                case PUBLISHER:
                    publisherMarketplaceType = adjustInWGMarketplaceType(publisherMarketplaceType, inWG);
                    registerChange("publisherMarketplaceType");
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private MarketplaceType adjustInWGMarketplaceType(MarketplaceType type, boolean inWG) {
            if (type == null) {
                return inWG ? MarketplaceType.WG : MarketplaceType.NOT_SET;
            } else {
                boolean inFOROS = type.isInFOROS();
                return MarketplaceType.byFlags(inFOROS, inWG);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof WalledGarden)) {
            return false;
        }

        WalledGarden other = (WalledGarden) obj;

        return new EqualsBuilder()
            .append(getId(), other.getId())
            .append(getPublisher(), other.getPublisher())
            .append(getAgency(), other.getPublisher())
            .append(getPublisherMarketplaceType(), other.getPublisherMarketplaceType())
            .append(getAgencyMarketplaceType(), other.getAgencyMarketplaceType()).isEquals();

    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (this.getId() == null) {
            return super.hashCode();
        }
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }
}
