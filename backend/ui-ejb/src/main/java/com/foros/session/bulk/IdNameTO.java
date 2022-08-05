package com.foros.session.bulk;

import com.foros.model.IdNameEntity;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import javax.xml.bind.annotation.XmlType;

/**
 * Just like NameTO but includes name in equals and hash code.
 */
@XmlType(propOrder = {
        "id",
        "name"
})
public class IdNameTO implements IdNameEntity, Serializable {
    public static final Constructor<IdNameTO> CONSTRUCTOR;
    static {
        try {
            CONSTRUCTOR = IdNameTO.class.getConstructor(Long.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("NamedTO constructor not found", e);
        }
    }

    private Long id;
    private String name;

    public IdNameTO() {
    }

    public IdNameTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdNameTO idNameTO = (IdNameTO) o;

        if (id != null ? !id.equals(idNameTO.id) : idNameTO.id != null) return false;
        if (name != null ? !name.equals(idNameTO.name) : idNameTO.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
