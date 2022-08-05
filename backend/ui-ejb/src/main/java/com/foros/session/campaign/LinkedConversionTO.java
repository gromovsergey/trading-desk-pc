package com.foros.session.campaign;

import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.model.action.ConversionCategory;

import java.math.BigDecimal;

public class LinkedConversionTO {

    private Long id;
    private String name;
    private ConversionCategory category;
    private Status status;
    private DisplayStatus displayStatus;
    private Long impConv;
    private BigDecimal impCR;
    private Long clickConv;
    private BigDecimal clickCR;

    public static class Builder {
        private Long id;
        private String name;
        private ConversionCategory category;
        private Status status;
        private DisplayStatus displayStatus;
        private Long impConv;
        private BigDecimal impCR;
        private Long clickConv;
        private BigDecimal clickCR;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder category(ConversionCategory category) {
            this.category = category;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder displayStatus(DisplayStatus displayStatus) {
            this.displayStatus = displayStatus;
            return this;
        }

        public Builder impConv(Long impConv) {
            this.impConv = impConv;
            return this;
        }

        public Builder impCR(BigDecimal impCR) {
            this.impCR = impCR;
            return this;
        }

        public Builder clickConv(Long clickConv) {
            this.clickConv = clickConv;
            return this;
        }

        public Builder clickCR(BigDecimal clickCR) {
            this.clickCR = clickCR;
            return this;
        }

        public LinkedConversionTO build() {
            return new LinkedConversionTO(this);
        }
    }

    private LinkedConversionTO(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.category = builder.category;
        this.status = builder.status;
        this.displayStatus = builder.displayStatus;
        this.impConv = builder.impConv;
        this.impCR = builder.impCR;
        this.clickConv = builder.clickConv;
        this.clickCR = builder.clickCR;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ConversionCategory getCategory() {
        return category;
    }

    public Status getStatus() {
        return status;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public Long getImpConv() {
        return impConv;
    }

    public BigDecimal getImpCR() {
        return impCR;
    }

    public Long getClickConv() {
        return clickConv;
    }

    public BigDecimal getClickCR() {
        return clickCR;
    }

}
