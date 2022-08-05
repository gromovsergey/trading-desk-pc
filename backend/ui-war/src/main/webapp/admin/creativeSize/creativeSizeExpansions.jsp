<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<jsp:useBean id="expansionsComparator" class="org.springframework.util.comparator.ComparableComparator" scope="page"/>
<s:sort comparator="#attr.expansionsComparator" source="selectedExpansions" var="sortedSelectedExpansions"/>
<s:sort comparator="#attr.expansionsComparator" source="availableExpansions" var="sortedAvailableExpansions"/>
<ui:optiontransfer
    selList="${ad:convertEnums(sortedSelectedExpansions.list)}"
    selListKey="id"
    selListValue="name"
    list="${ad:convertEnums(sortedAvailableExpansions.list)}"
    listKey="id"
    listValue="name"
    name="expansions"
    cssClass="smallLengthText2" size="6"
    saveSorting="true"
    titleKey="CreativeSize.options.available"
    selTitleKey="CreativeSize.options.selected"/>
