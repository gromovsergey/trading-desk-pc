package com.foros.util;

import com.foros.util.messages.MessageProvider;

import java.text.MessageFormat;

/**
 * Author: Boris Vanin
 * Date: 18.12.2008
 * Time: 14:49:23
 * Version: 1.0
 */
public class FormatResolver implements Resolver {

    private MessageProvider provider;
    private String format;
    private boolean prepare;

    public FormatResolver(MessageProvider provider, String format) {
        this(provider, format, false);
    }

    public FormatResolver(MessageProvider provider, String format, boolean prepare) {
        this.provider = provider;
        this.format = format;
        this.prepare = prepare;
    }

    public String resolve(String name) {
        String prepared = prepare ? MessageHelper.prepareMessageKey(name) : name;
        return provider.getMessage(MessageFormat.format(format, prepared));
    }

}
