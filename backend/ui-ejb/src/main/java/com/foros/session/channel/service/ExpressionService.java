package com.foros.session.channel.service;

import com.foros.model.channel.Channel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.restriction.RestrictionService;
import com.foros.session.channel.exceptions.ExpressionConversionException;

import javax.ejb.Local;
import java.util.Collection;

@Local
public interface ExpressionService {
    Collection<Channel> findChannelsFromExpression(String expression);

    String convertToHumanReadable(String expression) throws ExpressionConversionException;

    void convertToHumanReadable(Collection<ExpressionChannel> channels);

    String convertToHumanReadableWithLinks(String cdml, String baseUrl, RestrictionService restrictionService) throws ExpressionConversionException;

    String convertFromHumanReadable(String expression, String countryCode) throws ExpressionConversionException;

    String convertFromHumanReadable(ConverterContext context, String expression, String countryCode) throws ExpressionConversionException;

    ConverterContext newContext();

    public interface ConverterContext {
        Long findChannelByAccountAndName(String accountName, String channelName, String countryCode);
    }
}
