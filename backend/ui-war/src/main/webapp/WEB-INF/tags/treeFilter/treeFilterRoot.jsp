<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<s:if test="levelAvailable">
    <ui:collapsible id="treeFilterCollapsible${treeId}"
                 labelKey="${entityFilterMessageKey}"
                 onCollapse="rememberLastExpandedStatus${treeId}(false)"
                 onExpand="rememberLastExpandedStatus${treeId}(true)">
        <div class="treeFilterContainer<s:if test="showRoot"> withRoot</s:if>">
            <s:if test="showRoot">
                <label>
                    <input id="treeRootCheckbox${treeId}" type="checkbox" checked="checked" onclick="switchAll(this);"/>
                    <fmt:message key="treeFilter.rootLabel.${parameterName}"/>
                </label>
            </s:if>
            <ul class="treeClickable">
                <%@ include file="treeFilterElem.jsp" %>
            </ul>
        </div>
    </ui:collapsible>
</s:if>
