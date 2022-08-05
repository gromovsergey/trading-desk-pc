package com.foros.model.creative;

import com.foros.jaxb.adapters.AbstractXmlAdapter;
import com.foros.model.Identifiable;
import com.foros.model.account.Account;
import com.foros.model.account.TnsAdvertiser;
import com.foros.model.account.TnsBrand;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "yandexCreative")
@XmlType(propOrder = {
        "creativeId",
        "tnsAdvertiser",
        "tnsBrand",
        "tnsArticles"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class YandexCreativeTO {

    private Creative creative;

    private Set<String> tnsArticles = new HashSet<>();

    public YandexCreativeTO() {
    }

    public YandexCreativeTO(Creative creative) {
        this.creative = creative;
    }

    @XmlElement(name = "id")
    @XmlElementWrapper(name = "tnsArticles")
    public Set<String> getTnsArticles() {
        return tnsArticles;
    }

    @XmlElement
    public Long getCreativeId() {
        if (creative != null) {
            return creative.getId();
        }
        return null;
    }

    public void addTnsArticle(String tnsArticle)
    {
        tnsArticles.add(tnsArticle);
    }

    @XmlElement
    @XmlJavaTypeAdapter(TnsXmlAdapter.class)
    public TnsAdvertiser getTnsAdvertiser() {
        if (creative != null && creative.getAccount() != null) {
            return creative.getAccount().getTnsAdvertiser();
        }
        return null;
    }

    @XmlElement
    @XmlJavaTypeAdapter(TnsXmlAdapter.class)
    public TnsBrand getTnsBrand() {
        if (creative != null) {
            if (creative.getTnsBrand() == null || creative.getTnsBrand().getId() == null) {
                return creative.getAccount().getTnsBrand();
            }
            return creative.getTnsBrand();
        }
        return null;
    }

    public Account getAccount() {
        return creative.getAccount();
    }

    public static class TnsXmlAdapter extends AbstractXmlAdapter {
        @Override
        protected Identifiable createInstance(Long id) {
            return null;
        }
    }

    public Creative getCreative() {
        return creative;
    }
}
