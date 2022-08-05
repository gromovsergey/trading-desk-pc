package com.foros.session.creative;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreativeCategoryTO implements Comparable<CreativeCategoryTO> {
    private Long id;
    private String name;
    private List<String> rtbCategories = new ArrayList<>();
    private Map<String, String> localisationMap;

    @Override
    public int compareTo(CreativeCategoryTO o) {
        return name.compareTo(o.getName());
    }

    public Long getId() {
        return id;
    }

    public Map<String, String> getLocalisationMap() {
        return localisationMap;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocalisationMap(Map<String, String> localisationMap) {
        this.localisationMap = localisationMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalisationMapStr() {
        StringBuilder sb = new StringBuilder();
        if (localisationMap != null) {
            StringBuilder locSb = new StringBuilder();
            for (Map.Entry<String , String> entry : localisationMap.entrySet()) {
                locSb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
            }
            sb.append(locSb);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "CreativeCategoryTO[id=" + id + ", name=" + name
                + ", rtbCategories=" + getRtbCategories()
                + ", localisationMap=" + localisationMap + "]";
    }

    public List<String> getRtbCategories() {
        return rtbCategories;
    }

    public void setRtbCategories(List<String> rtbCategories) {
        this.rtbCategories = rtbCategories;
    }

}
