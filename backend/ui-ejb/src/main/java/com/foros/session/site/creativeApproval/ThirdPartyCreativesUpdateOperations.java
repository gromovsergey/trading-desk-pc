package com.foros.session.site.creativeApproval;

import com.foros.jaxb.adapters.IdLinkXmlAdapter;
import com.foros.model.site.ThirdPartyCreative;
import com.foros.validation.constraint.violation.parsing.ParseErrorsContainer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement(name="thirdPartyCreativesUpdate")
@XmlType(propOrder = {
        "siteId",
        "thirdPartyCreatives"
})
public class ThirdPartyCreativesUpdateOperations extends ParseErrorsContainer {

    private Long siteId;

    private List<ThirdPartyCreative> thirdPartyCreatives = new ArrayList<>();

    @XmlElement(name = "site")
    @XmlJavaTypeAdapter(IdLinkXmlAdapter.class)
    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    @XmlElement(name = "thirdPartyCreative")
//    @XmlElementWrapper(name = "thirdPartyCreatives")
    public List<ThirdPartyCreative> getThirdPartyCreatives() {
        return thirdPartyCreatives;
    }

    public void setThirdPartyCreatives(List<ThirdPartyCreative> thirdPartyCreatives) {
        this.thirdPartyCreatives = thirdPartyCreatives;
    }
}
