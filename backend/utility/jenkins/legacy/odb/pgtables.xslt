<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ac="http://atlassian.com/confluence"
  xmlns:ri="http://atlassian.com/richtext">
  <xsl:output method="text" encoding="UTF-8" />
  <xsl:param name='number' select="'1'"/>
  <xsl:template match="/">
    <xsl:apply-templates select="//table[number($number)]"/>
  </xsl:template>
  <xsl:template match="table">
    <xsl:for-each select="tbody/tr[td]">
      <xsl:for-each select="td"><xsl:value-of select="."/>;</xsl:for-each>;
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
