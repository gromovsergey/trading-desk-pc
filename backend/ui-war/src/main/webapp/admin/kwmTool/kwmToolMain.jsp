<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<ui:pageHeadingByTitle/>

<s:if test="hasFieldErrors()">
    <s:fielderror/>
</s:if>
<s:elseif test="errorMessage != null">
    <span class="errors">
        <s:if test="toolExitCode != 0">
            <fmt:message key="KWMTool.errorMessage">
                <fmt:param><s:property value="toolExitCode"/></fmt:param>
                <fmt:param><s:property value="errorMessage"/></fmt:param>
            </fmt:message>
        </s:if>
        <s:else>
            <s:property value="errorMessage"/>
        </s:else>
    </span>
</s:elseif>
<s:else>
<script type="text/javascript">
    var targetElementId = null;

    function browseFile(id) {
        targetElementId = id;
        window.open('${_context}/fileman/fileManager.action?id=' + targetElementId + '&mode=kwmtool','filebrowser');
    }

    var currentSettingsSchema;
    
    function adserverDebugChangeHandler(){
        if (!this.checked) {
            $('#loopsField').prop({disabled : false});
        } else {
            $('#loopsField').prop({disabled : true})
        }
    }

    $(function(){
        $(document).off('submit.preventDoubleSubmit');
        currentSettingsSchema = $('#settingsSchema').val();

        $('#adserverDebug').change(adserverDebugChangeHandler)
            .click(adserverDebugChangeHandler);

        $('#adserverDebug').change();
        $('[name=source],[name=text]').click(switchFields);
        $('#kwmToolForm_sourceUrl').focus();
        $('#kwmToolForm_text').prop({disabled : true});
    });
    
    function KWMSubmit(jqForm){
        // Safari post request cache problem solution
        jqForm.attr({action : jqForm.attr('action')});
    }
    
    function switchFields() {
        if ($('[name=source]:checked').val() > 0) {
            $('#kwmToolForm_sourceUrl, #maxSizeId').prop({disabled : true});
            $('#kwmToolForm_text').prop({disabled : false}).focus();
        } else {
            $('#kwmToolForm_text').prop({disabled : true});
            $('#kwmToolForm_sourceUrl, #maxSizeId').prop({disabled : false});
            $('#kwmToolForm_sourceUrl').focus();
        }
    }
</script>

<s:form action="admin/KWMTool/results" id="kwmToolForm" target="_blank">
<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="KWMTool.source" required="true">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">
                        <label class="withInput">
                            <s:radio name="source" list="0" template="justradio"
                            /><fmt:message key="KWMTool.source.url"/>
                        </label>
                    </td>
                    <td class="withField">
                        <s:textfield name="sourceUrl" cssClass="middleLengthText"/>
                    </td>
                    <td class="withError">
                        <s:fielderror><s:param>sourceUrl</s:param></s:fielderror>
                    </td>
                </tr>
            </table>
        </ui:field>
        
        <ui:field>
            <ui:fieldGroup>
                <ui:field labelKey="KWMTool.source.maxSize" labelForId="maxSizeId" errors="maxSize" tipKey="KWMTool.hint.settingsSchema">
                    <table class="fieldAndAccessories">
                        <tr>
                            <td class="withField">
                                <s:textfield name="maxSize" id="maxSizeId"/>
                            </td>
                            <td class="withField">
                                <ui:text textKey="KWMTool.source.bytes"/>
                            </td>
                        </tr>
                    </table>
                </ui:field>
            </ui:fieldGroup>
        </ui:field>
        
        <ui:field>
            <label for="kwmToolForm_source1" class="withInput">
                <s:radio name="source" list="1" template="justradio"
                /><fmt:message key="KWMTool.source.text"/>
            </label>
            <label for="kwmToolForm_source2" class="withInput">
                <s:radio name="source" list="2" template="justradio"
                /><fmt:message key="KWMTool.source.html"/>
            </label>
        </ui:field>
        
        <ui:field>
            <s:textarea name="text" cssClass="bigLengthText"/>
        </ui:field>
        <ui:field labelKey="KWMTool.settingsSchema" labelForId="settingsSchema" errors="settingsSchema" required="true">
            <table class="fieldAndAccessories">
                <tr>
                    <td class="withField">
                        <s:textfield name="settingsSchema" id="settingsSchema" cssClass="middleLengthText"/>
                    </td>
                    <td class="withButton">
                        <ui:button message="form.browse" onclick="browseFile('settingsSchema');"/>
                    </td>
                </tr>
            </table>
        </ui:field>
        <ui:field labelKey="KWMTool.adserverDebug" errors="adserverDebug">
            <s:checkbox name="adserverDebug" id="adserverDebug"/>
        </ui:field>
        <ui:field labelKey="KWMTool.loopsLabel" labelForId="loopsField" errors="loops" required="true">
            <s:textfield name="loops" id="loopsField"/>
        </ui:field>
    </ui:fieldGroup>
</ui:section>

<div class="wrapper">
    <ui:button message="form.submit" onclick="KWMSubmit($('#kwmToolForm'));" type="submit" />
</div>

</s:form>
</s:else>
