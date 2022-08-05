package com.foros.action.xml.model;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 16:29:22
 * Version: 1.0
 */
public class CurrencyInfo {

    private String name;
    private String symbol;
    private int fractionDigits;

    public CurrencyInfo(String name, String symbol, int fractionDigits) {
        this.name = name;
        this.symbol = symbol;
        this.fractionDigits = fractionDigits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(int fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

}
