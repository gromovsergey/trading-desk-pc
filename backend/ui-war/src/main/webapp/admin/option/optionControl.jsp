<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set value="${option.id}" var="valueKey"/>

<c:set var="fileTypesStr" scope="page">
    <ad:commaWriter var="fileType" items="${option.fileTypes}" separator=",">${fileType.fileType}</ad:commaWriter>
</c:set>
<c:set value="${optionValues[valueKey].file}" var="isFile"/>
<s:if test="(#option.type.name == 'File/URL' || #option.type.name == 'File' || #option.type.name == 'Dynamic File') && #attr.isFile && (entityType =='Tag' || entityType =='Creative' || entityType =='TextAd' || entityType =='WDTag')">
    <c:set value="${empty optionValues[valueKey] ? option.defaultValue : optionValues[valueKey].fileStripped}" var="valueStr"/>
</s:if>
<s:else>
    <c:set value="${empty optionValues[valueKey] ? option.defaultValue : optionValues[valueKey].value}" var="valueStr"/>
</s:else>
<c:set value="'saveForm_optionValues_\\\\'${valueKey}\\\\'__value'" var="controlIdStr"/>
<c:set value="saveForm_optionValues_'${valueKey}'__value" var="controlId"/>
<s:set var="isNew"  value="%{model.id == null}" scope="page"/>

