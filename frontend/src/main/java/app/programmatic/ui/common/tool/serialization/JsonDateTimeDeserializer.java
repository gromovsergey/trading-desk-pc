package app.programmatic.ui.common.tool.serialization;

import app.programmatic.ui.common.tool.formatting.DateTimeFormatterWrapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;


public class JsonDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext)
            throws IOException, JsonProcessingException {
        return DateTimeFormatterWrapper.parseDateTime(jsonparser.getText());
    }
}
