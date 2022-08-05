<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net/el"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ad" uri="/ad/serverUI"%>

<ui:header>
    <ui:pageHeadingByTitle />
</ui:header>

<table class="dataViewSection">
        <tr class="bodyZone">
            <td>
                <display:table name="childrenChannels" class="dataView" id="channel">
                    <display:setProperty name="basic.msg.empty_list" >
                      <div class="wrapper">
                          <fmt:message key="nothing.found.to.display"/>
                      </div>
                    </display:setProperty>
                    <display:column titleKey="DeviceChannel.entityName">
                        <ui:displayStatus cssClass="indentLevel_${channel.level - 1}" displayStatus="${channel.displayStatus}">
                            <a href="/admin/DeviceChannel/view.action?id=${channel.id}"><c:out value="${channel.name}"/></a>
                        </ui:displayStatus>
                    </display:column>
                </display:table>
            </td>
        </tr>
</table>

