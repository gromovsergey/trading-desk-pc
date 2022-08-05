<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<script>
    function confirmConversion() {
        <s:if test="targetFileExists">
        <fmt:message key="fileman.warning.fileWillBeOverwritten" var="confirmConvertionMessage1">
        <fmt:param value="${targetFileName}"/>
        </fmt:message>
        </s:if>
        var msg1 = '<c:out value="${confirmConvertionMessage1}"/>';

        <s:if test="targetWithMacroFileExists">
        <fmt:message key="fileman.warning.fileWillBeOverwritten" var="confirmConvertionMessage2">
        <fmt:param value="${targetFileNameWithMacro}"/>
        </fmt:message>
        </s:if>
        var msg2 = '<c:out value="${confirmConvertionMessage2}"/>';

        return msg1.length > 0 || msg2.length > 0 ? confirm( msg1 + ' ' + msg2) : true;
    }
</script>
