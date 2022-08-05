package com.foros.session.security.descriptiors;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

public class IdStatusSqlType implements SQLData {

    public static final String TABLE_TYPE_NAME = "ID_STATUS_LIST";
    public static final String TYPE_NAME = "ID_STATUS_REC";

    private Long objectId;
    private char objectStatus;

    public IdStatusSqlType(Long objectId, char objectStatus) {
        this.objectId = objectId;
        this.objectStatus = objectStatus;
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return TYPE_NAME;
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
        stream.writeLong(objectId);
        stream.writeString(String.valueOf(objectStatus));
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
    }
}
