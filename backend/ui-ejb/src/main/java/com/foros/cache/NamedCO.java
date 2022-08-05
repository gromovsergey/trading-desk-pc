package com.foros.cache;

/**
 * @author pavel
 * Created on September 14, 2006, 7:37 PM
 */
public class NamedCO<ID> extends CacheObject<ID> {
    public static final NameComparator NAME_COMPARATOR = new NameComparator();
    
    /**
     * Holds value of property name.
     */
    private volatile String name;

    /**
     * Creates a new instance of NamedCO
     * @param id object identifier.
     * @param name name of object.
     */
    public NamedCO(ID id, String name) {
        super(id);
        this.name = name;
    }

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
}
