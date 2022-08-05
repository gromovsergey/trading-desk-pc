<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="/struts-tags" prefix="s" %>

<s:label key="fileman.conversionWarnings"/>
<s:actionerror/>

<script>
    $('#convertDialogSubmit, #convertDialogCancel').hide();
    $('#convertDialogClose').show();
</script>
