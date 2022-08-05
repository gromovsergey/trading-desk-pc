package com.foros.action.xml.generator;

import com.foros.model.security.AccountType;
import com.foros.security.AccountRole;
import com.foros.util.xml.XmlUtil;

public class AccountTypeFlagsGenerator implements Generator<AccountType> {
    @Override
    public String generate(AccountType accountType) {
        StringBuilder xml = new StringBuilder(Constants.XML_HEADER);

        xml.append("<account-type>");
        if (accountType != null) {
            xml.append(XmlUtil.Generator.tag("allowFreqCap", accountType.isFreqCapsFlag())).
                append(XmlUtil.Generator.tag("allowInventoryEstimation", accountType.isPublisherInventoryEstimationFlag())).
                append(XmlUtil.Generator.tag("allowAdvExclusions", accountType.isAdvExclusionFlag())).
                append(XmlUtil.Generator.tag("isAgency", accountType.getAccountRole() == AccountRole.AGENCY));
        }

        xml.append("</account-type>");

        return xml.toString();
    }

}
