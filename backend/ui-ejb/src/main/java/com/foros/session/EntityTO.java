package com.foros.session;

import com.foros.model.LocalizableName;
import com.foros.model.Status;
import com.foros.util.LocalizableNameUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class EntityTO extends NamedTO {

    private Status status;

    private String resKey;

    private String localizedName;

    public EntityTO() {
        super();
    }

    public EntityTO(Long id, String name, char status) {
        this(id, name, Status.valueOf(status));
    }

    public EntityTO(Long id, String name, Status status) {
        super(id, name);
        this.status = status;
        this.resKey = getProvidedResKey();
    }

    public EntityTO(Long id, String name, char status, String resKey) {
        super(id, name);
        this.status = Status.valueOf(status);
        this.resKey = resKey;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalizableName getLocalizableName() {
        return new LocalizableName(getName(), resKey);
    }

    protected String getProvidedResKey() {
        return null;
    }

    public String getLocalizedName() {
        if (localizedName == null) {
            localizedName = LocalizableNameUtil.getLocalizedValue(this);
        }
        return localizedName;
    }
}
