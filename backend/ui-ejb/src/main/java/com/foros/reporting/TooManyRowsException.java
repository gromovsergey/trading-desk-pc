package com.foros.reporting;

import com.foros.util.StringUtil;

import javax.ejb.ApplicationException;

@ApplicationException
public class TooManyRowsException extends RuntimeException {

    private int maxRows;

    public TooManyRowsException(int maxRows) {
        super(StringUtil.getLocalizedString("error.report.tooManyRows.reporting", maxRows));
        this.maxRows = maxRows;
    }

    public int getMaxRows() {
        return maxRows;
    }
}
