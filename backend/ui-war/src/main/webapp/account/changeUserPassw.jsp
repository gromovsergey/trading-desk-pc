<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
    function checkStrength(val){
        $('.errors').html('');
        showScore(val, 'pstrength');
    }
    
    function showScore(pwd, id) {
        var status = UI.Password.getStatus(pwd);
        $('#'+id).text(pwd.length ? status.msg : '').css('color', status.color);
    }

    $(function(){
        UI.Password.setStatusMessages([
            {msg:'<fmt:message key="login.passwordStrength.short"/>',   color:'#f00',       score:0},
            {msg:'<fmt:message key="login.passwordStrength.weak"/>',    color:'#999',       score:1},
            {msg:'<fmt:message key="login.passwordStrength.medium"/>',  color:'#000',       score:2},
            {msg:'<fmt:message key="login.passwordStrength.good"/>',    color:'#17D22F',    score:3},
            {msg:'<fmt:message key="login.passwordStrength.strong"/>',  color:'#00970C',    score:4}
        ]);
        
        $('form[name=ChangePasswordForm]').attr({autocomplete : 'off'});
        
        $('#newPassword').keyup(function(){
            checkStrength(this.value);
        }).blur(function(){
            $(this).keyup();
        }).bind('input paste', function(){
            var curr = $(this);
            setTimeout(function(){
                curr.keyup();
            }, 10);
        });
    });
</script>

<s:form action="%{#request.moduleName}/changePasswordSave">
<s:hidden name="id"/>
<ui:pageHeadingByTitle/>

<ui:section titleKey="form.main">
    <ui:fieldGroup>
        
        <ui:field labelKey="user.oldPassword" labelForId="oldPassword" required="true" errors="oldPassword">
            <s:password name="oldPassword" id="oldPassword" cssClass="middleLengthText" maxlength="50"/>
        </ui:field>
        
        <ui:field labelKey="user.newPassword" labelForId="newPassword" required="true" errors="newPassword">
            <s:password name="newPassword" id="newPassword" cssClass="middleLengthText" maxlength="50"/>
            <span id="pstrength"></span>
        </ui:field>
        
        <ui:field labelKey="user.confirmNewPassword" labelForId="confirmNewPassword" required="true" errors="repeatedPassword">
            <s:password name="repeatedPassword" id="confirmNewPassword" cssClass="middleLengthText" maxlength="50"/>
        </ui:field>
        
    </ui:fieldGroup>
</ui:section>

<div class="wrapper">
    <ui:button message="form.save" type="submit"/>
    <ui:button message="form.cancel" onclick="location='view.action';" type="button" />
</div>
</s:form>

