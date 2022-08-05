package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.site.CreativeRejectReason;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalOperationType;
import com.foros.validation.code.InputErrors;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Arrays;

public class CreativeRejectReasonXmlAdapter extends XmlAdapter<String, CreativeRejectReason> {

    @Override
    public String marshal(CreativeRejectReason entity) throws Exception {
        return entity.toString();
    }

    @Override
    public CreativeRejectReason unmarshal(String s) throws Exception {
        try {
            return CreativeRejectReason.valueOf(s);
        } catch (Exception e) {
            throw new LocalizedParseException(
                    InputErrors.XML_ENUM_PARSE_ERROR,
                    "errors.unexpectedEnumValue",
                    Arrays.toString(CreativeRejectReason.values()));
        }
    }

}
