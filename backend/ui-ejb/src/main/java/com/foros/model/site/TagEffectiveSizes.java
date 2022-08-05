package com.foros.model.site;

import com.foros.jaxb.adapters.TagLinkXmlAdapter;
import com.foros.model.creative.EffectiveSizeTO;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "tagEffectiveSizes")
@XmlType(propOrder = {
        "tag",
        "effectiveSizes"
})
@XmlAccessorType(XmlAccessType.NONE)
public class TagEffectiveSizes {
    private Tag tag;
    private List<EffectiveSizeTO> effectiveSizes;

    public TagEffectiveSizes() {
        this.tag = new Tag();
        this.effectiveSizes = new ArrayList<>();
    }

    public TagEffectiveSizes(Tag tag, List<EffectiveSizeTO> effectiveSizes) {
        this.tag = tag;
        this.effectiveSizes = effectiveSizes;
    }

    @XmlElement
    @XmlJavaTypeAdapter(TagLinkXmlAdapter.class)
    public Tag getTag() {
        return tag;
    }


    @XmlElement(name = "size")
    @XmlElementWrapper(name = "sizes")
    public List<EffectiveSizeTO> getEffectiveSizes() {
        return effectiveSizes;
    }
}
