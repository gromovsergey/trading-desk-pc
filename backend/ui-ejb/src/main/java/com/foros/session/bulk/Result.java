package com.foros.session.bulk;

import com.foros.model.EntityBase;
import com.foros.model.channel.ApiDeviceChannelTO;
import com.foros.model.channel.ApiGeoChannelTO;
import com.foros.model.channel.Platform;
import com.foros.model.creative.YandexCreativeTO;
import com.foros.model.isp.Colocation;
import com.foros.model.site.ThirdPartyCreative;
import com.foros.session.channel.triggerQA.TriggerQATO;
import com.foros.session.query.PartialList;
import com.foros.session.security.ExtensionAccountTO;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalTO;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"entities", "paging"})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Result<T> {
    private List<T> entities;
    private Paging paging;

    public Result() {
    }

    public Result(PartialList<T> partialList) {
        setEntities(partialList);
        setPaging(partialList.getPaging());
    }

    public Result(List<T> entities, Paging paging) {
        setEntities(entities);
        setPaging(paging);
    }

    @XmlElementRefs({
            @XmlElementRef(type = EntityBase.class)
            , @XmlElementRef(type = SiteCreativeApprovalTO.class)
            , @XmlElementRef(type = TriggerQATO.class)
            , @XmlElementRef(type = ThirdPartyCreative.class)
            , @XmlElementRef(type = YandexCreativeTO.class)
            , @XmlElementRef(type = ExtensionAccountTO.class)
            , @XmlElementRef(type = Colocation.class)
            , @XmlElementRef(type = ApiGeoChannelTO.class)
            , @XmlElementRef(type = ApiDeviceChannelTO.class)
            , @XmlElementRef(type = Platform.class)
    })
    public List<T> getEntities() {
        return entities;
    }

    public void setEntities(List<T> entities) {
        this.entities = entities;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
