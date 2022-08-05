package app.programmatic.ui.common.validation.pathalias;

public class ValidationPathAlias {
    private String name;
    private String alias;

    public ValidationPathAlias(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }
}
