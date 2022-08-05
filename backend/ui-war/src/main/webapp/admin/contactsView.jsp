<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<ui:pageHeadingByTitle/>
<ui:fieldGroup>
    <ui:field labelKey="contacts.accountManager">
        <table width="100%" cellpadding="0" cellspacing="0">
            <tr>
                <td><c:out value="${manager.firstName} ${manager.lastName}"/></td>
            </tr>
            <tr>
                <td>${manager.email}</td>
            </tr>
            <tr>
                <td>${manager.phone}</td>
            </tr>
        </table>
    </ui:field>
    <ui:field labelKey="contacts.generalInfo">
        <table width="100%" cellpadding="0" cellspacing="0">
            <tr>
                <td>${contact.email}</td>
            </tr>
            <tr>
                <td>${contact.phone}</td>
            </tr>
        </table>
    </ui:field>
</ui:fieldGroup>