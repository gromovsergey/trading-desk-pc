package app.programmatic.ui.agentreport.tool.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

public class MoneyDeserializer extends JsonDeserializer<BigDecimal> {

    @Value("${agentReport.currencyAccuracy}")
    private Integer scale;

    @Override
    public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        NumberFormat numberFormat = NumberFormat.getInstance(LOCALE_RU);
        numberFormat.setMaximumFractionDigits(scale);

        ParsePosition parsePosition = new ParsePosition(0);
        Number number = numberFormat.parse(jsonParser.getText(), parsePosition);
        if (parsePosition.getIndex() != jsonParser.getText().length()) {
            throw new InvalidFormatException(jsonParser, "Unparseable number", jsonParser.getText(), BigDecimal.class);
        }

        return new BigDecimal(number.toString());
    }
}