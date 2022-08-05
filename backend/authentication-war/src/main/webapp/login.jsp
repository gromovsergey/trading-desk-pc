<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="ad" uri="WEB-INF/AdServerUI.tld" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head profile="http://www.w3.org/2005/10/profile">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="login.button"/> - <fmt:message key="systemTitle"/></title>
    <link rel="icon" href="/images/logo.png" />
    <link rel="shortcut icon" href="/images/logo.png" />

    <script type="text/javascript" src="/thirdparty/jQuery/1.8.2/jquery.min.js"></script>
    <script type="text/javascript" src="/scripts/${timestampVersion}/cookie.js"></script>

    <link rel="stylesheet" href="/styles/${timestampVersion}/common.css">

    <c:if test="${hieroglyphSupport}">
        <link rel="stylesheet" href="/styles/hieroglyph.css">
    </c:if>
</head>
<body class="b-loginbody">
    <form name="LoginForm" id="login_form" method="post" action="j_spring_security_check" autocomplete="off" class="b-loginform">
        <dl class="b-loginform__fields">
            <c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
                <dt/>
                <dd><span class="errors"><fmt:message key="errors.loginInvalid"/></span></dd>
            </c:if>
            <dt>
                <label for="login">
                    <fmt:message key="login.name"/>
                </label>
            </dt>
            <dd>
                <c:set var="loginInputType" value="${ad:isMobileAgent(pageContext.request) ? 'email' : 'text'}"/>
                <c:choose>
                    <c:when test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
                        <input type="${loginInputType}" name="j_username" maxlength="320" value="${fn:escapeXml(wrongUsername)}" id="login" class="smallLengthText2">
                    </c:when>
                    <c:otherwise>
                        <input type="${loginInputType}" name="j_username" maxlength="320" id="login" class="smallLengthText2">
                    </c:otherwise>
                </c:choose>
            </dd>
            <dt>
                <label for="password">
                    <fmt:message key="login.password"/>
                </label>
            </dt>
            <dd>
                <input type="password" name="j_password" maxlength="50" value="" id="password" class="smallLengthText2">
            </dd>
            <dt></dt>
            <dd class="">
                <input class="b-loginform__field-checkbox" type="checkbox" name="save_login" id="save_login" value="true"/><label for="save_login"><fmt:message key="login.save.login"/></label>
            </dd>
            <dt></dt>
            <dd class="b-loginform__field-wide">
                <fmt:message key="login.button" var="loginValue"/>
                <input type="submit" name="" formnovalidate="formnovalidate" value="${loginValue}"/>
            </dd>
            <dt></dt>
            <dd>
                <fmt:message key="password.Assistance.forgotPassword"/>
                <a href="../forgotPassword/main.action"><fmt:message key="password.Assistance.clickHere"/></a>
            </dd>
        </dl>
    </form>

    <script type="text/javascript">
        $(function(){
            var iExpireDays   = 30;
        
            if (Cookie.read("save_login")){
                var sUsername = Cookie.read("j_username");
                sUsername && $('#login').val(sUsername);
                $('#save_login').prop({"checked": true});
            }

            if ($('#login').val().length > 0) {
                $('#password').focus();
            } else {
                $('#login').focus();
            }

            $('#login_form').on('submit', function(){
                Cookie.create('j_username', $('#login').val(), iExpireDays);
                Cookie.create('save_login', ($('#save_login').is(':checked') ? $('#save_login').val() : ''), iExpireDays);
            });
        });
    </script>
</body>
</html>
