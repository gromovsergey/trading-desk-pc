package com.foros.session.template.descriptors;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

public class OptionValueSqlType implements SQLData {

    public static final String TABLE_TYPE_NAME = "OPTIONVALUE_TBL";
    public static final String TYPE_NAME = "OPTIONVALUE_REC";

    private Long optionId;
    private String newValue;
    private String oldValue;

    public OptionValueSqlType() {}

    public OptionValueSqlType(Long optionId, String oldValue, String newValue) {
        this.optionId = optionId;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }


    @Override
    public String getSQLTypeName() throws SQLException {
        return TYPE_NAME;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        this.optionId = stream.readLong();
        this.newValue = stream.readString();
        this.oldValue = stream.readString();
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        stream.writeLong(optionId);
        stream.writeString(newValue);
        stream.writeString(oldValue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((newValue == null) ? 0 : newValue.hashCode());
        result = prime * result + ((oldValue == null) ? 0 : oldValue.hashCode());
        result = prime * result + ((optionId == null) ? 0 : optionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OptionValueSqlType other = (OptionValueSqlType) obj;
        if (newValue == null) {
            if (other.newValue != null)
                return false;
        } else if (!newValue.equals(other.newValue))
            return false;
        if (oldValue == null) {
            if (other.oldValue != null)
                return false;
        } else if (!oldValue.equals(other.oldValue))
            return false;
        if (optionId == null) {
            if (other.optionId != null)
                return false;
        } else if (!optionId.equals(other.optionId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "OptionValueSqlType [optionId=" + optionId + ", newValue=" + newValue + ", oldValue=" + oldValue + "]";
    }


}
