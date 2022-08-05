package com.foros.web.taglib.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PagesModel {

    public static class PageInfo {
        private int number;
        private boolean selected;

        public PageInfo(int number, boolean selected) {
            this.number = number;
            this.selected = selected;
        }

        public int getNumber() {
            return number;
        }

        public boolean isSelected() {
            return selected;
        }

    }

    private int pageSize = 20;
    private int total = 0;
    private int selectedNumber = 0;
    private int visiblePageCount = 10;

    private List<PageInfo> pages = null;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSelectedNumber() {
        return selectedNumber;
    }

    public int getStartCount() {
        return (getSelectedNumber() - 1) * getPageSize() + 1;
    }

    public int getEndCount() {
        return isLast() == true ? getTotal() : getSelectedNumber() * getPageSize(); 
    }

    public void setSelectedNumber(int selectedNumber) {
        this.selectedNumber = selectedNumber;
    }

    private List<PageInfo> initPages() {
        if (pages != null) {
            return pages;
        }

        this.pages = new ArrayList<PageInfo>();

        int pageCount = getPageCount();

        int half = getHalf();

        int begin = selectedNumber <= half ? 1 : selectedNumber - half;
        int end = selectedNumber >= pageCount - half ? pageCount : selectedNumber + half;

        for (int i = begin; i <= end; i++) {
            this.pages.add(new PageInfo(i, i == selectedNumber));
        }

        return this.pages;
    }

    private int getHalf() {
        return visiblePageCount / 2;
    }

    private int getPageCount() {
        return Double.valueOf(Math.ceil((float)total / pageSize)).intValue();
    }

    public List<PageInfo> getPages() {
        return Collections.unmodifiableList(initPages());
    }

    public boolean isFirst() {
        return selectedNumber == 1;
    }

    public boolean isLast() {
        return selectedNumber == getPageCount();
    }

    public boolean isLess() {
        return selectedNumber - getHalf() > 1;
    }

    public boolean isMore() {
        return selectedNumber + getHalf() < getPageCount();
    }

    public int getCount() {
        return getPageCount();
    }

    public boolean isPagingNeeded() {
        return getPageCount() > 1;
    }

    public int getVisiblePageCount() {
        return visiblePageCount;
    }

    public void setVisiblePageCount(int visiblePageCount) {
        this.visiblePageCount = visiblePageCount;
    }
}
