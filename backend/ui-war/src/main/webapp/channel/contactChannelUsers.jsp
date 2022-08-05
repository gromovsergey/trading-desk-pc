<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:pageHeadingByTitle/>

<s:form action="sendMessage" id="actionSave">
<s:hidden name="id"/>
<s:hidden name="name"/>

    <s:fielderror>
        <s:param value="'version'"/>
        <s:param value="'noUsers'"/>
        <s:param value="'exceedLimit'"/>
    </s:fielderror>

    <ui:section>
        <ui:fieldGroup>
            <ui:field>
                <span class="infos">
                    <fmt:message key="cmp.channel.contact.user.info"/>
                </span>
            </ui:field>

            <ui:field labelKey="cmp.channel.user.message" labelForId="message" required="true" errors="message">
                <s:textarea name="message" id="message" rows="5" cssClass="middleLengthText1" cssStyle="height: 50px"/>
            </ui:field>

            <ui:field id="sendCopy">
                <label class="withInput">
                    <s:checkbox name="sendMeCopyFlag" id="sendCopyFlag"/>
                   <fmt:message key="cmp.channel.user.sendCopy"/>
                </label>
            </ui:field>

            <ui:field>
                <span class="infos">
                    <fmt:message key="cmp.channel.contact.user.message.note"/>
                </span>
            </ui:field>

        </ui:fieldGroup>
    </ui:section>

    <div class="wrapper">
        <ui:button message="form.submit" type="submit"/>
        <ui:button message="form.cancel" action="view?id=${id}" type="button"/>
    </div>

</s:form>
