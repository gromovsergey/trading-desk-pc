<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<s:set var="viewPublisherUrl" value="'/admin/publisher/account/view.action'" />
<s:set var="viewAgencyUrl" value="'/admin/advertiser/account/advertiserView.action'"/>

<display:table name="entities" class="dataView" id="walledGarden">
    <display:setProperty name="basic.msg.empty_list" >
          <div class="wrapper">
              <fmt:message key="nothing.found.to.display"/>
          </div>
      </display:setProperty>
    <display:column titleKey="WalledGarden.current.publisher">
        <ui:displayStatus displayStatus="${walledGarden.publisher.displayStatus}" testFlag="${walledGarden.publisher.testFlag}">
            <a href="${viewPublisherUrl}?id=${walledGarden.publisher.id}"><c:out value="${walledGarden.publisher.name}"/></a>
        </ui:displayStatus>
    </display:column>
    <display:column titleKey="WalledGarden.current.agency">
        <ui:displayStatus displayStatus="${walledGarden.agency.displayStatus}" testFlag="${walledGarden.agency.testFlag}">
            <a href="${viewAgencyUrl}?id=${walledGarden.agency.id}"><c:out value="${walledGarden.agency.name}"/></a>
        </ui:displayStatus>
    </display:column>
    <s:if test="%{countryCode == null || countryCode == ''}">
        <display:column titleKey="WalledGarden.current.country">
            <ad:resolveGlobal resource="country" id="${walledGarden.publisher.country.countryCode}"/>
        </display:column>
    </s:if>
    <c:if test="${ad:isPermitted0('WalledGarden.update')}">
        <display:column titleKey="WalledGarden.current.actions">
            <s:set var="editUrl">
                <s:url action="edit" namespace="/admin/WalledGarden">
                    <s:param name="id">${walledGarden.id}</s:param>
                </s:url>
            </s:set>
            <ui:button message="form.edit" href="${editUrl}" type="link" />
            <ui:button message="form.viewLog" href="/admin/auditLog/view.action?type=52&id=${walledGarden.id}" />
            <div class="fixing" style="min-width:100px;"></div>
        </display:column>
    </c:if>
</display:table>
