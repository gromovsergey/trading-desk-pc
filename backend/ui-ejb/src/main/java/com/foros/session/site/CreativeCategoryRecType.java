package com.foros.session.site;

import com.foros.model.creative.RTBCategory;

import java.sql.Array;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {
        "name",
        "rtbCategories"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class CreativeCategoryRecType implements SQLData {
    public static final String TYPE = "CREATIVE_CATEGORY_REC";

    private String name;
    private List<KeyValueRecType> rtbCategories = new ArrayList<>();

    @Override
    public String getSQLTypeName() throws SQLException {
        return TYPE;
    }

    @Override
    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        stream.readLong();
        name = stream.readString();
        Array array = stream.readArray();
        for (Object keyValue : Arrays.asList((Object[]) array.getArray())) {
            rtbCategories.add((KeyValueRecType) keyValue);
        }
    }

    @Override
    public void writeSQL(SQLOutput stream) throws SQLException {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "rtbCategory")
    @XmlElementWrapper(name = "rtbCategories")
    public List<KeyValueRecType> getRtbCategories() {
        return rtbCategories;
    }

    public void setRtbCategories(List<KeyValueRecType> rtbCategories) {
        this.rtbCategories = rtbCategories;
    }

}
