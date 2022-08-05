package com.foros.reporting.tools.olap.query.builder;

import java.util.ArrayList;
import java.util.List;

public class MemberSetAndOrderHolder {
    private List<MeasureAndOrder> measures;
    private List<LevelAndOrder> levels;

    public MemberSetAndOrderHolder() {
        this.measures = new ArrayList<>();
        this.levels = new ArrayList<>();
    }

    public void addLevelAndOrder(LevelAndOrder level) {
        this.levels.add(level);
    }

    public void addMeasureAndOrder(MeasureAndOrder measure) {
        this.measures.add(measure);
    }

    public List<LevelAndOrder> getLevels() {
        return levels;
    }

    public List<MeasureAndOrder> getMeasures() {
        return measures;
    }

    public boolean hasLevels() {
        return !this.levels.isEmpty();
    }

    public boolean hasMeasures() {
        return !this.measures.isEmpty();
    }
}
