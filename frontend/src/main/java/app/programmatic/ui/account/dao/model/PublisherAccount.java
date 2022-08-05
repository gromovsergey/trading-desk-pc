package app.programmatic.ui.account.dao.model;

public class PublisherAccount extends Account {
    private String name;
    private String currencyCode;
    private int currencyAccuracy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public int getCurrencyAccuracy() {
        return currencyAccuracy;
    }

    public void setCurrencyAccuracy(int currencyAccuracy) {
        this.currencyAccuracy = currencyAccuracy;
    }
}
