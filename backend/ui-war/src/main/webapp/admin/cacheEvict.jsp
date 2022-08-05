<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xhtml="true">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><ui:windowTitle attributeName="page.title.support.cacheEviction"/></title>
    <ui:stylesheet fileName="common.css" />
    
    <ui:externalLibrary libName="jquery"/>
    
    <ui:javascript fileName="jquery-custom.js"/>
<body>
<script>
    function clearPage() {
    <s:if test="hasActionMessages()">
        $('#collectionName').val('');
        $('#id').val('');

        var isFlagChecked = $('input[type=checkbox][name=allEntries]:checked').val();
        if (isFlagChecked == 'true') {
            $('#allEntries').prop({checked : false});
        }
        var selectedClassList = $('#className')[0];
        selectedClassList.options[0].selected = true;
    </s:if>
    }

     $().ready(function(){
        $("#allEntries").click(function() {
            $('#id').prop({disabled : $('#allEntries').is(":checked") ? true : false});
        });
        clearPage();
     });
</script>
<s:if test="hasActionMessages()">
    <div style="margin-top: 5px; margin-bottom: 5px">
        <s:iterator value="actionMessages">
            <span class="infos"><s:actionmessage/></span>
        </s:iterator>
    </div>
</s:if>
<s:form  action="admin/support/cache/evict" method="post">
    <ui:section>
        <ui:fieldGroup>
            <ui:field labelKey="id" errors="id" labelForId="id">
                <s:textfield name="id" id="id" cssClass="smallLengthText1"/>
            </ui:field>
            <ui:field labelKey="all.objects.of.class" labelForId="allEntries">
                <s:checkbox id="allEntries" name="allEntries" title="All"/>
            </ui:field>
            <ui:field labelKey="class" errors="class" labelForId="className">
                <s:select name="className" id="className" cssClass="smallLengthText1" list="entityClasses" listKey="name" listValue="simpleName"/>
            </ui:field>
            <ui:field labelKey="collection.field" errors="collection" labelForId="collectionName">
                <s:textfield name="collectionName" id="collectionName" cssClass="smallLengthText1"/>
            </ui:field>
            <ui:field>
                <ui:button message="evict" type="submit"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>
<s:form  action="admin/support/cache/clear" method="post">
    <ui:section>
        <ui:fieldGroup>
            <ui:field>
                <h4>Clear memcache</h4>
            </ui:field>
            <ui:field>
                <ui:button message="evict" type="submit"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>

<s:form action="admin/support/saiku/refreshDataSource" method="post">
    <ui:section>
        <ui:fieldGroup>
            <ui:field>
                <h4>Refresh Olap DataSource</h4>
            </ui:field>
            <ui:field>
                <ui:button message="refresh" type="submit"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>
</s:form>
</body>
</html>
