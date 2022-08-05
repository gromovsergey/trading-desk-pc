<%@ tag language="java" body-content="empty" description="ui post button" %>

<%@ attribute name="id" %>
<%@ attribute name="href" required="true" %>
<%@ attribute name="entityId" %>
<%@ attribute name="message" %>
<%@ attribute name="messageText" %>
<%@ attribute name="onclick"%>
<%@ attribute name="styleClass"%>
<%@ attribute name="subClass"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>

<c:if test="${empty pageScope.styleClass}">
    <c:set var="styleClass" value="button"/>
</c:if>

<c:set var="buttonCaption">
    <c:choose>
        <c:when test="${not empty pageScope.message}">
            <fmt:message key="${pageScope.message}"/>
        </c:when>
        <c:when test="${not empty pageScope.messageText}">${pageScope.messageText}</c:when>
        <c:otherwise></c:otherwise>
    </c:choose>
</c:set>


<c:if test="${not empty pageScope.buttonCaption}">
    <c:set var="url">${pageScope.href}</c:set>
    
    <c:set var="idAttachment">
        <c:choose>
            <c:when test="${not empty pageScope.id}">${pageScope.id}</c:when>
            <c:otherwise><%= (int) (Math.random() * 10000000) %></c:otherwise>
        </c:choose>
    </c:set>
    <c:set var="formId">form_${pageScope.idAttachment}</c:set>
    <c:set var="buttonId">button_${pageScope.idAttachment}</c:set>
    
    <c:set var="hiddenInputForId">
        <c:choose>
            <c:when test="${not empty pageScope.entityId}">
                <input type="hidden" name="id" value="${pageScope.entityId}"/>
            </c:when>
            <c:otherwise></c:otherwise>
        </c:choose>
    </c:set>

    <ui:button id="${pageScope.buttonId}" subClass="${pageScope.subClass}" messageText="${pageScope.buttonCaption}" type="link" />
    
    <script type="text/javascript">
        $(function(){
            var currButton = $('#${pageScope.buttonId}'),
            currForm = $('<form id="${pageScope.formId}" method="post" action="${pageScope.url}"> \
                <input type="hidden" name="PWSToken" value="${sessionScope.PWSToken}"/> \
                ${pageScope.hiddenInputForId} \
                </form>');

            currForm.prependTo('body');
            currButton.data('currForm', currForm);
            
            var buttOnClick;

            var doOnClick = function(that) {
                var result = (function(){
                    ${pageScope.onclick};
                }).apply(that);

                if(result !== false){
                    currForm.submit();
                }
            };

            if(!$.browser.safari){
                buttOnClick = function() {
                    doOnClick(this);
                    return false;
                }
            }else{
                buttOnClick = function(){
                    var that = this;
                    setTimeout(function(){ // "setTimeout" - is hack for preventing bug with loosing focus in Safari (OUI-17358)
                        doOnClick(that);
                    }, 10);
                    return false;
                };
            }

            currButton.click(buttOnClick);
        });
    </script>
</c:if>

