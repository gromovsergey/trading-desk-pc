<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="dataView">
    <tr>
        <thead>
            <th><s:text name="UserRole.objectType"/></th>
            <th><s:text name="UserRole.actions"/></th>
        </thead>
    </tr>
    <s:iterator value="#currentPolicy.policy" var="objectTypeEntry">
        <s:if test="#objectTypeEntry.value.get(nullParameter) neq null">
            <tr>
                <td>
                    <c:set var="textVal">
                        <ad:resolveGlobal resource="permissionType" id="${objectTypeEntry.key}"/>
                    </c:set>
                    <ui:text subClass="level1" text="${pageScope.textVal}"/>
                </td>
                <td>
                    <s:iterator value="#objectTypeEntry.value.get(nullParameter)" var="action" status="actionStatus">
                        <ad:resolveGlobal resource="permissionAction" id="${action}"/><s:if test="not #actionStatus.last">, </s:if>
                    </s:iterator>
                </td>
            </tr>
        </s:if>
        <s:iterator value="#objectTypeEntry.value" var="parameterEntry">
            <s:if test="#parameterEntry.key neq nullParameter">
                <tr>
                    <td><ui:text text="${parameterEntry.key.text}" subClass="level2"/></td>
                    <td>
                        <s:iterator value="#parameterEntry.value"
                                    var="action" status="actionStatus"><ad:resolveGlobal resource="permissionAction" id="${action}"/><s:if test="not #actionStatus.last">, </s:if></s:iterator>
                    </td>
                </tr>
            </s:if>
        </s:iterator>
    </s:iterator>
</table>
