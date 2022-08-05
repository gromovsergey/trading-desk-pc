package com.foros.session.channel.service;

import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersChannel;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultKeywordSettingsTO implements BehavioralParametersChannel {

    private Map<Character, Timestamp> versions = new HashMap<Character, Timestamp>();
    private Set<BehavioralParameters> behavioralParameters = new HashSet<BehavioralParameters>();

    public Map<Character, Timestamp> getVersions() {
        return versions;
    }

    @Override
    public Set<BehavioralParameters> getBehavioralParameters() {
        return behavioralParameters;
    }

    @Override
    public void setBehavioralParameters(Set<BehavioralParameters> behavioralParameters) {
        this.behavioralParameters = behavioralParameters;
    }
}
