<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>

<ui:field labelKey="${fieldLabelKey}" labelForId="accountPair"  required="true">
    <ui:optiontransfer
        name="accounts"
        size="9"
        escape="true"
        cssClass="middleLengthText"
        listKey="id"
        listValue="name"
        list="${availableAccounts}"
        selList="${selectedAccounts}"
        selListKey="id"
        selListValue="name"
        titleKey="report.accounts.available"
        selTitleKey="report.accounts.selected"
        saveSorting="true"
        onchange="onAccountsChange();"
    />

</ui:field>
