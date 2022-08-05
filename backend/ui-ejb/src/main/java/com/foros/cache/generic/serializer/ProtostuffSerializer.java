package com.foros.cache.generic.serializer;

import com.dyuproject.protostuff.GraphIOUtil;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.springframework.util.Assert;

public class ProtostuffSerializer implements Serializer {

    private static final BufferProvider bufferProvider = new BufferProvider();

    private static class BufferProvider {

        private ThreadLocal<LinkedBuffer> linkedBuffer = new ThreadLocal<LinkedBuffer>();
        private int size;

        public BufferProvider() {
            this(8 * 1024);
        }

        public BufferProvider(int size) {
            this.size = size;
        }

        public LinkedBuffer getBuffer() {
            LinkedBuffer buffer = linkedBuffer.get();

            if (buffer == null) {
                buffer =  LinkedBuffer.allocate(size);
                linkedBuffer.set(buffer);
            }

            return buffer;
        }

    }

    @Override
    public <T> byte[] serialize(T object) {
        Assert.notNull(object);

        Schema<T> schema = RuntimeSchema.getSchema((Class<T>)object.getClass());

        LinkedBuffer buffer = bufferProvider.getBuffer();

        try {
            return GraphIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(Class<T> type, byte[] bytes) {
        Assert.notNull(type);
        Assert.notNull(bytes);

        Schema<T> schema = RuntimeSchema.getSchema(type);

        T object = schema.newMessage();

        GraphIOUtil.mergeFrom(bytes, object, schema);

        return object;
    }
}
