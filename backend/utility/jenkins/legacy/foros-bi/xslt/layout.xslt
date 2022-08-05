<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rp-core="http://reporting.pentaho.org/namespaces/engine/attributes/core"
  xmlns:rp-lo="http://reporting.pentaho.org/namespaces/engine/classic/bundle/layout/1.0" >

  <xsl:output method="xml" indent="yes" encoding="UTF-8" />

  <xsl:template match="/">
    <!--<html>
    <head/>
    <body>-->
    <strong>Columns</strong>
    <p>
      <ul>
        <xsl:apply-templates select="//rp-lo:label"/>
      </ul>
    </p>
    <!--</body>
    </html>-->
  </xsl:template>

  <xsl:template match="rp-lo:label">
    <li><xsl:value-of select="./rp-core:value"/></li>
  </xsl:template>
</xsl:stylesheet>

