package com.foros.session.security.descriptiors;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;

import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

public class AuditUserSqlType implements SQLData {

    public static final String TYPE_NAME = "AUDIT_USER_REC";

    private Long userId;

    private String ip;

    public AuditUserSqlType(Long userId, String ip) {
        this.userId = userId;
        this.ip = ip;
    }

    @Override
    public String getSQLTypeName() throws SQLException {
        return TYPE_NAME;
    }

    @Override
    public void readSQL(SQLInput sqlInput, String s) throws SQLException {
    }

    @Override
    public void writeSQL(SQLOutput sqlOutput) throws SQLException {
        if (userId == null) {
            sqlOutput.writeObject(null);
        } else {
            sqlOutput.writeLong(userId);
        }
        sqlOutput.writeString(ip);
    }

    public static AuditUserSqlType createInstance() {
        Long id = SecurityContext.getPrincipal().getUserId();
        String ip = CurrentUserSettingsHolder.getIp();
        return new AuditUserSqlType(id, ip);
    }
}
