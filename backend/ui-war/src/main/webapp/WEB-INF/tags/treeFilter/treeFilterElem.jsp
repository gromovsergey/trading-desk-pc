<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>

<s:if test="options != null && !options.empty">
    <c:set var="childAction" value="${param.childAction}"/>
    <s:iterator value="options" var="option">
        <s:set var="isSelected" value="#option.id == selectedId"/>
        <s:if test="#option.hasChildren">
            <li class="${isSelected ? "treeOpen" : "treeClosed"}">
                <div class="expand" onclick="clickButton${treeId}(this, '${option.id}', '${childAction}');"></div>
        </s:if>
        <s:else>
            <li class="treeBullet">
        </s:else>
             <label class="withInput">
                <table class="grouping">
                    <tr>
                        <s:if test="levelAvailable">
                            <td class="withInput">
                               <input type="checkbox" name="${parameterName}" value="${option.id}" onclick="switchAll(this);"/>
                            </td>
                        </s:if>
                        <td>
                            <ui:displayStatus displayStatus="${option.displayStatus}">
                                <ui:text text="${option.name}"/>
                            </ui:displayStatus>
                       </td>
                   </tr>
               </table>
            </label>
            <s:if test="#option.hasChildren">
                <ul id="data_${childAction}${option.id}" class="treeClickable">
                    <s:if test="#isSelected">
                        <s:action name="%{#attr.childAction}" executeResult="true">
                            <s:param name="root" value="false"/>
                            <s:param name="ownerId" value="selectedId"/>
                        </s:action>
                    </s:if>
                </ul>
            </s:if>
        </li>
    </s:iterator>
</s:if>
<s:else>
    <li/>
</s:else>