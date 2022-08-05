<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<ui:pageHeadingByTitle/>

<s:form action="admin/CreativeCategory/save">
  <s:hidden name="version"/>
  <s:hidden name="type"/>
  <s:fielderror><s:param value="'version'"/></s:fielderror>
  <s:if test="not isTagCategory()">
        <s:property escapeHtml="false" value="%{creativeCategoryEditDescription}"/> 
  </s:if>
  <div class="wrapper">
    <table class="formFields">
        <tr><td>
            <s:if test="%{fieldErrors['categories']}">
                <div style="margin-top: 5px; margin-bottom: 5px">
                    <s:fielderror><s:param value="'categories'"/></s:fielderror>
                </div>
            </s:if>
            <s:if test="hasActionErrors()">
                <s:actionerror/>
            </s:if>
        </td></tr>
        <tr>
            <td>
                <s:textarea cssClass="bigLengthText" name="categoriesText" />
            </td>
        </tr>
    </table>
  </div>

  <script type="text/javascript">
    function saveConfirm(jqButt){
        if(confirm('${ad:formatMessage("CreativeCategory.save.confirm")}')){
            jqButt.closest('form').submit();
            return false;
        }else{
            return false;
        }
    }
  </script>

  <div class="wrapper">
    <ui:button message="form.save" onclick="return saveConfirm($(this));" type="button"/>
    <ui:button message="form.cancel" onclick="location='main.action';" type="button" />
  </div>
</s:form>
