package com.foros.util.messages;

import com.foros.util.StringUtil;

/**
 * Author: Boris Vanin
 * Date: 21.11.2008
 * Time: 11:06:29
 * Version: 1.0
 */
public class SessionContextMessageProviderAdapter extends MessageProvider {

    public String getMessage(String key) {
        return StringUtil.getLocalizedString(key);
    }

}