<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xhtml="true">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><ui:windowTitle attributeName="Manual execution of db jobs" isSimpleText="true"/></title>
    <ui:stylesheet fileName="common.css" />
</head>
    
<body>

<s:form  action="admin/support/dbJobs/checkThresholdChannelByUsers" method="post">
    <ui:section>
        <ui:fieldGroup>
            <ui:field>
                <h3>
                    To run displaystatus.update_channel_status_by_stats()
                    (check Threshold qualification by users for Advertising channels) press 'Proceed' button
                </h3>
            </ui:field>
            <ui:field>
                <ui:button messageText="Proceed" type="submit"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>

<s:form  action="admin/support/dbJobs/checkPendingInactivation" method="post">
    <ui:section>
        <ui:fieldGroup>
            <ui:field>
                <h3>To run cmpchannels.check_pending_inactivation press 'Proceed' button</h3>
            </ui:field>
            <ui:field>
                <ui:button messageText="Proceed" type="submit"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>

<%--ToDo: uncomment when needed (OUI-28825)--%>
<%--<s:form  action="admin/support/dbJobs/checkBillingDate" method="post">--%>
    <%--<ui:section>--%>
        <%--<ui:fieldGroup>--%>
            <%--<ui:field>--%>
                <%--<h3>--%>
                    <%--To run billing.generate_invoices_and_bills() (Check the billing date and run the private procedure to--%>
                    <%--populate data into invoice tables), press 'Proceed' button--%>
                <%--</h3>--%>
            <%--</ui:field>--%>
            <%--<ui:field>--%>
                <%--<ui:button messageText="Proceed" type="submit"/>--%>
            <%--</ui:field>--%>
        <%--</ui:fieldGroup>--%>
    <%--</ui:section>--%>
<%--</s:form>--%>

<s:form  action="admin/support/dbJobs/calcCTR" method="post">
    <ui:section>
        <ui:fieldGroup>
            <ui:field>
                <h3>Calculate CTR</h3>
            </ui:field>
            <ui:field>
                <ui:button messageText="Proceed" type="submit"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>

</body>
</html>
