<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{value!=null && value!=''}">
    <ui:section titleKey="report.notices">
        <s:property value="value" escape="false"/>
    </ui:section>
</s:if>

<s:if test="%{advertiserNotice.value!=null && advertiserNotice.value!=''}">
    <ui:section titleKey="report.notices">
        <s:property value="advertiserNotice.value" escape="false"/>
    </ui:section>
</s:if>

<s:if test="%{publisherNotice.value!=null && publisherNotice.value!=''}">
    <ui:section titleKey="report.notices">
        <s:property value="publisherNotice.value" escape="false"/>
    </ui:section>
</s:if>