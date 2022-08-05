package com.foros.session.channel;

import com.foros.model.account.Account;
import com.foros.model.channel.ExpressionChannel;

public class ExpressionAssociationTO {
    private Account account;
    private ExpressionChannel expression;

    public ExpressionAssociationTO(ExpressionChannel expression) {
        this.expression = expression;
        this.account = expression.getAccount();
    }

    public Account getAccount() {
        return account;
    }

    public ExpressionChannel getExpression() {
        return expression;
    }
}
