<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>

<c:if test="${wizardFunctionalityEnabled}">
    <s:hidden name="navigateBack" value="true"/>
    <s:hidden name="countryCode"/>
    <s:hidden name="namingConvention"/>
    <s:hidden name="channelTargetsList"/>
    <s:hidden name="channelTargetIds"/>
</c:if>
