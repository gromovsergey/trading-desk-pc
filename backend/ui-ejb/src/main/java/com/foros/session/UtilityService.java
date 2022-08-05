package com.foros.session;

import com.foros.aspect.annotation.ElFunction;
import com.foros.model.EntityBase;
import com.foros.model.IdNameEntity;
import com.foros.model.Identifiable;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

@Local
public interface UtilityService {
    <E extends IdNameEntity> String calculateNameForCopy(E entity, int maxLength);

    <E> String calculateNameForCopy(E entity, int maxLength, String originalName, String nameParameter);

    <E> E findById(Class<? extends E> entityClass, Long id);

    @ElFunction
    Object find(String entityClass, Long id);

    <E> E find(Class<? extends E> entityClass, Long id);

    <E> E safeFind(Class<? extends E> entityClass, Long id);

    String getEntityText(Class entityClass, Long id);

    List<String> getEntityTextList(Class entityClass, Collection<Long> ids);

    <C extends Collection<T>, T extends Identifiable> C resolveReferences(C source, C result, Class<T> type);

    boolean isEntityExists(Class<? extends EntityBase> type, Long id);
}
