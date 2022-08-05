/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.foros.util;

import java.sql.Timestamp;

public class UITimestamp extends Timestamp {

    public UITimestamp() {
        super(System.currentTimeMillis());
    }

    public UITimestamp(long milis) {
        super(milis);
    }

    public UITimestamp(Timestamp ts) {
        super(ts.getTime());
        setNanos(ts.getNanos());
    }

    public UITimestamp(String time) {
        super(System.currentTimeMillis());
        setFullTime(time);
    }

    public String getFullTime() {
        return getTime() + "/" + getNanos();
    }

    public void setFullTime(String time) {
        if (time != null && StringUtil.isPropertyNotEmpty(time)) {
            String[] array = time.split("[/]");
            setTime(Long.parseLong(array[0]));
            setNanos(Integer.parseInt(array[1]));
        }
    }
}
