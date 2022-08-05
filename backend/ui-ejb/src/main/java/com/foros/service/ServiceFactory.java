package com.foros.service;

/**
 * Factory class for creaing services.
 */
public interface ServiceFactory {
    /**
     * Is factory supports this url.
     * @param url url to check
     * @return true == supports
     */
    boolean supports(String url);

    /**
     * Creates service with specified interface and url
     * @param tinterface service interface
     * @param url service url
     * @return created service
     * @throws RemoteServiceRegistrationException e
     */
    <T> T create(Class<T> tinterface, String url) throws RemoteServiceRegistrationException;
}
