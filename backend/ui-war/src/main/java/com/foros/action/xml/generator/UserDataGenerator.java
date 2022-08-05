package com.foros.action.xml.generator;

import com.foros.action.xml.model.UserData;
import com.foros.util.xml.XmlUtil;

public class UserDataGenerator implements Generator<UserData> {

    public String generate(UserData user) {
        StringBuffer xml = new StringBuffer(Constants.XML_HEADER);

        xml.append("<userData>").
                append(XmlUtil.Generator.tag("email", user.getEmail())).
                append(XmlUtil.Generator.tag("firstName", user.getFirstName())).
                append(XmlUtil.Generator.tag("lastName", user.getLastName())).
                append(XmlUtil.Generator.tag("jobTitle", user.getJobTitle())).
                append(XmlUtil.Generator.tag("phone", user.getPhone())).
                append("</userData>");

        return xml.toString();
    }

}