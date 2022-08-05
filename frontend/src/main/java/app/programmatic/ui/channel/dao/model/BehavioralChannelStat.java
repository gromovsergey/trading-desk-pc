package app.programmatic.ui.channel.dao.model;

import java.util.List;

public class BehavioralChannelStat {
    private final List<KeywordHitsTO> keywordHitsStat;
    private final List<KeywordTypeHitsTO> keywordTypeHitsStat;

    public BehavioralChannelStat(List<KeywordHitsTO> keywordHitsStat, List<KeywordTypeHitsTO> keywordTypeHitsStat) {
        this.keywordHitsStat = keywordHitsStat;
        this.keywordTypeHitsStat = keywordTypeHitsStat;
    }

    public List<KeywordHitsTO> getKeywordHitsStat() {
        return keywordHitsStat;
    }

    public List<KeywordTypeHitsTO> getKeywordTypeHitsStat() {
        return keywordTypeHitsStat;
    }
}
