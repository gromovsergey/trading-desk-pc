package com.foros.session.site;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

public class KeyValueRecType implements SQLData {
    public static final String TYPE = "NAME_NAME_REC";

    private String key;
    private String name;

    @Override
    public String getSQLTypeName() throws SQLException {
        return TYPE;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        key = stream.readString();
        name = stream.readString();
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
