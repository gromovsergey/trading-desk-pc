<%@ tag description="UI Field" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<%@ attribute name="optionGroup" required="true" type="com.foros.model.template.OptionGroup" %>
<%@ attribute name="label" required="false" %>
<%@ attribute name="groupInfo" required="false" type="java.lang.Boolean" %>


<c:set var="first" value="${true}"/>
<c:forEach var="option" items="${optionGroup.options}">
    <tr>
        <c:if test="${empty groupInfo || groupInfo}">
            <c:if test="${first}">
                <c:if test="${not empty label}">
                    <td style="vertical-align:middle" rowspan="${fn:length(optionGroup.options)}"><c:out value="${label}"/></td>
                </c:if>
                <td style="vertical-align:middle" rowspan="${fn:length(optionGroup.options)}">
                    <a class="preText" href="/admin/OptionGroup/view.action?id=${optionGroup.id}"><c:out value="${ad:localizeName(optionGroup.name)}"/></a>
                </td>
            </c:if>
        </c:if>

        <c:set var="first" value="${false}"/>
        <td>
            <div class="textWithHint white">
                <a href="/admin/Option/view.action?id=${option.id}"><c:out value="${ad:localizeName(option.name)}"/></a>
                <c:if test="${not empty option.defaultLabel}">
                    <ui:hint><c:out value="${ad:localizeName(option.label)}"/></ui:hint>
                </c:if>
            </div>
        </td>

        <td>
            ##<c:out value="${option.token}"/>##
        </td>
        <td>
            <c:if test="${option.defaultValue != null}">
                <c:set var="type" value="${option.type}"/>
                <c:if test="${type.name == 'String' || type.name == 'File' || type.name == 'URL' || type.name == 'URL Without Protocol' || type.name == 'File/URL' || type.name == 'Dynamic File' || type.name == 'Enum'}">
                    <ui:text text="${option.defaultValue}"/>
                </c:if>
                <c:if test="${type.name == 'Color'}">
                    <c:set var="textVal">
                        #${option.defaultValue}
                    </c:set>
                    <span class="colorInput">
                        <ui:text text="${pageScope.textVal}"/>
                        <input onfocus="this.blur();"
                               class="colorBox"
                               type="text"
                               readonly="readonly"
                               style="background-color:#${option.defaultValue};"
                               tabindex="-1"/>
                    </span>
                </c:if>
                <c:if test="${type.name == 'Integer'}">
                    <fmt:formatNumber value="${option.defaultValue}" groupingUsed="true"/>
                </c:if>
                <c:if test="${type.name == 'Text' || type.name == 'HTML'}">
                    <div style="white-space:pre;position:relative;"><c:out value="${option.defaultValue}"/></div>
                </c:if>
            </c:if>
        </td>
    </tr>
</c:forEach>
