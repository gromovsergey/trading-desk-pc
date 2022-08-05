package com.foros.cache.generic.serializer;

public interface Serializer {

    <T> byte[] serialize(T object);

    <T> T deserialize(Class<T> type, byte[] bytes);

}
