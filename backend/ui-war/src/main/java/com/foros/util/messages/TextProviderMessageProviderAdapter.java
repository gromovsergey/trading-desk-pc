package com.foros.util.messages;

import com.opensymphony.xwork2.TextProvider;

/**
 * Author: Boris Vanin
 * Date: 27.10.2008
 * Time: 14:06:29
 * Version: 1.0
 */
public class TextProviderMessageProviderAdapter extends MessageProvider {

    private TextProvider textProvider;

    public TextProviderMessageProviderAdapter(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    public String getMessage(String key) {
        return textProvider.getText(key);
    }

}
