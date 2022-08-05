package com.foros.session.cache;

import java.io.Serializable;
import javax.ejb.Local;

@Local
public interface CacheService {
    /**
     * Evicts current object from the second level cache.<br>
     * Method also method initializes cascade evict. Fields marked with <code>@Cascade(org.hibernate.annotations.CascadeType.EVICT)</code> will be triggered for eviction also<br>
     * <b>Note:</b> that this method will be executed outside current transaction, and if particular object participates in previous transaction,
     * then <b>dead lock</b> may occur and TimeOuteException to be thrown.
     * @param entity - the entity to be evicted.
     */
    void evictNonTransactional(Object entity);

    void evictNonTransactional(String persistentClass, Serializable id);

    void evictNonTransactional(Class persistentClass, Serializable id);

    /**
     * Evicts all entities of a given class from cache. Exectutes outside of any transaction.
     * EJBExcpetion will be thrown if no cache registered for a particular class.
     *
     * @param persistentClasses regions to evict 
     */
    void evictRegionNonTransactional(Class... persistentClasses);

    /**
     * Evicts collection field of an object with specified id.
     * Format is the following:  com.foros.model.account.Account.users
     * EJBExcpetion will be thrown if no cache registered for a particular class or collection field is invalid
     *
     * @param collection a string in format className.colletionField
     * @param id the id of entity which collection field will be evicted.
     *
     */
    void evictCollectionNonTransactional(String collection, Serializable id);

    /**
     * Evicts collection field of an object of specified class.
     * Fromat is the following:  com.foros.model.account.Account.users (all user fields in Account will be evicted)
     * EJBExcpetion will be thrown if no cache registered for a particular class or collection field is invalid
     *
     * @param collection a string in format className.colletionField
     *
     */
    void evictCollectionNonTransactional(String collection);

    /**
     *
     * Performs eviction within current transaction.
     * Method also method initializes cascade evict. Fields marked with <code>@Cascade(org.hibernate.annotations.CascadeType.EVICT)</code> will be triggered for eviction also<br>
     *
     * @param entity the entity to be evicted from a cache.
     */
    void evict(Object entity);

    /**
     * @see {@link CacheService#evict(Object)}.
     */
    void evict(Class persistentClass, Serializable id);

    void evict(String persistentClass, Serializable id);

    void evictCollection(Class className, String collectionField);

    void evictCollection(Class className, String collectionField, Serializable id);

    void evictRegion(Class className);
}
