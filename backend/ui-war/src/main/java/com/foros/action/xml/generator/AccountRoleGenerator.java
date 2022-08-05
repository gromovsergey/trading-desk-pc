package com.foros.action.xml.generator;

import com.foros.security.AccountRole;

/**
 * Author: Boris Vanin
 * Date: 03.12.2008
 * Time: 15:24:43
 * Version: 1.0
 */
public class AccountRoleGenerator implements Generator<AccountRole> {

    public String generate(AccountRole accountRole) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<account-role id='").append(accountRole.ordinal()).append("'>").append(accountRole.getName()).append("</account-role>");

        return xml.toString();
    }

}
