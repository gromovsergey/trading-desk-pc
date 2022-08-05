<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rp-core="http://reporting.pentaho.org/namespaces/engine/attributes/core"
  xmlns:rp-lo="http://reporting.pentaho.org/namespaces/engine/classic/bundle/layout/1.0"
  xmlns:rp-data="http://reporting.pentaho.org/namespaces/engine/classic/bundle/data/1.0">

  <xsl:output method="xml" indent="yes" encoding="UTF-8" />

  <xsl:template match="/">
    <strong>Filters</strong>
    <p>
      <ul>
        <xsl:apply-templates select="//rp-data:plain-parameter"/>
      </ul>
    </p>
  </xsl:template>

  <xsl:template match="rp-data:plain-parameter">
    <li>
      <strong><xsl:value-of select="@name"/></strong>
      <xsl:choose>
        <xsl:when test="@mandatory = 'true'"> - mandatory</xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="string-length(@default-value) &gt; 0"> - default: '<xsl:value-of select="@default-value"/>'</xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
    </li>
  </xsl:template>
</xsl:stylesheet>


