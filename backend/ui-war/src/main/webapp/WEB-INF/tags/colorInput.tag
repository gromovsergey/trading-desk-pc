<%@ tag language="java" body-content="empty" description="Color Input Tag" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="dataname" %>

<script type="text/javascript">
    $(document).ready(function(){
        
        function setColorDisplay(jInput, colorNum){
            var jColorOut = $('#${pageScope.id}Color');
            
            if(jInput.val()){
                jColorOut.show()
                    .css({backgroundColor : '#' + ('ffffff' + jInput.val()).slice(-6)});
            }else{
                jColorOut.hide();
            }
        };
        
        $('#${pageScope.id}Input')
            .bind('keyup paste', function(){
                var jSelf = $(this);
                setTimeout(function(){setColorDisplay(jSelf)}, 1);
            })
            .change(function(){
                setColorDisplay($(this));
            })
            .change();
    });
</script>

<span class="colorInput" id="${pageScope.id}Span">
       # <input id="${pageScope.id}Input"
                type="text"
                data-name="${pageScope.dataname}"
                name="${pageScope.name}"
                value="<c:out value="${pageScope.value}"/>"
                maxlength="6"
                class="smallLengthText"
        />
        <input id="${pageScope.id}Color"
               onfocus="this.blur();"
               class="colorBox"
               type="text" 
               readonly="readonly"
               style="background-color:#<c:out value='${empty pageScope.value ? "ffffff" : pageScope.value}'/>;"
               tabindex="-1"
        />
</span>
