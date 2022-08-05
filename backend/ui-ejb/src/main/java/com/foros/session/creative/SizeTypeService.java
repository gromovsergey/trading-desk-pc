package com.foros.session.creative;

import com.foros.model.creative.SizeType;

import java.util.List;

import javax.ejb.Local;

@Local
public interface SizeTypeService {
    SizeType findByName(String name);

    void create(SizeType sizeType);

    void update(SizeType sizeType);

    SizeType find(Long id);

    SizeType view(Long id);

    List<SizeTypeTO> findByAccountType(Long accountTypeId);

    List<SizeType> findAll();
}
