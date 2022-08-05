<%@ tag description="UI Field" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<%@ attribute name="label" required="false" %>
<%@ attribute name="collection" required="true" type="java.util.Collection" %>

<c:forEach var="optionGroup" items="${collection}">
    <c:if test="${empty optionGroup.options}">
        <tr>
            <c:if test="${not empty label}">
                <td>
                    <c:out value="${label}"/>
                </td>
            </c:if>
            <td>
                <a class="preText" href="/admin/OptionGroup/view.action?id=${optionGroup.id}"><c:out value="${ad:localizeName(optionGroup.name)}"/></a>
            </td>
            <td>
            </td>
            <td>
            </td>
            <td>
            </td>
        </tr>
    </c:if>

    <ui:optionsView label="${label}" optionGroup="${optionGroup}" groupInfo="${true}"/>

</c:forEach>
