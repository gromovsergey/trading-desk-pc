package app.programmatic.ui.audienceresearch.tool;

public class PostgreChannelPairUserType {
    private Long id1;
    private Long id2;

    public PostgreChannelPairUserType(Long id1, Long id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public String toString() {
        return "(" + id1 + "," + id2 + ")";
    }
}
