package com.foros.session.security.descriptiors;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;

public class IdNameSqlType implements SQLData {
    public static final String TABLE_TYPE_NAME = "ID_TYPE_LIST";
    public static final String TYPE_NAME = "ID_TYPE_REC";

    private Long objectId;
    private String objectName;


    public IdNameSqlType(Long objectId, String objectName) {
        this.objectId = objectId;
        this.objectName = objectName;
    }

    public String getSQLTypeName() {
        return TYPE_NAME;
    }

    public void writeSQL(java.sql.SQLOutput output) throws SQLException {
        output.writeLong(objectId);
        output.writeString(objectName);
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {}
}
