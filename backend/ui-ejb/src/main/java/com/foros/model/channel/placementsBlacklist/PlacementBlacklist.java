package com.foros.model.channel.placementsBlacklist;

import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.model.channel.PlacementBlacklistChannel;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.User;
import com.foros.util.CollectionUtils;

import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.ObjectUtils;


public class PlacementBlacklist extends EntityBase implements Identifiable {
    private Long id;
    private PlacementBlacklistChannel channel;
    private String url;
    private CreativeSize size;
    private Set<BlacklistReason> reason;
    private Date dateAdded;
    private User user;
    private BlacklistAction action;

    public PlacementBlacklist() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlacementBlacklistChannel getChannel() {
        return channel;
    }

    public void setChannel(PlacementBlacklistChannel channel) {
        this.channel = channel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CreativeSize getSize() {
        return size;
    }

    public void setSize(CreativeSize size) {
        this.size = size;
    }

    public String getSizeName() {
        return size == null ? null : size.getDefaultName();
    }

    public void setSizeName(String sizeName) {
        if (size == null) {
            size = new CreativeSize();
        }
        size.setDefaultName(sizeName);
    }

    public Set<BlacklistReason> getReason() {
        return reason;
    }

    public String getReasonAsString() {
        return CollectionUtils.toString(reason);
    }

    public void setReason(Set<BlacklistReason> reason) {
        this.reason = reason;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BlacklistAction getAction() {
        return action;
    }

    public void setAction(BlacklistAction action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof PlacementBlacklist)) {
            return false;
        }

        PlacementBlacklist placement = (PlacementBlacklist) o;
        if (!ObjectUtils.equals(url, placement.getUrl())) {
            return false;
        }
        if (!ObjectUtils.equals(getSizeName(), placement.getSizeName())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (getSizeName() != null ? getSizeName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return PlacementBlacklist.class.getSimpleName() +
                " [ " + "sizeName=" + getSizeName() +
                "; url=" + getUrl() +
                "; reason=" + reasonToString() +
                "; dateAdded=" + getDateAdded() +
                "; userId=" + getUser().getId() + " ]";
    }

    private String reasonToString() {
        if (reason == null) {
            return "";
        }
        StringBuilder result = new StringBuilder(getReason().size());
        for (BlacklistReason reason : getReason()) {
            result.append(reason.getCode());
        }
        return result.toString();
    }
}
