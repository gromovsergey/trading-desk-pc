package com.foros.action;

import com.foros.util.StringUtil;

public class IdNameBean {
    private String id;
    private String name;

    public IdNameBean() {
    }

    public IdNameBean(String id) {
        this.id = id;
    }

    public IdNameBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = StringUtil.isPropertyEmpty(id) ? null : id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IdNameBean that = (IdNameBean)o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "IdNameBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
    
}
