package com.foros.rs.client.model.advertising.template;

import com.foros.rs.client.util.QueryParameter;

public class CreativeSizeSelector {
    @QueryParameter("id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
