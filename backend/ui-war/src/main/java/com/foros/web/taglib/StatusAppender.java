package com.foros.web.taglib;

import com.foros.model.DisplayStatus;
import com.foros.model.Status;
import com.foros.util.EntityUtils;

public class StatusAppender {
    public static String appendStatus(String message, Object status) {
        if (status instanceof Status) {
            return EntityUtils.appendStatusSuffix(message, (Status) status);
        } else if (status instanceof DisplayStatus) {
            return EntityUtils.appendStatusSuffix(message, (DisplayStatus)status);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
