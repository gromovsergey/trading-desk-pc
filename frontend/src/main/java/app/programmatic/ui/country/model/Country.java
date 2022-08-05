package app.programmatic.ui.country.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "country")
public class Country {

    @Id
    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @Column(name = "conversion_tag_domain")
    private String conversionDomain;

    @Column(name = "sortorder")
    private Integer sortOrder;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getConversionDomain() {
        return conversionDomain;
    }

    public void setConversionDomain(String conversionDomain) {
        this.conversionDomain = conversionDomain;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
