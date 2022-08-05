<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<input id="changePasswordUrl" readonly="readonly" type="text" style="width: 100%" value="${changePasswordUrl}"/>
<div>
    <fmt:message key="user.passwordResetLink.info"/>
</div>

<script type="text/javascript">
    $(function() {
        $('#changePasswordUrl').select();
    })
</script>