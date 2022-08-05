package com.foros.audit.changes;

import java.io.Serializable;

public class ChangeRecord implements Serializable {
    private final long primaryKey;
    private final String className;
    private int hashCode;

    public ChangeRecord(Long primaryKey, String className) {
        if (className == null) {
            throw new IllegalArgumentException("className");
        }

        this.primaryKey = primaryKey;
        this.className = className;

        initializeHash();
    }

    private void initializeHash() {
        hashCode = (int) (primaryKey ^ (primaryKey >>> 32));
        hashCode = 31 * hashCode + className.hashCode();
    }

    public long getPrimaryKey() {
        return primaryKey;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final ChangeRecord other = (ChangeRecord)obj;

        if (this.hashCode != other.hashCode) {
            return false;
        }

        if (this.primaryKey != other.primaryKey) {
            return false;
        }
        
        if (!this.className.equals(other.className)) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "ChangeRecord: primaryKey=" + primaryKey + ", class=" + className;
    }
}
