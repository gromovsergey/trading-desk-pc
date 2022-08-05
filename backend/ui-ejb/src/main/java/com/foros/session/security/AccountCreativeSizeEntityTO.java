package com.foros.session.security;

import com.foros.model.LocalizableName;
import com.foros.session.EntityTO;

public class AccountCreativeSizeEntityTO extends EntityTO {
    private Long sizeId;
    private LocalizableName sizeName;

    public AccountCreativeSizeEntityTO(Long id, String AccountName, char status, Long sizeId, String defaultName) {
        super(id, AccountName, status);
        this.sizeId = sizeId;
        this.sizeName = new LocalizableName(defaultName, "CreativeSize." + sizeId);
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public LocalizableName getSizeName() {
        return sizeName;
    }

    public void setSizeName(LocalizableName sizeName) {
        this.sizeName = sizeName;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AccountCreativeSizeEntityTO)) {
            return false;
        }

        AccountCreativeSizeEntityTO obj1 = (AccountCreativeSizeEntityTO)obj;
        return (this.getId().equals(obj1.getId()) && this.getSizeId().equals(obj1.getSizeId()));
    }

    @Override
    public int hashCode() {
        return (int)(this.getId().longValue());
    }
}
