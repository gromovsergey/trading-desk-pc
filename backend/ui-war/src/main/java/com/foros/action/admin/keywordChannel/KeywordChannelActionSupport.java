package com.foros.action.admin.keywordChannel;

import com.foros.action.BaseActionSupport;
import com.foros.session.channel.service.KeywordChannelService;

import com.opensymphony.xwork2.ModelDriven;
import javax.ejb.EJB;

public abstract class KeywordChannelActionSupport<T> extends BaseActionSupport implements ModelDriven<T> {

    @EJB
    protected KeywordChannelService keywordChannelService;

}
