<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="yes"/>

    <xsl:param name="myParamValue"/>

    <xsl:template match="/">
        <books>
            <xsl:attribute name="value">
                <xsl:value-of select="$myParamValue" />
            </xsl:attribute>
            <xsl:apply-templates select="/bookstore/book/title[../price>$myParamValue]">
                <xsl:sort select="."/>
            </xsl:apply-templates>
        </books>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>