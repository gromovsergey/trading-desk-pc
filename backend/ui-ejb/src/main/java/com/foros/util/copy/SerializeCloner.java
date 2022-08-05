package com.foros.util.copy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author oleg_roshka
 */
public class SerializeCloner implements Cloner {
    public SerializeCloner() {
    }

    public Object clone(Object bean, ClonerContext context) {
        try {
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);

                oos.writeObject(bean);

                oos.flush();
                ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
                ois = new ObjectInputStream(bin);

                return ois.readObject();
            } finally {
                oos.close();
                ois.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("SerializeCloner exception", e);
        }
    }
}
