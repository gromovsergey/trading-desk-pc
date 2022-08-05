package com.foros.session.security.descriptiors.oraclefinance;

import java.math.BigDecimal;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

class OnAccountCreditSqlType implements SQLData {

    public static final String TYPE_NAME = "AR25_DATA_RECORD";

    private Long accountId;
    private BigDecimal onAccountCredit;

    OnAccountCreditSqlType(Long accountId, BigDecimal onAccountCredit) {
        this.accountId = accountId;
        this.onAccountCredit = onAccountCredit;
    }

    public String getSQLTypeName() {
        return TYPE_NAME;
    }

    public void writeSQL(SQLOutput output) throws SQLException {
        output.writeBigDecimal(BigDecimal.valueOf(accountId));
        output.writeBigDecimal(onAccountCredit);
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {}
}
