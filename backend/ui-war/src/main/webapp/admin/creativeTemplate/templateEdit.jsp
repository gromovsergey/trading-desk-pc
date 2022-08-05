<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<s:form action="admin/CreativeTemplate/%{#attr.isCreatePage?'create':'update'}">
  <s:hidden name="id"/>
  <s:hidden name="version"/>
  
  <div class="wrapper">
      <s:fielderror><s:param value="'version'"/></s:fielderror>
      <s:actionerror/>
  </div>

<ui:section titleKey="form.main" >
    <ui:fieldGroup id="" cssClass="">
        
        <ui:field labelKey="defaultName" labelForId="name" required="true" errors="defaultName,name">
            <s:textfield name="defaultName" id="name" cssClass="middleLengthText" maxlength="100"/>
        </ui:field>
        
        <ui:field labelKey="CreativeTemplate.expandable.label" labelForId="expandable" errors="expandable">
            <s:radio list="#{true, false}" name="expandable" value="expandable" listValue="getText('CreativeTemplate.expandable.'+key)"></s:radio>
        </ui:field>
        
    </ui:fieldGroup>
</ui:section>

<ui:section titleKey="creative.categories.visual" errors="categories">
    <table class="grouping">
      <tr>
        <td width="10">
          <ui:optiontransfer name="selectedCategories" id="categories"
            list="${ad:localizeEntities(availableCategories)}" selList="${ad:localizeEntities(categories)}" saveSorting="true"
            cssClass="smallLengthText1" size="9"
            titleKey="CreativeTemplate.categories.available" selTitleKey="CreativeTemplate.categories.selected" escape="true" />
        </td>
      </tr>
    </table>
</ui:section>

  <s:include value="/templates/formFooter.jsp"/>
</s:form>
