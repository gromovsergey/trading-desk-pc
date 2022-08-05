<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<table class="dataView">
    <thead>
        <th><s:text name="UserRole.objectType"/></th>
        <s:iterator value="#currentPolicy.actions" var="action">
            <th><ad:resolveGlobal resource="permissionAction" id="${action}"/></th>
        </s:iterator>
    </thead>
    <s:iterator value="#currentPolicy.permissions" var="permission" status="permissionsRow">
        <s:set var="objectType" value="%{#permission.key}"/>
        <tr>
            <td>
                <c:set var="textVal">
                    <ad:resolveGlobal resource="permissionType" id="${objectType}"/>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </td>
            <s:iterator value="#currentPolicy.actions" var="action" status="actionsRow">
                <s:set var="descriptor" value="#permission.value[#action]"/>
                <td>
                    <s:if test="#descriptor">
                        <s:checkbox name="checks[%{#permissionCounter}].value"
                                    value="#currentPolicy.checked(#descriptor, null)"
                                    cssClass="objectType-%{#objectType}-%{#action} basePermission"
                                    onclick="handleCheckChange('%{#objectType}','%{#action}')"
                                    onkeydown="handleCheckChange('%{#objectType}','%{#action}')"/>
                        <s:hidden name="checks[%{#permissionCounter}].entry" value="%{#currentPolicy.id(#descriptor, null)}"/>
                        <s:hidden name="checks[%{#permissionCounter}].objectType" value="%{#objectType}"/>
                        <s:hidden name="checks[%{#permissionCounter}].action" value="%{#action}"/>
                        <s:set var="permissionCounter" value="#permissionCounter+1"/>
                    </s:if>
                </td>
            </s:iterator>
        </tr>
        <s:iterator value="#currentPolicy.getParameters(#objectType)" var="parameter">
            <tr>
                <td><ui:text text="${parameter.text}" subClass="level2"/></td>
                <s:iterator value="#currentPolicy.actions" var="action" status="actionsRow">
                    <s:set var="descriptor" value="#permission.value[#action]"/>
                    <td>
                        <s:if test="#descriptor.isParameterized()">
                            <s:checkbox name="checks[%{#permissionCounter}].value"
                                    value="#currentPolicy.checked(#descriptor, #parameter.name)" cssClass="parameter-%{#objectType}-%{#action}"/>
                            <s:hidden name="checks[%{#permissionCounter}].entry" value="%{#currentPolicy.id(#descriptor, #parameter.name)}"/>
                            <s:hidden name="checks[%{#permissionCounter}].objectType" value="%{#objectType}"/>
                            <s:hidden name="checks[%{#permissionCounter}].action" value="%{#action}"/>
                            <s:hidden name="checks[%{#permissionCounter}].parameter" value="%{#parameter.name}"/>
                            <s:set var="permissionCounter" value="#permissionCounter+1"/>
                        </s:if>
                    </td>
                </s:iterator>
            </tr>
        </s:iterator>
    </s:iterator>
</table>
