package app.programmatic.ui.account.dao.model;

public class StatusSearchParam {
    private AccountDisplayStatusParam type;
    private String name;

    public AccountDisplayStatusParam getType() {
        return type;
    }

    public void setType(AccountDisplayStatusParam type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
