package app.programmatic.ui.common.tool.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import app.programmatic.ui.common.tool.formatting.DateTimeFormatterWrapper;

import java.io.IOException;
import java.time.LocalDate;

public class JsonDateSerializer extends JsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate src, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
        jsonGenerator.writeString(DateTimeFormatterWrapper.format(src));
    }
}
