package app.programmatic.ui.common.tool.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import app.programmatic.ui.common.tool.formatting.DateTimeFormatterWrapper;

import java.io.IOException;
import java.time.LocalDate;

public class JsonDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext)
            throws IOException, JsonProcessingException {
        return DateTimeFormatterWrapper.parseDate(jsonparser.getText());
    }
}

