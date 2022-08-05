package com.foros.session.bulk;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Paging {

    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final int MAX_PAGE_SIZE = 500;

    private Integer count = DEFAULT_PAGE_SIZE;
    private Integer first = 0;

    public Paging() {
    }

    public Paging(Integer first, Integer count) {
        if (first != null) {
            this.first = first;
        }

        if (count != null) {
            this.count = count;
        }
    }

    public Integer getFirst() {
        return first;
    }

    public void setFirst(Integer first) {
        this.first = first;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
