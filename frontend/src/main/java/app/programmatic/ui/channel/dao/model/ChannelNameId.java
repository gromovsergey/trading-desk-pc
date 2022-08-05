package app.programmatic.ui.channel.dao.model;

public class ChannelNameId extends ChannelName {
    private Long id;

    public ChannelNameId() {
    }

    public ChannelNameId(String name, String accountName, Long id) {
        super(name, accountName);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
