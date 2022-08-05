package com.foros.session.channel.service;

import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.util.StringUtil;

import java.util.Arrays;
import java.util.Collection;

public enum AdvertisingChannelType {
    EXPRESSION('E', ExpressionChannel.class),
    BEHAVIORAL('B', BehavioralChannel.class),
    AUDIENCE('A', AudienceChannel.class);

    private char alias;
    private Class<? extends Channel> type;

    AdvertisingChannelType(char alias, Class<? extends Channel> type) {
        this.alias = alias;
        this.type = type;
    }

    public Class<? extends Channel> getType() {
        return type;
    }

    public char getAlias() {
        return alias;
    }

    public static AdvertisingChannelType byAlias(String alias) {
        for (AdvertisingChannelType type : AdvertisingChannelType.values()) {
            if (String.valueOf(type.getAlias()).equals(alias)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Can't find advertising channel type with alias " + alias);
    }

    public static AdvertisingChannelType[] byAliases(String... alias) {
        if (alias == null || alias.length == 0 || StringUtil.isPropertyEmpty(alias[0])) {
            return values();
        }

        AdvertisingChannelType[] result = new AdvertisingChannelType[alias.length];
        for (int i = 0; i < alias.length; i++) {
            result[i] = byAlias(alias[i]);
        }

        return result;
    }

    public static String[] aliases(Collection<AdvertisingChannelType> types) {
        if (types == null || types.size() == 0) {
            types = Arrays.asList(values());
        }

        String[] result = new String[types.size()];
        int i = 0;
        for (AdvertisingChannelType type : types) {
            result[i++] = String.valueOf(type.getAlias());
        }
        return result;
    }

}
