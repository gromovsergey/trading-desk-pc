<%@ taglib tagdir="/WEB-INF/tags" prefix="ui" %>
<%@ taglib uri="/ad/serverUI" prefix="ad" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
    function sitesControl(toHide){
        $('#siteTable')[toHide ? 'hide' : 'show']();
        $('#site_targeting_section').css({"width":toHide ?"auto":"100%"});
    }

    $(function(){
        $('#site_targeting_section:visible').css({"width":"100%"});
    });
</script>

<s:if test="%{canEditSites()}">
    <a name="ccg_site_targeting"></a>
    <ui:section titleKey="ccg.site.targeting" id="site_targeting_section" cssClass="widest">
      <%-- Now label column present here and is empty --%>
      <%-- Need to remove empty columns --%>
      <ui:fieldGroup>
      
        <ui:field>
            <table class="grouping">
                <tr>
                    <td>
                        <label class="withInput">
                            <s:radio name="includeSpecificSitesFlag" id="sitesAllId" list="false" template="justradio" onclick="sitesControl(true);"
                            /><fmt:message key="ccg.site.all"/>
                        </label>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label class="withInput">
                            <s:radio name="includeSpecificSitesFlag" id="siteSpecificSitesId" list="true" template="justradio" onclick="sitesControl(false);"
                            /><fmt:message key="ccg.site.specific"/>
                        </label>
                    </td>
                </tr>
            </table>
        </ui:field>
        
        <ui:field id="siteTable">
            <table class="grouping">
                <tr>
                    <s:fielderror><s:param value="'sites'"/></s:fielderror>
                </tr>
                <tr>
                    <td>
                        <ui:optiontransfer
                            name="selectedSites"
                            size="9"
                            cssClass="middleLengthText"
                            list="${availableSites}"
                            selList="${groupSites}"
                            titleKey="ccg.site.select.available"
                            selTitleKey="ccg.site.select.selected"
                            saveSorting="true"
                            mandatory="true"
                          />
                    </td>
                </tr>
            </table>
        </ui:field>
        
      </ui:fieldGroup>
    </ui:section>
    
    <s:if test="!includeSpecificSitesFlag">
      <script type="text/javascript">
        $().ready(function(){
            sitesControl(true);
        })
      </script>
    </s:if>
</s:if>
