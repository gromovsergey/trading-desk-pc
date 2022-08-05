<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rp-core="http://reporting.pentaho.org/namespaces/engine/attributes/core"
  xmlns:rp-lo="http://reporting.pentaho.org/namespaces/engine/classic/bundle/layout/1.0"
  xmlns:rp-sql="http://jfreereport.sourceforge.net/namespaces/datasources/sql">

  <xsl:output method="xml" indent="yes" encoding="UTF-8" />

  <xsl:template match="/">
    <!--<html>
    <head/>
    <body>-->
    <strong>SQLs</strong>
    <p>
      <ul>
        <xsl:apply-templates select="//rp-sql:query"/>
      </ul>
    </p>
    <!--</body>
    </html>-->
  </xsl:template>

  <xsl:template match="rp-sql:query">
    <xsl:variable select="./rp-sql:static-query" name="sql"/>
    <li>
      <strong><xsl:value-of select="@name"/></strong>
      <p>
        <ac--macro ac--name="code">
          <ac--parameter ac--name="language">sql</ac--parameter>
          <ac--plain-text-body><xsl:value-of select="'&lt;![CDATA['"/><xsl:value-of select="$sql"/><xsl:value-of select="']]&gt;'"/></ac--plain-text-body>
        </ac--macro>
      </p>
    </li>
  </xsl:template>
</xsl:stylesheet>

