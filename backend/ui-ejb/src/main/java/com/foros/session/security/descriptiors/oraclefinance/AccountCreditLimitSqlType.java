package com.foros.session.security.descriptiors.oraclefinance;

import java.math.BigDecimal;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

class AccountCreditLimitSqlType implements SQLData {

    public static final String TYPE_NAME = "AR05_DATA_RECORD";

    private Long accountId;
    private BigDecimal creditLimit;

    AccountCreditLimitSqlType(Long accountId, BigDecimal creditLimit) {
        this.accountId = accountId;
        this.creditLimit = creditLimit;
    }

    public String getSQLTypeName() {
        return TYPE_NAME;
    }

    public void writeSQL(SQLOutput output) throws SQLException {
        output.writeBigDecimal(BigDecimal.valueOf(accountId));
        output.writeBigDecimal(creditLimit);
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {}
}
