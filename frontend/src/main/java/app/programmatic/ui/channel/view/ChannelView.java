package app.programmatic.ui.channel.view;

import com.foros.rs.client.model.advertising.channel.Channel;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.tool.foros.StatusHelper;

public class ChannelView {
    private Long id;
    private String name;
    private String type;
    private String country;
    private MajorDisplayStatus displayStatus;

    public ChannelView(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.type = channel.getClass().getSimpleName();
        this.country = channel.getCountry();
        this.displayStatus = StatusHelper.getMajorStatusByRsStatus(channel.getStatus());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCountry() {
        return country;
    }

    public MajorDisplayStatus getDisplayStatus() {
        return displayStatus;
    }
}
