package com.foros.session.channel.service;

import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.Channel;

import java.util.Collection;

public interface BehavioralParametersFinder<C extends Channel> {

    Collection<BehavioralParameters> getBehavioralParameters(C channel);

}