<c:set var="labelText" value="${ad:localizeName(option.name)}"/>
<ui:field label="${labelText}" labelForId="${controlId}" tipText="${ad:localizeNameForTip(option.label)}" required="${option.required && option.type.name != 'Enum'}" escapeXml="false">
    <s:if test="#option.token == 'MAX_ADS_PER_TAG'">
        <script type="text/javascript">
            maxAdsId =  ${option.id};
        </script>
    </s:if>
    <s:if test="#option.token == 'AD_FOOTER_ENABLED'">
        <script type="text/javascript">
            adFooterId =  ${option.id};
        </script>
    </s:if>

    <c:choose>
        <c:when test="${entityType =='Creative' && !isNew}">
            <s:hidden name="optionValues['%{#attr.valueKey}'].id.creativeId" value="%{model.id}"/>
        </c:when>
        <c:when test="${entityType =='Tag' && !isNew}">
            <s:hidden name="optionValues['%{#attr.valueKey}'].id.tagId" value="%{model.id}"/>
        </c:when>
        <c:when test="${entityType =='WDTag' && !isNew}">
          <s:hidden name="optionValues['%{#attr.valueKey}'].id.wdTagId" value="%{model.id}"/>
        </c:when>
        <c:when test="${entityType =='TextAd' && !isNew}">
            <s:hidden name="optionValues['%{#attr.valueKey}'].id.creativeId" value="%{model.creative.id}"/>
        </c:when>
    </c:choose>

    <s:hidden name="optionValues['%{#attr.valueKey}'].option.id" value="%{#option.id}"/>
    <s:hidden name="optionValues['%{#attr.valueKey}'].id.optionId" value="%{#option.id}"/>
    <s:hidden name="optionValues['%{#attr.valueKey}'].version"/>
    <s:hidden name="optionValues['%{#attr.valueKey}'].option.type" value="%{#option.type}" />
    
    <table class="fieldAndAccessories">
        <tr>
            <s:if test="#option.type.name == 'String' || #option.type.name == 'URL' || #option.type.name == 'URL Without Protocol'">
                <td class="withField">
                    <s:textfield data-name="${option.token}" name="optionValues['%{#attr.valueKey}'].value" value="%{#attr.valueStr}" maxlength="2000" cssClass="middleLengthText"/>
                </td>
                <td class="withError">
                    <s:fielderror><s:param value="'optionValues[' + #attr.valueKey + '].value'"/></s:fielderror>
                    <s:fielderror><s:param value="'urls[' + #attr.valueKey + ']'"/></s:fielderror>
                </td>
            </s:if>
            <s:if test="#option.type.name == 'Integer'">
                <s:if test="%{fieldErrors['optionValues[' + #attr.valueKey + '].value'] == null}">
                    <fmt:parseNumber var="parsedNumberValue" type="number" value="${valueStr}" />
                    <fmt:formatNumber var="valueStr" value="${parsedNumberValue}" groupingUsed="true"/>
                </s:if>
                <td class="withField">
                    <s:textfield data-name="${option.token}" name="optionValues['%{#attr.valueKey}'].value" value="%{#attr.valueStr}"
                        maxlength="14" cssClass="smallLengthText"/>
                </td>
                <td class="withError">
                    <s:fielderror><s:param value="'optionValues[' + #attr.valueKey + '].value'"/></s:fielderror>
                    <s:fielderror><s:param value="'urls[' + #attr.valueKey + ']'"/></s:fielderror>
                </td>
            </s:if>
            <s:elseif test="#option.type.name == 'Text'">
                <td class="withField">
                    <s:textarea data-name="${option.token}" name="optionValues['%{#attr.valueKey}'].value" value="%{#attr.valueStr}" cssClass="middleLengthText" rows="10"/>
                </td>
                <td class="withError">
                    <s:fielderror><s:param value="'optionValues[' + #attr.valueKey + '].value'"/></s:fielderror>
                </td>
            </s:elseif>
            <s:elseif test="#option.type.name == 'HTML'">
                <td class="withField">
                    <s:textarea data-name="${option.token}" name="optionValues['%{#attr.valueKey}'].value" value="%{#attr.valueStr}" cssClass="middleLengthText html_highlight" rows="10"/>
                </td>
                <td class="withError">
                    <s:fielderror><s:param value="'optionValues[' + #attr.valueKey + '].value'"/></s:fielderror>
                </td>
            </s:elseif>
            <s:elseif test="#option.type.name == 'File' || #option.type.name == 'Dynamic File'">
                <td class="withField">
                    <s:textfield id="%{#attr.controlId}" data-name="${option.token}" name="optionValues['%{#attr.valueKey}'].value" value="%{#attr.valueStr}" maxlength="2000" cssClass="middleLengthText"/>
                </td>
                <td class="withButton">
                    <ui:button message="form.browse" onclick="openFileBrowser(${controlIdStr}, '${fileTypesStr}');"/>
                </td>
                <td class="withError">
                    <s:fielderror><s:param value="'optionValues[' + #attr.valueKey + '].value'"/></s:fielderror>
                    <s:fielderror><s:param value="'files[' + #attr.valueKey + ']'"/></s:fielderror>
                </td>
            </s:elseif>
            <s:elseif test="#option.type.name == 'File/URL'">
                <td class="withField">
                    <s:textfield data-name="${option.token}" id="%{#attr.controlId}" name="optionValues['%{#attr.valueKey}'].value" value="%{#attr.valueStr}" maxlength="2000" cssClass="middleLengthText"/>
                </td>
                <td class="withButton">
                    <ui:button message="form.browse" onclick="openFileBrowser(${controlIdStr}, '${fileTypesStr}');"/>
                </td>
                <td class="withError">
                    <s:fielderror><s:param value="'optionValues[' + #attr.valueKey + '].value'"/></s:fielderror>
                    <s:fielderror><s:param value="'fileUrls[' + #attr.valueKey + ']'"/></s:fielderror>
                </td>
            </s:elseif>
            <s:elseif test="#option.type.name == 'Enum'">
                <td class="withField">
                    <s:if test="#option.values.size() == 2">
                        <s:radio data-name="${option.token}" name="optionValues['%{#attr.valueKey}'].value"
                             list="#option.values" value="%{#attr.valueStr}"
                             listKey="value" listValue="name" />
                    </s:if>
                    <s:else>
                        <s:select list="#option.values" data-name="${option.token}" name="optionValues['%{#attr.valueKey}'].value" value="%{#attr.valueStr}"
                                  listKey="value" listValue="name"/>
                    </s:else>
                </td>
                <td class="withError">
                    <s:fielderror><s:param value="'optionValues[' + #attr.valueKey + '].value'"/></s:fielderror>
                </td>
            </s:elseif>
            <s:elseif test="#option.type.name =='Color'">
                <td class="withField">
                    <ui:colorInput id="color${valueKey}" dataname="${option.token}" name="optionValues['${valueKey}'].value" value="${valueStr}"/>
                </td>
                <td class="withError">
                    <s:fielderror><s:param value="'optionValues[' + #attr.valueKey + '].value'"/></s:fielderror>
                </td>
            </s:elseif>
        </tr>
    </table>

</ui:field>
