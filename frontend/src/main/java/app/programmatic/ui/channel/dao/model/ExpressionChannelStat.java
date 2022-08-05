package app.programmatic.ui.channel.dao.model;

import java.util.List;

public class ExpressionChannelStat {
    private final List<ExpressionHitsTO> channelStat;

    public ExpressionChannelStat(List<ExpressionHitsTO> channelStat) {
        this.channelStat = channelStat;
    }

    public List<ExpressionHitsTO> getChannelStat() {
        return channelStat;
    }
}
