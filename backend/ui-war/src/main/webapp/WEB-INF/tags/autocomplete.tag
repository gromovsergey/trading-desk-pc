<%@ tag description="Select box for long lists" %>

<%@ tag import="java.util.Collection" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="source" %>
<%@ attribute name="requestType" %>
<%@ attribute name="isMultiSelect" %>
<%@ attribute name="requestDataCb" %>
<%@ attribute name="selectedItems" rtexprvalue="true" type="java.util.Collection" %>
<%@ attribute name="selectedNameKey" %>
<%@ attribute name="selectedValueKey" %>
<%@ attribute name="onSelect" %>
<%@ attribute name="onCloseOption" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="minLength" %>
<%@ attribute name="maxLength" %>
<%@ attribute name="editable" %>
<%@ attribute name="submitMode" %>
<%@ attribute name="addInLowercase" %>
<%@ attribute name="defaultLabel" %>
<%@ attribute name="defaultValue" %>

<script type="text/javascript">
    var Autocomplete = Autocomplete || {};
    var screenedId = '${pageScope.id}'.replace(/\./g, 'Point');
    
    Autocomplete[screenedId] = {};
</script>

<input id="${pageScope.id}" class="${pageScope.cssClass}" maxlength="${pageScope.maxLength}"/>
<jsp:doBody/>

<script type="text/javascript">
    $.fn.getComboVal = $.fn.getComboVal || function(){
        var jSelf = $(this);
        
        if(jSelf.data('extAutocomplete')){
            return jSelf.extAutocomplete('getVal');
        }else if(jSelf.data('tokenizer')){
            return jSelf.tokenizer('getVal');
        }
    };

    $(function(){
       
        function getSelectedData(){
            var selectedData = [];
            var selectedVals = [];
            
            <c:if test="${not empty pageScope.selectedItems and not empty pageScope.selectedNameKey}">
                <c:forEach items="${pageScope.selectedItems}" var="item">
                    <c:set var="subnames" value="${fn:split(pageScope.selectedNameKey, '.')}" />
                    <c:set var="subItem" value="${item}" />
                    
                    <c:forEach items="${pageScope.subnames}" var="subname">
                        <c:set var="subItem" value="${pageScope.subItem[pageScope.subname]}" />
                    </c:forEach>
                    
                    <c:set var="escapedName" value="${fn:replace(pageScope.subItem, \"'\", \"\\\x27\")}" />
                    <c:set var="escapedName" value="${fn:replace(pageScope.escapedName, '<', '\\\x3C')}" />
                    <c:set var="escapedName" value="${fn:replace(pageScope.escapedName, '>', '\\\x3E')}" />
                    <c:set var="escapedName" value="${fn:replace(pageScope.escapedName, ';', '\\\x3B')}" />
                    
                    selectedData.push({label:'${pageScope.escapedName}', value:'${pageScope.escapedName}'});
                </c:forEach>
                
                <c:if test="${not empty pageScope.selectedValueKey}">
                    <c:forEach items="${pageScope.selectedItems}" var="item">
                        <c:set var="subnames" value="${fn:split(pageScope.selectedValueKey, '.')}" />
                        <c:set var="subItem" value="${item}" />
                        
                        <c:forEach items="${pageScope.subnames}" var="subname">
                            <c:set var="subItem" value="${pageScope.subItem[pageScope.subname]}" />
                        </c:forEach>
                        
                        selectedVals.push('${pageScope.subItem}');
                    </c:forEach>
                </c:if>
            </c:if>
            
            $.each(selectedVals, function(i){
                selectedData[i].value = this;
            });
            
            return selectedData;
        };
        
        function getSource(source){
            var getType = function(str) {
                try {
                    var obj = eval(str);
                    if ($.isFunction(obj)){
                        return 'function'
                    } else if ($.isArray(obj)){
                        return 'array'
                    } else if ($.isNumeric(obj)){
                        return 'number'
                    } else {
                        return typeof obj;
                    }
                } catch(e) {
                    if (typeof(str) == 'string'){
                        return 'string';
                    } else {
                        throw e;
                    }
                }
            },
            typeOfSource = getType(source);
            
            if (typeOfSource == 'function' || typeOfSource == 'array'){
                return eval(source);
            } else if (typeOfSource == 'string'){ // if source is string (url)
                return function(request, response) {
                    $.ajax({
                        url: source,
                        data: '${pageScope.requestDataCb}' ? eval('${pageScope.requestDataCb}')(request.term) : {query : request.term},
                        type: '${not empty pageScope.requestType ? pageScope.requestType : "GET"}',
                        dataType: 'xml',
                        cache: true,
                        waitHolder: jInput, 
                        success: function (xmlData) {
                            var list = $.map($('option', xmlData), function(el){
                                return new $.custom.extAutocomplete.Option($(el).text(), $(el).attr('id'));
                            });
                            response(list);
                        }
                    }); 
                }
            } else {
                return [];
            }
        };

        var screenedId = '${pageScope.id}'.replace(/\./g, '\\.'); // screening 'point' symbols
        var jInput = $('#' + screenedId);
        var multiply = eval('${pageScope.isMultiSelect}') ? true : false;
        
        var noResultsText = '<fmt:message key="form.autocomplete.noResults"/>';
        var addNewText = '<fmt:message key="form.addNewLC"/>';
        var minLength = +'${pageScope.minLength}';
        var pick = ${(not empty pageScope.onSelect) ? 'true' : 'false'}
            ? eval(${pageScope.onSelect}) : function(){};
        var source = getSource('${not empty pageScope.source ? pageScope.source : ""}');

        if(multiply){
            jInput.tokenizer({
                noResultsText : noResultsText,
                addNewText : addNewText,
                minLength : minLength,
                optionClose : ${(not empty pageScope.onCloseOption) ? 'true' : 'false'}
                    ? eval(${pageScope.onCloseOption}) : function(){},
                editable : ${(not empty pageScope.editable) ? 'true' : 'false'} 
                    ? eval(${pageScope.editable}) : false, 
                pick : pick,
                pickedOpts : getSelectedData(), 
                keepText : ${(pageScope.submitMode == 'value+name') ? 'true' : 'false'}, 
                paramValueName : '${(not empty pageScope.selectedValueKey) ? pageScope.selectedValueKey : ""}', 
                paramTextName : '${(not empty pageScope.selectedNameKey) ? pageScope.selectedNameKey : ""}', 
                addInLowercase : eval('${pageScope.addInLowercase}') ? true : false,
                source : source 
            });
        }else{
            jInput.extAutocomplete({
                noResultsText : noResultsText,
                minLength : minLength,
                pick : pick,
                source : source,
                defaultLabel : '${pageScope.defaultLabel}',
                defaultValue : '${pageScope.defaultValue}'
            });
        }
    });
</script>
