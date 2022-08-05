<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
        
        $('#password').keyup(function(){
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

<ui:pageHeadingByTitle/>
<s:if test="hasActionErrors()">
    <div style="margin-top: 5px; margin-bottom: 5px">
        <s:actionerror/>
    </div>
</s:if>
<s:else>
    <s:form action="forgotPassword/result">
        <script type="text/javascript">
            $('form').attr({autocomplete : 'off'});
        </script>
        <s:hidden name="userCredentialId"/>
        <s:hidden name="uid"/>
        
        <ui:section>
            <ui:fieldGroup>
                
                <ui:field labelKey="password.Assistance.field.password" labelForId="password" required="true" errors="password">
                    <s:password id="password" name="password" cssClass="middleLengthText" maxLength="100"/>
                    <span id="pstrength"></span>
                </ui:field>
                
                <ui:field labelKey="password.Assistance.field.repeatPassword" labelForId="repeatedPassword" required="true" errors="repeatedPassword">
                    <s:password id="repeatedPassword" name="repeatedPassword" cssClass="middleLengthText" maxLength="100"/>
                </ui:field>
                
                <ui:field cssClass="withButton">
                    <ui:button message="password.Assistance.field.changeButton" type="submit"/>
                </ui:field>
                
            </ui:fieldGroup>
        </ui:section>
        
    </s:form>
</s:else>
