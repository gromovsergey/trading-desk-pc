package com.foros.audit.serialize.serializer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class AuditSerializerSupport implements AuditSerializer {
    @Override
    public void endSerialize(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }
}
