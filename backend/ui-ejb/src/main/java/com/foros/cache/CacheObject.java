package com.foros.cache;

/**
 * @author pavel
 * Created on September 11, 2006, 4:50 PM
 */
public class CacheObject<ID> {
    /**
     * Creates a new instance of CacheObject.
     * @param id object identifier.
     */
    public CacheObject(ID id) {
        this.id = id;
    }

    /**
     * Holds value of property id.
     */
    private final ID id;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public ID getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CacheObject that = (CacheObject) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }
}
