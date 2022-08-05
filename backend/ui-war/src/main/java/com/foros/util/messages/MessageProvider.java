package com.foros.util.messages;

/**
 * Author: Boris Vanin
 * Date: 27.10.2008
 * Time: 14:03:10
 * Version: 1.0
 *
 * Message reources access abstraction
 */
public abstract class MessageProvider {

    /**
     * Resolve message value by key
     *
     * @param key message key
     * @return message value
     */
    public abstract String getMessage(String key);

    private static MessageProvider provider = new SessionContextMessageProviderAdapter();

    /**
    * Create adapter for session context, fetch resources from 'applicationResources'
    *
    * @return adapter for message provider
    */
    public static MessageProvider createMessageProviderAdapter() {
        return provider;
    }

    /**
     * Set message provider
     * @param provider message provider
     */
    public static void setMessageProvider(MessageProvider provider) {
        MessageProvider.provider = provider;
    }

    public static void setDefaultMessageProvider() {
        setMessageProvider(new SessionContextMessageProviderAdapter());
    }

}
