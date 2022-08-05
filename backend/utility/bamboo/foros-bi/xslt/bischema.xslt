<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes" encoding="UTF-8" />

  <xsl:template match="/">
    <!--<html>
    <head/>
    <body>-->
    <xsl:apply-templates select="Schema"/>
    <!--</body>
    </html>-->
  </xsl:template>

  <xsl:template match="/Schema">
    <h1>Schema: "<xsl:value-of select="@name"/>"</h1>
    <xsl:apply-templates select="Dimension[not(@visible = 'false')]"/>
    <xsl:apply-templates select="Cube[not(@visible = 'false')]"/>
    <xsl:apply-templates select="VirtualCube[not(@visible = 'false')]"/>
  </xsl:template>

  <xsl:template match="/Schema/Dimension">
    <h2>Dimention: "<xsl:value-of select="@name"/>"</h2>
    <p><xsl:value-of select="@description"/></p>
    <!--<ul>-->
      <xsl:apply-templates select="Hierarchy"/>
      <!--</ul>-->
  </xsl:template>

  <xsl:template match="/Schema/Dimension/Hierarchy">
    <xsl:variable select="Table/@name" name="table" />
    <xsl:variable select="Table/@schema" name="schema" />
    <xsl:variable select="View/SQL" name="sql" />
    <xsl:variable name="name"><xsl:choose><xsl:when test="string-length(@name) &gt; 0"><xsl:value-of select="@name"/></xsl:when><xsl:otherwise><xsl:value-of select="../@name"/></xsl:otherwise></xsl:choose></xsl:variable>
    <xsl:choose>
      <xsl:when test="string-length($table) &gt; 0">
        <h3>Hierarchy <xsl:value-of select="$name"/></h3>: table = <xsl:value-of select="$table"/><xsl:choose><xsl:when test="string-length($schema) &gt; 0">, schema = "<xsl:value-of select="$schema"/>"</xsl:when><xsl:otherwise/></xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="string-length($sql) &gt; 0">
            <h3>Hierarchy <xsl:value-of select="$name"/></h3>: SQL = <xsl:value-of select="View/@alias"/>
            <ac--macro ac--name="code">
              <ac--parameter ac--name="language">sql</ac--parameter>
              <ac--plain-text-body><xsl:value-of select="'&lt;![CDATA['"/><xsl:value-of select="$sql"/><xsl:value-of select="']]&gt;'"/></ac--plain-text-body>
            </ac--macro>
          </xsl:when>
          <xsl:otherwise>
            <h3>Hierarchy <xsl:value-of select="$name"/></h3>: join = <xsl:for-each select="Join/Table"><xsl:value-of select="@name"/>,</xsl:for-each>

          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
    <p><xsl:value-of select="@description"/></p>
    <p><strong>Level:</strong></p>
    <ul>
      <xsl:apply-templates select="Level"/>
    </ul>
  </xsl:template>

  <xsl:template match="/Schema/Dimension/Hierarchy/Level">
    <xsl:variable select="@nameColumn" name="nameColumn" />
    <xsl:variable select="@ordinalColumn" name="ordinalColumn" />
    <li>
      <strong>"<xsl:value-of select="@name"/>", </strong>
      <span>column = <xsl:value-of select="@column"/>
        <xsl:choose>
          <xsl:when test="string-length($nameColumn) &gt; 0">
            , nameColumn = <xsl:value-of select="$nameColumn"/>
          </xsl:when>
        </xsl:choose>
        <xsl:choose>
          <xsl:when test="string-length($ordinalColumn) &gt; 0">
            , ordinalColumn = <xsl:value-of select="$ordinalColumn"/>
          </xsl:when>
        </xsl:choose>
      </span>
      <p><xsl:value-of select="@description"/></p>
    </li>
  </xsl:template>

  <xsl:template match="/Schema/Cube">
    <h2>Cube: "<xsl:value-of select="@name"/>"</h2>
    <p><xsl:value-of select="@description"/></p>
    <ul>
    <xsl:apply-templates select="Table[not(@visible = 'false')]"/>
    </ul>
    <ul>
      <xsl:apply-templates select="DimensionUsage"/>
    </ul>
    <ul>
      <xsl:apply-templates select="Measure[not(@visible = 'false')]"/>
      <xsl:apply-templates select="CalculatedMember[not(@visible = 'false')]"/>
    </ul>
  </xsl:template>

  <xsl:template match="/Schema/VirtualCube">
    <h2>Cube: "<xsl:value-of select="@name"/>"</h2>
    <p><xsl:value-of select="@description"/></p>
    <ul>
      <xsl:apply-templates select="VirtualCubeDimension[not(@visible = 'false')]"/>
    </ul>
    <ul>
      <xsl:apply-templates select="VirtualCubeMeasure[not(@visible = 'false')]"/>
      <xsl:apply-templates select="CalculatedMember[not(@visible = 'false')]"/>
    </ul>
  </xsl:template>

  <xsl:template match="/Schema/Cube/Table">
    <li><strong>Table</strong>: name = "<xsl:value-of select="@name"/>"<xsl:choose><xsl:when test="string-length(@schema) &gt; 0">, schema = "<xsl:value-of select="@schema"/>"</xsl:when><xsl:otherwise/></xsl:choose>
    <ul>
      <xsl:apply-templates select="AggName[not(@visible = 'false')]"/>
    </ul>
    </li>
  </xsl:template>

  <xsl:template match="/Schema/Cube/Table/AggName">
    <li><strong>Aggregate</strong>: name = "<xsl:value-of select="@name"/>", schema = "<xsl:value-of select="@schema"/>"</li>
  </xsl:template>

  <xsl:template match="/Schema/Cube/DimensionUsage">
    <li><strong>Dimention</strong>: <a href="#BISpecificationversion#VERSION#-Dimention{translate(@source, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ', 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789')}">"<xsl:value-of select="@source"/>"</a></li>
  </xsl:template>

  <xsl:template match="/Schema/Cube/Measure">
    <li><strong>Measure</strong>: "<xsl:value-of select="@name"/>", column = "<xsl:value-of select="@column"/>", aggregator = "<xsl:value-of select="@aggregator"/>"</li>
    <p><xsl:value-of select="@description"/></p>
  </xsl:template>

  <xsl:template match="/Schema/VirtualCube/VirtualCubeDimension">
    <li><strong>Dimention</strong>: <a href="#BISpecificationversion#VERSION#-Dimention{translate(@name, 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ', 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789')}">"<xsl:value-of select="@name"/>"</a></li>
  </xsl:template>

  <xsl:template match="/Schema/VirtualCube/VirtualCubeMeasure">
    <li><strong>Measure</strong>: "<xsl:value-of select="@name"/>"
      <p><xsl:value-of select="@description"/></p>
    </li>
  </xsl:template>

  <xsl:template match="/Schema/VirtualCube/CalculatedMember">
    <li><strong>Measure</strong>: "<xsl:value-of select="@name"/>"
      <p><xsl:value-of select="@description"/></p>
        <ac--macro ac--name="code">
          <ac--parameter ac--name="language">none</ac--parameter>
          <ac--plain-text-body><xsl:value-of select="'&lt;![CDATA['"/><xsl:value-of select="Formula"/><xsl:value-of select="']]&gt;'"/></ac--plain-text-body>
        </ac--macro>
    </li>
  </xsl:template>

  <xsl:template match="/Schema/Cube/CalculatedMember">
    <li><strong>Measure</strong>: "<xsl:value-of select="@name"/>"
      <p><xsl:value-of select="@description"/></p>
      <ac--macro ac--name="code">
        <ac--parameter ac--name="language">none</ac--parameter>
        <ac--plain-text-body><xsl:value-of select="'&lt;![CDATA['"/><xsl:value-of select="Formula"/><xsl:value-of select="']]&gt;'"/></ac--plain-text-body>
      </ac--macro>
    </li>
  </xsl:template>
</xsl:stylesheet>
