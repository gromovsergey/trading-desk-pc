package com.foros.action.admin.discoverChannelList;

import com.foros.model.channel.DiscoverChannelList;

public class StatusDiscoverChannelListAction extends DiscoverChannelListActionSupport {
    public StatusDiscoverChannelListAction() {
        model = new DiscoverChannelList();
    }
    private Long[] selectedChannels = {};
    private String declinationReason;

    public Long[] getSelectedChannels() {
        return selectedChannels;
    }

    public void setSelectedChannels(Long[] selectedChannels) {
        this.selectedChannels = selectedChannels;
    }

    public String getDeclinationReason() {
        return declinationReason;
    }

    public void setDeclinationReason(String declinationReason) {
        this.declinationReason = declinationReason;
    }

    public String activate() {
        discoverChannelListService.activate(getModel().getId());
        return SUCCESS;
    }

    public String inactivate() {
        discoverChannelListService.inactivate(getModel().getId());
        return SUCCESS;
    }

    public String delete() {
        discoverChannelListService.delete(getModel().getId());
        return SUCCESS;
    }

    public String undelete() {
        discoverChannelListService.undelete(getModel().getId());
        return SUCCESS;
    }

    public String batchActivate() {
        discoverChannelListService.activate(getModel().getId(), getSelectedChannels());
        return SUCCESS;
    }

    public String batchInactivate() {
        discoverChannelListService.inactivate(getModel().getId(), getSelectedChannels());
        return SUCCESS;
    }

    public String batchDelete() {
        discoverChannelListService.delete(getModel().getId(), getSelectedChannels());
        return SUCCESS;
    }
}
