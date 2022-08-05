<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ah="java/com.foros.util.AuditHelper">

  <xsl:template name="translateName">
    <xsl:choose>
      <xsl:when test="@name='frequencyCap'">frequency caps</xsl:when>
      <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="/audit-record">
        <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="collection">
    <table class="auditCol">
      <tbody>
      <xsl:for-each select="entity">
        <xsl:if test="count(property) > 0">
        <tr>
          <td><xsl:apply-templates select="."/></td>
        </tr>
        </xsl:if>
      </xsl:for-each>
      </tbody>
    </table>
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
  
  <xsl:template match="entity">
    <xsl:if test="count(property) > 0">
    <ul class="auditEntity" style="margin-bottom:0;">
      <xsl:for-each select="property">
        <li>
          <span class="auditLabel"><xsl:call-template name="translateName"/>:</span>
          <xsl:apply-templates/>
        </li>
      </xsl:for-each>
    </ul>
    </xsl:if>
  </xsl:template>

  <xsl:template match="entity[@class='com.foros.model.campaign.CcgChannel']" priority="1">
    <xsl:value-of select="property[@name='channel']/entity/property[@name='name']"/>
    : 
    <xsl:value-of select="property[@name='threshold']"/>
  </xsl:template>
  
  <xsl:template match="entity[@class='com.foros.model.FrequencyCap']" priority="1">
    <ul class="auditEntity">
      <li>period - 1 impression per <xsl:call-template name="formatSeconds">
					<xsl:with-param name="value"
						select="property[@name='period']" />
				</xsl:call-template>
	  </li>		
      <li><xsl:value-of select="property[@name='lifeCount']"/> impressions life total</li>
      <li>maximum <xsl:value-of select="property[@name='windowCount']"/> impressions per 
        	<xsl:call-template name="formatSeconds">
					<xsl:with-param name="value"
						select="property[@name='windowLength']" />
				</xsl:call-template>
      </li>
    </ul>
  </xsl:template>
  <xsl:template name="formatSeconds" priority="1">
	<xsl:param name="value" />
	<xsl:variable name="period" select="$value" />
	<xsl:variable name="formatPeriod">
		<xsl:choose>
	 	<xsl:when test="$period mod (604800) = 0">
	 		<xsl:value-of select="$period div (604800)" />
     	</xsl:when>
	 	<xsl:when test="$period mod (86400) = 0">
	 		<xsl:value-of select="$period div (86400)" />
	 	</xsl:when>
	 	<xsl:when test="$period mod  3600 = 0">
	 		<xsl:value-of select="$period div (3600)" />
	 	</xsl:when>
	 	<xsl:when test="$period mod 60 = 0">
	 		<xsl:value-of select="$period div 60" />
	 	</xsl:when>
	 	<xsl:otherwise>
	 		<xsl:value-of select="$period" />
	 	</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="periodMetric">
		<xsl:choose>
		<xsl:when test="$period mod (604800) = 0">
			<xsl:value-of select="'week'" />
		</xsl:when>
		<xsl:when test="$period mod (86400) = 0">
			<xsl:value-of select="'day'" />
		</xsl:when>
		<xsl:when test="$period mod 3600 = 0">
			<xsl:value-of select="'hour'" />
		</xsl:when>
		<xsl:when test="$period mod 60 = 0">
			<xsl:value-of select="'minute'" />
		</xsl:when>
		<xsl:otherwise>second</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
    <xsl:value-of select='$formatPeriod' />&#160;<xsl:value-of select='$periodMetric' /><xsl:if test="$formatPeriod!=1">s</xsl:if>
	</xsl:template>

   <xsl:template match="entity[@class='com.foros.model.site.SiteSetting']" priority="1">
    <ul class="auditEntity">
      <li>country: <xsl:value-of select="property[@name='country']/entity/property[@name='fullName']"/></li>
      <li>size: <xsl:value-of select="property[@name='size']/entity/property[@name='name']"/></li>
      <li>cpm: <xsl:value-of select="property[@name='siteRate']/entity/property[@name='cpm']"/></li>
    </ul>
  </xsl:template>
  <xsl:template match="entity[@class='com.foros.model.creative.CreativeOptionValue']" priority="1">
    <xsl:value-of select="property[@name='option']/entity/property[@name='name']"/>
    : 
    <xsl:value-of select="property[@name='value']"/>
  </xsl:template>
  <xsl:template match="entity[@class='com.foros.model.site.WDTagOptionValue']" priority="1">
    <xsl:value-of select="property[@name='option']/entity/property[@name='name']"/>
    :
    <xsl:value-of select="property[@name='value']"/>
  </xsl:template>

  <xsl:template match="collection/entity[@class='com.foros.model.site.Site']" priority="1">
    <xsl:value-of select="property[@name='account']/entity/property[@name='name']"/>/<xsl:value-of select="property[@name='name']"/>
  </xsl:template>
  
  <xsl:template match="/audit-record/entity/property/entity[@class='com.foros.model.account.Account']" priority="1">
    <xsl:value-of select="property[@name='name']"/>
  </xsl:template>

  <xsl:template match="property[@name='flags']" priority="1">
     <li><strong><xsl:value-of select="@name"/></strong>: <xsl:value-of select="ah:toHexString(string(.))"/></li>
  </xsl:template>

  <xsl:template match="property[@name='timezone']/entity[@class='com.foros.model.Timezone']" priority="1">
    <xsl:value-of select="property[@name='key']"/>
  </xsl:template>

  <xsl:template match="property[@name='currencyExchangeRates']/collection" priority="1">
    <table class="auditCol">
      <tbody>
        <tr>
          <td><span class="auditLabel">Currency</span></td>
          <td><span class="auditLabel">Rate</span></td>
        </tr>
      <xsl:for-each select="entity">
        <tr>
          <td><xsl:value-of select="property[@name='currency']/entity/property[@name='currencyCode']"/></td>
          <td><xsl:value-of select="property[@name='rate']"/></td>
        </tr>
      </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

  <xsl:template match="entity[@class='com.foros.model.site.Site']/property[@name='advertisers']/collection/entity[@class='com.foros.model.account.Account']" priority="1">
    <xsl:if test="count(property) > 0">
      <ul class="auditEntity" style="margin-bottom:0;">
        <li><b>Id: </b><xsl:value-of select="property[@name='id']"/></li>
        <li><b>Name: </b><xsl:value-of select="property[@name='name']"/></li>
      </ul>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
