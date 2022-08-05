package com.foros.session.admin.behavioralParameters;

import com.foros.model.channel.BehavioralParametersList;

import javax.ejb.Local;
import java.util.List;

@Local
public interface BehavioralParamsListService {
    List<BehavioralParametersList> findAll();

    BehavioralParametersList find(Long id);

    BehavioralParametersList findWithNoErrors(Long id);

    BehavioralParametersList view(Long id);

    Long create(BehavioralParametersList params);

    void update(BehavioralParametersList params);

    void delete(Long id);

    int getChannelUsageCount(Long id);
}
