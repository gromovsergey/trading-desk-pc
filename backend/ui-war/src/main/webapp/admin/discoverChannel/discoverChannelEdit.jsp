<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:set var="isLinked" value="${not empty model.channelList.id}"/>
<c:if test="${!isLinked}">
<script type="text/javascript">
    function loadBparams() {
        $("#bparamsSection").html($.localize('form.select.wait'));
        UI.Data.getUrl('/admin/behavioralParameters/get.action', "html", {id: $("#bparamsList").val()}, function(data) {
            $("#bparamsSection").html(data);
        });
    }
    $().ready(function() {
        loadBparams();
        $("#account").change(function() {
            UI.Data.get('accountCountry', {accountId: $(this).val()}, function(data) {
                $("#countryCode").val($("countryCode", data).text()).change();
            });
        });

        $("#bparamsList").change(function() {
            loadBparams();
        });
    });

</script>
</c:if>
<s:form action="save">
    <s:hidden name="id"/>
    <s:hidden name="version"/>
    <s:hidden name="standalone"/>
    <ui:pageHeadingByTitle/>

<ui:errorsBlock>
    <s:fielderror fieldName="version"/>
    <s:actionerror/>
</ui:errorsBlock>

<c:choose>
    <c:when test="${!isLinked}">
        <ui:section>
            <ui:fieldGroup>
                <c:choose>
                    <c:when test="${empty model.id}">
                        <ui:field labelKey="account.account" labelForId="account" required="true" errors="account.id">
                            <s:select list="channelOwners" listKey="id" listValue="name" value="account.id"
                                         name="accountId" id="account" cssClass="middleLengthText"/>
                        </ui:field>
                    </c:when>
                    <c:otherwise>
                        <s:hidden name="accountId"/>
                        <s:hidden name="accountName"/>
                        <ui:simpleField labelKey="channel.account" value="${accountName}"/>
                    </c:otherwise>
                </c:choose>

                <ui:field labelKey="channel.name" labelForId="name" required="true" errors="name,errors.duplicate">
                    <s:textfield name="name" id="name" cssClass="middleLengthText" maxlength="100"/>
                </ui:field>

                <ui:field id="countryElem" labelKey="channel.country" labelForId="countryCode" errors="country.countryCode">
                   <s:select name="country.countryCode" id="countryCode" cssClass="middleLengthText"
                       list="countries" value="country.countryCode"
                       listKey="id" listValue="getText('global.country.' + id + '.name')"/>
                </ui:field>

                <%@include file="/channel/channelLanguageField.jsp"%>

                <ui:field labelKey="channel.discoverQuery" labelForId="discoverQuery" required="true" errors="discoverQuery">
                    <s:textfield name="discoverQuery" id="discoverQuery" cssClass="middleLengthText" maxlength="4000"/>
                </ui:field>

                <ui:field labelKey="channel.discoverAnnotation" labelForId="discoverAnnotation" required="true" errors="discoverAnnotation">
                    <s:textfield name="discoverAnnotation" id="discoverAnnotation" cssClass="middleLengthText" maxlength="4000"/>
                </ui:field>

                <ui:field labelKey="channel.params" labelForId="bparamsList" required="false" errors="behavParamsList.id">
                   <s:select name="behavParamsList.id" id="bparamsList" cssClass="middleLengthText"
                       list="availableBehavioralParameters" 
                       listKey="id" listValue="name"
                       headerKey="" headerValue="%{getText('form.select.none')}"/>
                </ui:field>

                <ui:field labelKey="channel.description" labelForId="description" errors="description">
                    <s:textarea name="description" cssClass="middleLengthText" rows="3" cssStyle="height: 50px"/>
                </ui:field>
            </ui:fieldGroup>
        </ui:section>
<%@include file="/channel/triggersEdit.jsp"%>
        <div id="bparamsSection" class="logicalBlock">
            <ui:section titleKey="channel.params">
                <ui:behavioralParamsView isChannelBehaviouralParamList="true"/>
            </ui:section>
        </div>
    </c:when>
    <c:otherwise>
        <ui:section titleKey="channel.keywords" errors="keywords,name">
            <s:textarea name="baseKeyword" cssClass="middleLengthText1" cssStyle="height: 80px"/>
            <div style="width:450px">
                <fmt:message key="channel.linkedChannelKeywordsNote"/>
            </div>
        </ui:section>

        <div class="wrapper">
            <fmt:message key="channel.linkedChannelNote"/>
        </div>

        <ui:section titleKey="channel.reference">
            <ui:fieldGroup>
                <ui:simpleField labelKey="channel.channelNameMacro" value="${model.channelList.channelNameMacro}"/>
                <ui:simpleField labelKey="channel.keywordTriggerMacro" value="${model.channelList.keywordTriggerMacro}"/>
                <ui:simpleField labelKey="channel.discoverQueryMacro" value="${model.channelList.discoverQuery}"/>
                <ui:simpleField labelKey="channel.discoverAnnotationMacro" value="${model.channelList.discoverAnnotation}"/>
            </ui:fieldGroup>
        </ui:section>
    </c:otherwise>
</c:choose>

    <div class="wrapper">
    <ui:button message="form.save" type="submit"/>
      <c:choose>
        <c:when test="${empty model.id}">
              <ui:button message="form.cancel" action="main" type="button" />
        </c:when>
        <c:otherwise>
          <ui:button message="form.cancel" action="view?id=${model.id}" type="button"/>
        </c:otherwise>
      </c:choose>
    </div>

</s:form>
