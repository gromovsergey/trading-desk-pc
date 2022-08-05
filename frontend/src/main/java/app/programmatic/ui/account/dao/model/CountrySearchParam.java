package app.programmatic.ui.account.dao.model;

public class CountrySearchParam {
    private String name;
    private String code;

    public CountrySearchParam(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
