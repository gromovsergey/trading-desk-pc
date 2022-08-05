<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">

    $().ready(function(){
        $('#permissionTable').css({borderBottom:'none'});

        $('.delRow').click(function(){
            var mainTable = $('#permissionTable');

            UI.Util.Table.deleteRowByButtonInIt(this, mainTable[0])
            mainTable.is(':has(tbody tr:visible)') || mainTable.hide();
            return false;
        });

        $('#addRowBtn').click(function(){
            UI.Util.Table.addRow('permissionTable');
            $('#permissionTable').show();
            $('#permissionTable > tbody > tr:last select').prop({disabled : false});
            return false;
        });
    });

    function doSave() {
        $('#CustomizableReportForm')[0].action = $('#CustomizableReportForm')[0].action + '?id=' + $('[name=id]').val() + '&PWSToken=' + $('[name=PWSToken]').val();
        $('#CustomizableReportForm').submit();
    }

</script>

<s:form id="CustomizableReportForm" action="admin/report/birtReport/%{#attr.isCreatePage?'create':'update'}" enctype="multipart/form-data" method="post">
  <input type="hidden" name="id" value="${model.id}"/>
  <input type="hidden" name="version.fullTime" value="${model.version.fullTime}"/>

<ui:pageHeadingByTitle/>

    <ui:errorsBlock>
        <s:fielderror><s:param>version</s:param></s:fielderror>
        <s:actionerror/>
    </ui:errorsBlock>

    <c:set var="isSupportedReport" value="${id < 1000000}"/>
    <c:if test="${isSupportedReport}">
        <span class="infos" id="infoAddParameter"><fmt:message key="report.supported.info"/></span>
    </c:if>

    <ui:section>
        <ui:fieldGroup>
            <c:choose>
                <c:when test="${isSupportedReport}">
                    <ui:field labelKey="birtReports.name" labelForId="name" errors="name">
                        <ui:text text="${model.name}"/>
                    </ui:field>
                </c:when>
                <c:otherwise>
                    <ui:field labelKey="birtReports.name" labelForId="name" required="true" errors="name">
                        <s:textfield name="name" cssClass="middleLengthText" maxlength="100"/>
                    </ui:field>
                </c:otherwise>
            </c:choose>

            <ui:field labelKey="birtReports.file">
                <c:if test="${not empty templateFile}">
                    <ui:text textKey="birtReports.templateLoaded"/>
                    <ui:button message="birtReports.download" href="downloadTemplate.action?id=${id}"/>
                </c:if>
                <c:if test="${empty templateFile}">
                    <ui:text textKey="birtReports.noTemplate"/>
                </c:if>
            </ui:field>

            <c:set var="textForTip">
                <fmt:message key="birtReports.rptdesign.hint">
                    <fmt:param value="10"/>
                </fmt:message>
            </c:set>
            <ui:field labelKey="birtReports.file.new" required="${(empty templateFile) ? 'true' : ''}" tipText="${textForTip}" errors="uploadedFile" >
                <input type="hidden" id="templateFile" name="templateFile" value="${model.templateFile}"/>
                <s:file name="uploadedFile" id="uploadedFile" cssClass="middleLengthText"/>
            </ui:field>
        </ui:fieldGroup>
    </ui:section>

    <ui:header styleClass="level2">
        <h2><fmt:message key="birtReports.permissions"/></h2>
        <ui:button id="addRowBtn" message="form.add" type="button"/>
    </ui:header>

    <table id="permissionTable" class="dataView" <c:if test="${empty permissions}">style="display:none"</c:if>>
        <thead>
          <tr>
            <th style="width:300px;"><fmt:message key="birtReports.table.role"/></th>
            <th style="width:50px;"><fmt:message key="birtReports.table.run"/></th>
            <th style="width:50px;"><fmt:message key="birtReports.table.edit"/></th>
            <th class="withButton"></th>
          </tr>
        </thead>
        <tbody>
          <tr style="display:none;">
            <td nowrap="true" class="field">
                <table class="mandatoryContainer">
                    <tr>
                        <td>
                            <s:select name="permissions[?].userRolePair"
                                 list="userRolePairs"
                                 listKey="id"
                                 listValue="name"/>
                        </td>
                    </tr>
                </table>
            </td>
            <td>
                <s:checkbox name="permissions[?].run" id="permissions[?].run" value="false" disabled="true"/>
            </td>
            <td>
                <s:checkbox name="permissions[?].edit" id="permissions[?].edit" value="false" disabled="true"/>
            </td>
            <td class="withButton">
              <ui:button messageText="-" subClass="delRow" type="button" />
            </td>
          </tr>
             <s:iterator value="permissions" status="status" var="permission">
                <s:set var="indexID" value="%{#status.count-1}"/>
                <s:hidden name="userRoles[%{indexID}]" value="%{userRolePair}" />
                <tr>
                  <td nowrap="true">
                      <s:hidden name="permissions[%{indexID}].runPolicyId" value="%{runPolicyId}"/>
                      <s:hidden name="permissions[%{indexID}].editPolicyId" value="%{editPolicyId}"/>
                      <s:hidden name="permissions[%{indexID}].runPolicyVersion.fullTime" value="%{runPolicyVersion.fullTime}"/>
                      <s:hidden name="permissions[%{indexID}].editPolicyVersion.fullTime" value="%{editPolicyVersion.fullTime}"/>
                      <s:hidden name="permissions[%{indexID}].runGlobal" value="%{runGlobal}"/>
                      <s:hidden name="permissions[%{indexID}].editGlobal" value="%{editGlobal}"/>
                      <s:if test="%{runGlobal || editGlobal}">
                          <s:hidden name="permissions[%{indexID}].userRolePair" value="%{userRolePair}" />
                          <s:hidden name="permissions[%{indexID}].userRoleName" value="%{userRoleName}"/>
                          <c:set var="textVal"><s:property value="userRoleName"/></c:set>
                          <ui:text text="${pageScope.textVal}"/>
                      </s:if>
                      <s:else>
                        <table class="mandatoryContainer">
                            <tr>
                                <td>
                                    <s:select name="permissions[%{indexID}].userRolePair"
                                          list="userRolePairs"
                                          listKey="id"
                                          listValue="name"
                                          value="%{userRolePair}"/>
                                </td>
                            </tr>
                        </table>
                      </s:else>
                  </td>
                  <td>
                      <s:checkbox id="permissions[%{indexID}].run" name="permissions[%{indexID}].run" value="%{run}" disabled="%{runGlobal}"/>
                      <s:if test="%{runGlobal}">
                          <s:hidden name="permissions[%{indexID}].run"/>
                      </s:if>
                  </td>
                  <td>
                      <s:checkbox id="permissions[%{indexID}].edit" name="permissions[%{indexID}].edit" value="%{edit}" disabled="%{editGlobal}"/>
                      <s:if test="%{editGlobal}">
                          <s:hidden name="permissions[%{indexID}].edit"/>
                      </s:if>
                  </td>
                  <td class="withButton">
                    <table class="fieldAndAccessories">
                        <tr>
                            <td class="withButton">
                                <s:if test="%{!runGlobal && !editGlobal}">
                                    <ui:button messageText="-" subClass="delRow" type="button" />
                                </s:if>
                            </td>
                            <td class="withError">
                                <s:fielderror><s:param>permissions[${indexID}].role.id</s:param></s:fielderror>
                            </td>
                        </tr>
                    </table>
                  </td>
                </tr>
             </s:iterator>
        </tbody>
    </table>

  <div class="wrapper">
    <ui:button message="form.save" onclick="doSave();" type="button" />
    <ui:button message="form.cancel" onclick="location='../main.action';" type="button" />
  </div>
</s:form>
