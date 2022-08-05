package app.programmatic.ui.agentreport.tool.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

public class MoneySerializer extends JsonSerializer<BigDecimal> {

    @Value("${agentReport.currencyAccuracy}")
    private Integer scale;

    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        NumberFormat numberFormat = NumberFormat.getInstance(LOCALE_RU);
        numberFormat.setMaximumFractionDigits(scale);

        jgen.writeString(numberFormat.format(value));
    }
}