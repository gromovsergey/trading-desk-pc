package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.session.campaign.CtrService;

import java.sql.Timestamp;

import javax.ejb.EJB;

public class ResetCtrAction extends BaseActionSupport {

    @EJB
    private CtrService ctrService;

    private Long id;

    private Timestamp version;

    public String resetCtr() {
        ctrService.resetCtr(id, version);
        return SUCCESS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }


}
