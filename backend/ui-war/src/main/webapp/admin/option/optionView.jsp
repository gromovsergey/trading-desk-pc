<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<ui:section>
    <ui:fieldGroup>
        <ui:field labelKey="OptionGroup.entityName">
            <a href="/admin/OptionGroup/view.action?id=${optionGroup.id}">${optionGroup.defaultName}</a>
        </ui:field>

		<c:if test="${ad:isPermitted('Option.update', model)}">
			<ui:localizedField id="name" labelKey="defaultName" value="${defaultName}"
				resourceKey="Option-name.${id}"
				resourceUrl="/admin/resource/htmlName/"
				entityName="Option"/>
		</c:if>
		<c:if test="${not ad:isPermitted('Option.update', model)}">
			<ui:simpleField labelKey="defaultName" value="${defaultName}"/>
	    </c:if>

        <s:if test="type != null">
            <ui:field labelKey="Option.type">
                <s:set var="textVal" value="getText('enums.OptionType.'+type)"/>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
            <s:if test="type.name == 'Integer'">
                <s:if test="minValue != null">
                    <ui:field labelKey="Option.minValue">
                        <c:set var="textVal">
                            <fmt:formatNumber value="${minValue}" groupingUsed="true"/>
                        </c:set>
                        <ui:text text="${pageScope.textVal}"/>
                    </ui:field>
                </s:if>
                <s:if test="maxValue != null">
                    <ui:field labelKey="Option.maxValue">
                        <c:set var="textVal">
                            <fmt:formatNumber value="${maxValue}" groupingUsed="true"/>
                        </c:set>
                        <ui:text text="${pageScope.textVal}"/>
                    </ui:field>
                </s:if>
            </s:if>
            <s:if test="type.name == 'String' || type.name == 'Text' || type.name == 'URL' || type.name == 'URL Without Protocol' || type.name == 'File/URL'">
                <s:if test="maxLength != null">
                    <ui:field labelKey="Option.maxLength">
                        <c:set var="textVal">
                            <fmt:formatNumber value="${maxLength}" groupingUsed="true"/>
                        </c:set>
                        <ui:text text="${pageScope.textVal}"/>
                    </ui:field>
                </s:if>
                <s:if test="maxLengthFullWidth != null">
                    <ui:field labelKey="Option.maxLengthFullWidth">
                        <c:set var="textVal">
                            <fmt:formatNumber value="${maxLengthFullWidth}" groupingUsed="true"/>
                        </c:set>
                        <ui:text text="${pageScope.textVal}"/>
                    </ui:field>
                </s:if>
            </s:if>
        </s:if>
        
        <ui:field labelKey="Option.required">
            <c:set var="textVal">
                <s:if test="required"><s:text name="yes"/></s:if>
                <s:else><s:text name="no"/></s:else>
            </c:set>
            <ui:text text="${pageScope.textVal}"/>
        </ui:field>

        <fmt:message key="${internalUse ? 'yes' : 'no'}" var="internalUseMessage"/>
        <ui:simpleField labelKey="Option.internalUse" value="${internalUseMessage}"/>


        <s:if test="token != null">
            <ui:simpleField labelKey="Option.token" value="${token}"/>
        </s:if>

        <s:if test="(type.name == 'File' || type.name == 'File/URL' || type.name == 'Dynamic File') && selFileTypes != null">
            <ui:field labelKey="Option.fileTypes">
                <c:set var="textVal">
                    <ad:commaWriter label="name" var="fileType" items="${selFileTypes}"/>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </s:if>


        <s:if test="defaultValue != null && type.name != 'Enum'">
            <ui:field labelKey="Option.defValue">
                <s:if test="type.name == 'String' || type.name == 'File' || type.name == 'URL' || type.name == 'URL Without Protocol' || type.name == 'File/URL' || type.name == 'Dynamic File'">
                    <ui:text text="${defaultValue}"/>
                </s:if>
                <s:elseif test="type.name == 'Color'">
                    <c:set var="textVal">
                        #${defaultValue}
                    </c:set>
                    <span class="colorInput">
                        <ui:text text="${pageScope.textVal}"/>
                        <input onfocus="this.blur();"
                               class="colorBox"
                               type="text"
                               readonly="readonly"
                               style="background-color:#${defaultValue};"
                               tabindex="-1"/>
                    </span>
                </s:elseif>
                <s:elseif test="type.name == 'Integer'">
                    <c:set var="textVal">
                        <fmt:formatNumber value="${defaultValue}" groupingUsed="true"/>
                    </c:set>
                    <ui:text text="${pageScope.textVal}"/>
                </s:elseif>
                <s:elseif test="type.name == 'Text' || type.name == 'HTML'">
                    <div style="white-space:pre;position:relative;"><c:out value="${defaultValue}"/></div>
                </s:elseif>
            </ui:field>
        </s:if>
        <s:elseif test="type.name == 'Enum'">
            <ui:field labelKey="Option.values">
                <c:set var="textVal">
                    <ad:commaWriter var="value" items="${displayValues}" escape="false"><c:out value="${value.name}" escapeXml="false"/><c:if test="${value.default}"> (<fmt:message key="Option.values.default"/>)</c:if></ad:commaWriter>
                </c:set>
                <ui:text text="${pageScope.textVal}"/>
            </ui:field>
        </s:elseif>
        <s:if test="!defaultLabel.empty">
			<c:if test="${ad:isPermitted('Option.update', model)}">
				<ui:localizedField isArea="true" id="label" labelKey="Option.defaultTooltip" value="${defaultLabel}"
                               resourceKey="Option-label.${id}"
                               resourceUrl="/admin/resource/Option/"
                               entityName="Option"
                               escapeXml="false"/>
			</c:if>
			<c:if test="${not ad:isPermitted('Option.update', model)}">
				<ui:simpleField labelKey="Option.defaultTooltip" value="${defaultLabel}" escapeXml="false"/>
		    </c:if>
        </s:if>

        <s:if test="recursiveTokensStr != ''">
            <ui:simpleField labelKey="Option.recursiveTokens" value="${recursiveTokensStr}"/>
        </s:if>

    </ui:fieldGroup>
</ui:section>
