package com.foros.session.site.creativeApproval;

import com.foros.jaxb.adapters.IdNameTOLinkXmlAdapter;
import com.foros.session.bulk.IdNameTO;
import com.foros.validation.constraint.violation.parsing.ParseErrorsContainer;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement(name="siteCreativeApprovals")
@XmlType(propOrder = {
        "site",
        "operations"
})
public class SiteCreativeApprovalOperations extends ParseErrorsContainer {

    private IdNameTO site;

    private List<SiteCreativeApprovalOperation> operations = new ArrayList<>();

    @XmlJavaTypeAdapter(IdNameTOLinkXmlAdapter.class)
    public IdNameTO getSite() {
        return site;
    }

    public Long getSiteId() {
        return site == null ? null : site.getId();
    }

    public void setSite(IdNameTO site) {
        this.site = site;
    }

    @XmlElement(name = "operation")
    public List<SiteCreativeApprovalOperation> getOperations() {
        return this.operations;
    }

    public void setOperations(List<SiteCreativeApprovalOperation> operations) {
        this.operations = operations;
    }
}