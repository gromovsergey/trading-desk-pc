package com.foros.session.channel.service;

import com.foros.model.channel.Platform;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;

import java.util.Collection;
import java.util.List;
import javax.ejb.Local;

@Local
public interface PlatformService {

    Collection<Platform> findByExpression(String expression);

    List<Platform> findAll();

    Platform findById(Long id);

    Platform findByName(String platformName);

    Result<Platform> get(Paging paging);
}
