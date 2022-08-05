package com.foros.jaxb.adapters;

import com.foros.session.site.creativeApproval.SiteCreativeApprovalOperationType;
import com.foros.util.StringUtil;
import com.foros.validation.code.InputErrors;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Arrays;

public class CreativeApprovaloperationTypeXmlAdapter extends XmlAdapter<String, SiteCreativeApprovalOperationType> {

    @Override
    public String marshal(SiteCreativeApprovalOperationType entity) throws Exception {
        return entity.toString();
    }

    @Override
    public SiteCreativeApprovalOperationType unmarshal(String s) throws Exception {
        try {
            return SiteCreativeApprovalOperationType.valueOf(s);
        } catch (Exception e) {
            throw new LocalizedParseException(
                    InputErrors.XML_ENUM_PARSE_ERROR,
                    "errors.unexpectedEnumValue",
                    Arrays.toString(SiteCreativeApprovalOperationType.values()));
        }
    }

}