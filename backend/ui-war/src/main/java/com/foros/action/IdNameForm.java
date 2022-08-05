package com.foros.action;

public class IdNameForm<NameT> extends IdForm {
    private NameT name;

    public IdNameForm() {
        super();
    }

    public IdNameForm(String id) {
        super(id);
    }

    public NameT getName() {
        return name;
    }

    public void setName(NameT name) {
        this.name = name instanceof String ? (NameT) (String) name : name;
    }
}
