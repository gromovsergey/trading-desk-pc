<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xhtml="true">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><ui:windowTitle attributeName="yandexJobs.title"/></title>
    <ui:stylesheet fileName="common.css" />
</head>
    
<body>
<s:form  action="admin/support/yandexJobs/advertisersSynchronize" method="post">
    <ui:section>
        <ui:fieldGroup>
            <ui:field>
                <h3>To run 'BannerStore.GetTnsAdvertiser' press 'Proceed' button</h3>
            </ui:field>
            <ui:field>
                <ui:button messageText="Proceed" type="submit"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>

<s:form  action="admin/support/yandexJobs/brandsSynchronize" method="post">
    <ui:section>
        <ui:fieldGroup>
            <ui:field>
                <h3>To run 'BannerStore.TnsBrandGetList' press 'Proceed' button</h3>
            </ui:field>
            <ui:field>
                <ui:button messageText="Proceed" type="submit"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>
</body>
</html>
