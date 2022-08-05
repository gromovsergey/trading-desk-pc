package com.foros.service;

public interface ByIdLocatorService<EntityT> {
    EntityT findById(Long id);

    EntityT view(Long id);
}

