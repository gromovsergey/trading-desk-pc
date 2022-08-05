<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>

<s:set var="prefix">${param.prefix}</s:set>

<ui:field labelKey="WalledGarden.${prefix}.parameters" errors="${prefix}Marketplace" required="true">
    <label class="withInput">
        <s:checkbox id="%{prefix}MarketplaceWG" name="%{prefix}Marketplace.inWG"/><s:text name="WalledGarden.%{prefix}.marketplace.WG"/>
    </label>
    <table class="fieldAndAccessories">
        <tr>
            <td class="withField">
                <label class="withInput">
                    <s:checkbox id="%{prefix}MarketplaceExWG" name="%{prefix}Marketplace.exWG"/><s:text name="WalledGarden.%{prefix}.marketplace.FOROS"/>
                </label>
            </td>
            <td class="withTip">
                <ui:hint>
                    <s:text name="WalledGarden.%{prefix}.marketplace.FOROS.tip"/>
                </ui:hint>
            </td>
        </tr>
    </table>
</ui:field>
