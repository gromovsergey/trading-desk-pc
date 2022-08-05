package app.programmatic.ui.channel.dao.model;

public class ChannelNode {
    private Long id;
    private String name;
    private String text;
    private Boolean hasChildren;

    public ChannelNode(Long id, String name, String text, Boolean hasChildren) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.hasChildren = hasChildren;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
