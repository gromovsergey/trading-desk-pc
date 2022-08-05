<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ah="java/com.foros.util.AuditHelper" 
                exclude-result-prefixes="ah">

    <xsl:param name="recordId"/>

    <xsl:template match="/auditRecord">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="auditRecord/entity">
        <xsl:if test="count(./property) > 0">
            <ul class="auditEntity" style="margin-bottom:0;">
                <xsl:apply-templates select="./property"/>
            </ul>
        </xsl:if>
    </xsl:template>

    <xsl:template match="entity">
            <xsl:call-template name="replace-string">
                <xsl:with-param name="text" select="@class"/>
                <xsl:with-param name="replace" select="'com.foros'" />
                <xsl:with-param name="with" select="''"/>
            </xsl:call-template>
            [
            <xsl:for-each select="@*[name(.) != 'class']">
                <span><xsl:value-of select="name(.)"/>=<xsl:value-of select="."/></span><xsl:if test="position()!=last()">, </xsl:if>
            </xsl:for-each>
            ]
            <xsl:if test="count(./property) > 0">
                <ul class="auditEntity" style="margin-bottom:0;">
                    <xsl:apply-templates select="./property"/>
                </ul>
            </xsl:if>
    </xsl:template>

    <xsl:template match="auditRecord/fileManager">
        <table class="auditCol">
            <tbody>
                <xsl:apply-templates select="item"/>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="property[@changeType='REMOVE']">
        <li>
            <strong><xsl:value-of select="@name"/></strong> <i> removed</i>
       </li>
    </xsl:template>

    <xsl:template match="property">
        <li>
            <strong><xsl:value-of select="@name"/></strong>:
            <xsl:choose>
                <xsl:when test="count(./entity) > 0">
                    <xsl:apply-templates select="./entity"/>
                </xsl:when>
                <xsl:when test="count(./collection) > 0">
                    <xsl:apply-templates select="./collection"/>
                </xsl:when>
                <xsl:when test="@hidden='true'">
                    *****
                </xsl:when>
                <xsl:when test="contains(@name,'arketplaceType') and . = 'FOROS'">
                    EX_WG
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </li>
    </xsl:template>

    <xsl:template match="entity/property[@changeType='REMOVE' and count(entity)>0]">
        <table class="auditCol">
            <tbody>
                <tr>
                    <td valign="top">removed</td>
                    <td><xsl:apply-templates/></td>
                </tr>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="collection">
        <table class="auditCol">
            <tbody>
                <xsl:apply-templates select="item"/>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="item[@changeType='REMOVE']">
        <tr>
            <td valign="top">removed</td>
            <xsl:call-template name="item"/>
        </tr>
    </xsl:template>

    <xsl:template match="item[@changeType='ADD']">
        <tr>
            <td valign="top">added</td>
            <xsl:call-template name="item"/>
        </tr>
    </xsl:template>

    <xsl:template match="item[@changeType='UPDATE']">
        <tr>
            <td valign="top">updated</td>
            <xsl:call-template name="item"/>
        </tr>
    </xsl:template>

    <xsl:template match="item[@changeType='UNCHANGED']">
        <tr>
            <xsl:choose>
                <xsl:when test="count(entity) > 0 and entity[@class='com.foros.model.creative.CreativeOptionValue']">
                    <td valign="top">updated</td>
                </xsl:when>
                <xsl:otherwise>
                    <td></td>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:call-template name="item"/>
        </tr>
    </xsl:template>

    <xsl:template name="item">
        <xsl:choose>
            <xsl:when test="count(entity) > 0">
                <td><xsl:apply-templates select="entity"/></td>
            </xsl:when>
            <xsl:otherwise>
                <td><xsl:value-of select="."/></td>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="property[@name='flags']" priority="1">
        <li><strong><xsl:value-of select="@name"/></strong>: <xsl:value-of select="ah:toHexString(string(.))"/></li>
    </xsl:template>


    <xsl:template match="property[@name='qaDescription' and @changeType != 'REMOVE']" priority="1">
        <li><strong><xsl:value-of select="@name"/></strong>: <xsl:value-of disable-output-escaping="yes" select="."/></li>
    </xsl:template>

    <xsl:template match="entity[@class='com.foros.model.account.PublisherAccount']/property/entity[@class='com.foros.model.account.AccountsPayableFinancialSettings']/property[@name='commission']" priority="1">
        <li><strong>Handling Fee</strong>: <xsl:value-of select="."/></li>
    </xsl:template>

    <xsl:template match="entity/property[@name='financialSettings']/entity/property[@name='taxNumber']" priority="1">
            <li><strong>vatNumber</strong>: <xsl:value-of select="."/></li>
    </xsl:template>

    <xsl:template match="entity[@class='com.foros.model.campaign.CcgChannel']" priority="1">
        <xsl:value-of select="property[@name='channel']/entity/property[@name='name']"/>
        :
        <xsl:value-of select="property[@name='threshold']"/>
    </xsl:template>

    <xsl:template match="entity[@class='com.foros.model.creative.CreativeOptionValue']" priority="1">
        <strong><xsl:value-of select="@name"/></strong> :
        <xsl:if test="property[@name='value']/@changeType != 'REMOVE'">
            <xsl:value-of select="property[@name='value']"/>
        </xsl:if>
    </xsl:template>
    <xsl:template match="entity[@class='com.foros.model.site.TagOptionValue']" priority="1">
        <strong><xsl:value-of select="@name"/></strong> : <xsl:value-of select="property[@name='value']"/>
    </xsl:template>

    <xsl:template match="entity/property[contains(@name,'.positive') or contains(@name,'.negative') or contains(@name,'keywords') or contains(@name,'pageSearchKeywords') or contains(@name,'URLs') or contains(@name,'urls') or contains(@name,'urlKeywords') or contains(@name,'placements')]/collection" priority="1">
        <xsl:variable name="currentXPath">
            <xsl:for-each select="ancestor-or-self::*">
                <xsl:text>/</xsl:text>
                <xsl:value-of select="name()"/>
                <xsl:text>[</xsl:text>
                <xsl:value-of select="1+count(preceding-sibling::*)"/>
                <xsl:text>]</xsl:text>
            </xsl:for-each>
        </xsl:variable>

        <xsl:variable name="added" select="count(item[@changeType='ADD'])"/>
        <xsl:variable name="removed" select="count(item[@changeType='REMOVE'])"/>

        <xsl:if test="$added > 0">
            <xsl:value-of select="$added"/>
            <xsl:text> added</xsl:text>
        </xsl:if>
        <xsl:if test="$added > 0 and $removed > 0">
            <xsl:text>, </xsl:text>
        </xsl:if>
        <xsl:if test="$removed > 0">
            <xsl:value-of select="$removed"/>
            <xsl:text> removed</xsl:text>
        </xsl:if>
        <xsl:text>. </xsl:text>

        <xsl:element name="a">
            <xsl:attribute name="targetTitle">
                <xsl:value-of select="parent::*/@name"/>
            </xsl:attribute>
            <xsl:attribute name="class">
                <xsl:text>logDetails</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="href">
                <xsl:text>/admin/auditLog/viewTriggers.action</xsl:text>
                <xsl:text>?recordId=</xsl:text>
                <xsl:value-of select="$recordId"/>
                <xsl:text>&amp;xpath=</xsl:text>
                <xsl:value-of select="$currentXPath"/>
            </xsl:attribute>
            <xsl:text>Show the list</xsl:text>
        </xsl:element>
        <xsl:text>.</xsl:text>
    </xsl:template>

    <xsl:template match="property[@name='timezone']/entity[@class='com.foros.model.Timezone']" priority="1">
        <xsl:value-of select="@key"/>
    </xsl:template>

    <xsl:template match="property[@name='country']/entity[@class='com.foros.model.Country']" priority="1">
        <xsl:value-of select="@code"/>
    </xsl:template>

    <xsl:template match="entity[@class='com.foros.model.currency.Currency']" priority="1">
        <xsl:value-of select="@code"/>
    </xsl:template>

    <xsl:template match="entity[@class='com.foros.model.currency.CurrencyExchangeAuditWrapper']/property[@name='rates']/collection" priority="1">
        <table class="auditCol">
            <tbody>
                <tr>
                    <td><span class="auditLabel">Currency</span></td>
                    <td><span class="auditLabel">Rate</span></td>
                    <td><span class="auditLabel">Update</span></td>
                </tr>
                <xsl:apply-templates select="item/entity"/>
            </tbody>
        </table>
    </xsl:template>
    <xsl:template match="entity[@class='com.foros.model.currency.CurrencyExchangeAuditWrapper']/property[@name='exchange']"/>
    <xsl:template match="entity[@class='com.foros.model.currency.CurrencyExchangeRateAuditWrapper']" priority="1">
        <tr>
            <td><xsl:value-of select="property[@name='rate']/entity/@currency"/></td>
            <td><xsl:value-of select="property[@name='rate']/entity/property[@name='rate']"/></td>
            <td>
                <xsl:choose>
                    <xsl:when test="property[@name='updated']='true'">
                        Updated
                    </xsl:when>
                    <xsl:otherwise>&#160;</xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="property[@name='currencyExchangeRates']/collection" priority="1">
        <table class="auditCol">
            <tbody>
                <tr>
                    <td><span class="auditLabel">Currency</span></td>
                    <td><span class="auditLabel">Rate</span></td>
                </tr>
                <xsl:apply-templates select="item/entity"/>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="entity[@class='com.foros.model.currency.CurrencyExchangeRate']" priority="1">
        <tr>
            <td><xsl:value-of select="@currency"/></td>
            <td><xsl:value-of select="property[@name='rate']"/></td>
        </tr>
    </xsl:template>

    <xsl:template match="login">
        <ul class="auditEntity">
            <li>Login <b><xsl:value-of select="user"/></b></li>
            <li>Status <b><xsl:value-of select="status"/></b></li>
        </ul>
    </xsl:template>

    <xsl:template match="approve">
        <ul class="auditEntity">
            <li><b>Action: </b><xsl:value-of select="action"/></li>
            <li><b>Description: </b><xsl:value-of select="description"/></li>
        </ul>
    </xsl:template>

    <xsl:template match="oraclejob">
        <ul class="auditEntity">
            <li>Job Name: <b><xsl:value-of select="name"/></b></li>
            <li>Description: <b><xsl:value-of select="description" disable-output-escaping="yes"/></b></li>
        </ul>
    </xsl:template>

    <xsl:template match="report">
        <ul class="auditEntity" style="margin-bottom:0;">
            <xsl:apply-templates select="./*"/>
        </ul>
    </xsl:template>

    <xsl:template match="report//*">
        <li>
            <strong><xsl:value-of select="@name"/></strong>:
            <xsl:choose>
                <xsl:when test="@name = 'parameters'">
                    <ul class="auditEntity" style="margin-bottom:0;">
                        <xsl:apply-templates select="./*"/>
                    </ul>
                </xsl:when>
                <xsl:when test="@name = 'columns'">
                    <ul class="auditEntity" style="margin-bottom:0;">
                        <xsl:for-each select="./*">
                            <li><xsl:value-of select="ah:getLocalizedStringWithDefault(string(.), string(.))"/></li>
                        </xsl:for-each>
                    </ul>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="."/>
                </xsl:otherwise>
            </xsl:choose>
        </li>
    </xsl:template>

    <xsl:template match="auditRecord/entity[@class='com.foros.model.report.birt.BirtReport']">
        <ul class="auditEntity" style="margin-bottom:0;">
            <li>
                <xsl:value-of select="@class" />
                [
                <xsl:for-each select="@*[name(.) != 'class']">
                    <span>
                        <xsl:value-of select="name(.)" />
                        =
                        <xsl:value-of select="." />
                    </span>
                    <xsl:if test="position()!=last()">
                        ,
                    </xsl:if>
                </xsl:for-each>
                ]
            </li>
            <xsl:if test="count(./property) >
        0">
                <ul class="auditEntity" style="margin-bottom:0;">
                    <xsl:apply-templates select="./property" />
                </ul>
            </xsl:if>
        </ul>
    </xsl:template>
    <xsl:template name="replace-string">
        <xsl:param name="text"/>
        <xsl:param name="replace"/>
        <xsl:param name="with"/>
        <xsl:choose>
          <xsl:when test="contains($text,$replace)">
            <xsl:value-of select="substring-before($text,$replace)"/>
            <xsl:value-of select="$with"/>
            <xsl:call-template name="replace-string">
              <xsl:with-param name="text" select="substring-after($text,$replace)"/>
              <xsl:with-param name="replace" select="$replace"/>
              <xsl:with-param name="with" select="$with"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$text"/>
          </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
