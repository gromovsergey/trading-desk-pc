<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">

    $().ready(function(){
        $('form :input[type=text], form :password').keypress(function(e){
            if(e.which == 13) $(this.form).submit();
        });
    });
    
</script>

<ui:pageHeadingByTitle/>

<s:if test="hasActionErrors()">
    <div style="margin-top: 5px; margin-bottom: 5px">
        <s:actionerror/>
    </div>
</s:if>
<s:else>
<s:form action="forgotPassword/send">
    <script type="text/javascript">
        $('form').attr({autocomplete : 'off'});
    </script>
    <span class="infos"><fmt:message key="password.Assistance.text"/></span>
    <ui:section>
        <ui:fieldGroup>
            
            <ui:field labelKey="user.email" labelForId="send_email" required="true" errors="email">
                <s:textfield name="userEmail" cssClass="middleLengthText" maxLength="320"/>
            </ui:field>
            
            <ui:field labelKey="kaptcha.text" labelForId="send_captcha" required="true" errors="captcha">
                <s:textfield name="captcha" cssClass="middleLengthText" maxLength="50"/>
            </ui:field>
            
            <ui:field>
                <img src="/images/captcha.jpg" width="200" height="50" />
            </ui:field>
            
            <ui:field cssClass="withButton">
                <ui:button message="password.Assistance.field.sendButton" type="submit"/>
            </ui:field>
            
        </ui:fieldGroup>
    </ui:section>
</s:form>
</s:else>
