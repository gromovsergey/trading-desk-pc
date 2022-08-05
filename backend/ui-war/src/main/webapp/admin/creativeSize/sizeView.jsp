<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="canUpdate" value="${ad:isPermitted('CreativeSize.update', model)}"/>

<ui:section titleKey="form.main" >
    <ui:fieldGroup>
        <c:if test="${canUpdate}">
            <ui:localizedField id="name" labelKey="defaultName" value="${defaultName}"
                resourceKey="CreativeSize.${id}"
                resourceUrl="/admin/resource/htmlName/"
                entityName="CreativeSize"/>
        </c:if>
        <c:if test="${not canUpdate}">
            <ui:simpleField labelKey="defaultName" value="${defaultName}"/>
        </c:if>

        <ui:field labelKey="CreativeSize.type">
            <a href="/admin/SizeType/view.action?id=${sizeType.id}"><c:out value="${ad:localizeName(sizeType.name)}"/></a>
        </ui:field>

        <ui:field labelKey="CreativeSize.protocolName">
            <ui:text text="${protocolName}"/>
        </ui:field>

        <s:if test="width != null">
            <ui:field labelKey="CreativeSize.width">
                <c:set var="textVal">
                    <fmt:formatNumber value="${width}" groupingUsed="true"/>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </s:if>

        <s:if test="height != null">
            <ui:field labelKey="CreativeSize.height">
                <c:set var="textVal">
                    <fmt:formatNumber value="${height}" groupingUsed="true"/>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </s:if>

        <s:if test="maxHeight != height && maxHeight != null">
            <ui:field labelKey="CreativeSize.maxHeight">
                <c:set var="textVal">
                    <fmt:formatNumber value="${maxHeight}" groupingUsed="true"/>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </s:if>
        
        <s:if test="maxWidth != width && maxWidth != null">
            <ui:field labelKey="CreativeSize.maxWidth">
                <c:set var="textVal">
                    <fmt:formatNumber value="${maxWidth}" groupingUsed="true"/>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </s:if>
        
        <s:if test="expansions.size != 0">
            <jsp:useBean id="expansionsComparator" class="org.springframework.util.comparator.ComparableComparator" scope="page"/>
            <ui:field labelKey="CreativeSize.expansions.label">
            <s:sort comparator="#attr.expansionsComparator" source="expansions">
                <s:iterator var="expansion">
                    <ui:text textKey="enums.CreativeSizeExpansion.${expansion}"/><br/>
                </s:iterator>
            </s:sort>
            </ui:field>
        </s:if>

        <ui:field labelKey="CreativeSize.status">
            <c:set var="statusDescriptionKey">enums.Status.${status}</c:set>
            <ui:statusButtonGroup
                descriptionKey="${statusDescriptionKey}"
                restrictionEntity="CreativeSize" entity="${model}"
                deletePage="delete.action"
                undeletePage="undelete.action"/>
        </ui:field>

    </ui:fieldGroup>

</ui:section>

<ui:header styleClass="level2">
  <h2><s:text name="CreativeSize.options"/></h2>
  <c:if test="${ad:isPermitted('CreativeSize.updateOptions', model)}">
    <ui:button message="OptionGroup.create" href="/admin/OptionGroup/new.action?creativeSizeId=${id}" />
    <c:if test="${not empty optionGroups}">
      <ui:button message="Option.create" href="/admin/Option/new.action?creativeSizeId=${id}" />
    </c:if>
  </c:if>
</ui:header>
<c:choose>
  <c:when test="${not empty optionGroups}">
    <table class="dataView">
        <thead>
            <tr>
                <th><fmt:message key="CreativeSize.options.group.type"/></th>
                <th><fmt:message key="CreativeSize.options.group.name"/></th>
                <th><fmt:message key="CreativeSize.options.name"/></th>
                <th><fmt:message key="CreativeSize.options.group.token"/></th>
                <th><fmt:message key="CreativeSize.options.group.defaultValue"/></th>
            </tr>
        </thead>
        <tbody>
            <c:set var="advertiserLabel"><fmt:message key="CreativeSize.options.group.type.Advertiser"/></c:set>
            <ui:allOptionsView label="${advertiserLabel}" collection="${advertiserOptionGroups}"/>

            <c:set var="publisherLabel"><fmt:message key="CreativeSize.options.group.type.Publisher"/></c:set>
            <ui:allOptionsView label="${publisherLabel}" collection="${publisherOptionGroups}"/>

            <c:set var="hiddenLabel"><fmt:message key="CreativeSize.options.group.type.Hidden"/></c:set>
            <ui:allOptionsView label="${hiddenLabel}" collection="${hiddenOptionGroups}"/>
        </tbody>
    </table>
  </c:when>
  <c:otherwise>
      <div class="wrapper">
          <fmt:message key="nothing.found.to.display"/>
      </div>
  </c:otherwise>
</c:choose>
