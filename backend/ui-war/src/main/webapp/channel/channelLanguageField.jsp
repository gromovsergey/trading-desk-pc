<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<ui:field id="languageElem" labelKey="channel.language" labelForId="language" errors="language.languageCode">
    <s:select name="language" id="language" cssClass="middleLengthText"
              list="availableLanguages"
              listKey="code" listValue="name"/>
    <script type="text/javascript">
        $('#countryCode').change(function() {
            var countryCode = $('#countryCode').val();
            $.ajax({
                type : 'POST',
                url : '/xml/countryInfo.action',
                data : {countryCode: countryCode},
                success : function(xml) {
                    var lang = $(xml).find('language').text();
                    if (lang == '') {
                        lang = 'en';
                    }
                    $('#language').val(lang);
                },
                waitHolder: null
            });
        });
    </script>
</ui:field>
