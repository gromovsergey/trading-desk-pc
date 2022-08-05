package com.foros.action.channel;

import com.foros.model.account.Account;
import com.foros.model.channel.Channel;
import com.foros.model.time.TimeSpan;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.EntityTO;
import com.foros.session.admin.categoryChannel.CategoryChannelService;
import com.foros.session.channel.service.ChannelTriggersStatsService;
import com.foros.session.channel.service.ChannelTriggersTotalsTO;
import com.foros.util.DateHelper;
import com.foros.util.RegularChecksUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.ejb.EJB;

public abstract class ViewChannelActionSupport<T extends Channel> extends ViewEditChannelActionSupport<T> {

    @EJB
    private ChannelTriggersStatsService channelTriggersStatsService;

    private Long id;
    private List<List<EntityTO>> categories;

    private ChannelTriggersTotalsTO triggersTotals;

    private String formattedYesterdayDate;

    @EJB
    protected CategoryChannelService categoryChannelService;

    protected void loadCategories() {
        categories = ChannelHelper.populateCategories(getChannel(), categoryChannelService);
    }

    public List<List<EntityTO>> getCategories() {
        return categories;
    }

    private Channel getChannel() {
        loadChannel();
        return model;
    }

    @Override
    public Account getExistingAccount() {
        return getChannel().getAccount();
    }

    protected void loadChannel() {
        if (model == null) {
            model = findChannel(getId());
        }
    }

    protected abstract T findChannel(Long id);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChannelTriggersTotalsTO getTriggersTotals() {
        if (triggersTotals == null) {
            triggersTotals = channelTriggersStatsService.getTriggersTotals(model.getId());
        }
        return triggersTotals;
    }

    public String getFormattedYesterdayDate() {
        if (formattedYesterdayDate != null) {
            return formattedYesterdayDate;
        }

        TimeZone gmt = TimeZone.getTimeZone("GMT");
        Locale locale = CurrentUserSettingsHolder.getLocale();
        Calendar c = Calendar.getInstance(gmt, locale);
        c.add(Calendar.DATE, -1);

        formattedYesterdayDate = DateHelper.formatDate(c.getTime(), gmt, locale);

        return formattedYesterdayDate;
    }

    public String getCheckStatusCaption() {
        boolean hourlyCheck = true;
        if (model.getInterval() != null) {
            TimeSpan checkInterval = model.getAccount().getAccountType().getChannelCheckByNum(model.getInterval());
            hourlyCheck = checkInterval.getValueInSeconds() / 3600 / 24 < 1;
        }
        return RegularChecksUtil.getCheckStatusCaption(model, CurrentUserSettingsHolder.getLocale(), hourlyCheck);
    }
}
