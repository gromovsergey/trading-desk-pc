<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">

    <xsl:template match="report">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4-landscape" page-height="21cm" page-width="29.7cm"
                                       margin-top="2cm" margin-bottom="15mm" margin-left="2cm" margin-right="2cm">
                    <fo:region-body margin-bottom="5mm"/>
                    <fo:region-after extent="5mm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="A4-landscape" force-page-count="no-force"  initial-page-number="1">
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block text-align="center" font-family="Arial" font-size="10pt">
                        - <fo:page-number/> -
                    </fo:block>
                </fo:static-content>

                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-family="Arial" font-size="11pt" space-after="10mm">
                        <fo:table table-layout="fixed" width="100%" space-after="5mm">
                            <fo:table-column column-width="50%"/>
                            <fo:table-column column-width="50%"/>
                            <fo:table-body>
                                <fo:table-row height="8mm">
                                    <fo:table-cell><fo:block/></fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block font-weight="bold">
                                            Приложение №3
                                        </fo:block>
                                        <fo:block>
                                            К Агентскому договору о распространении услуг Билайн
                                        </fo:block>
                                        <fo:block>
                                            №<xsl:value-of select="contractNumber"/> от <xsl:value-of select="contractDate"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                        <fo:table table-layout="fixed" width="100%" space-after="5mm">
                            <fo:table-column column-width="50%"/>
                            <fo:table-column column-width="50%"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>г. Москва</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right"><xsl:value-of select="reportDate"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                        <fo:block text-align="center" font-weight="bold" padding-bottom="3mm"
                                  border-bottom-width="1pt" border-bottom-style="solid" border-bottom-color="black">
                            ФОРМА ОТЧЕТА АГЕНТА
                        </fo:block>
                    </fo:block>

                    <fo:block font-family="Times New Roman" font-size="9pt" font-weight="bold" space-after="5mm">
                        <fo:block text-align="center">
                            Отчет Агента
                        </fo:block>
                        <fo:block text-align="center" space-after="5mm">
                            По Агентскому договору №<xsl:value-of select="contractNumber"/> от <xsl:value-of select="contractDate"/>
                        </fo:block>
                        <fo:table table-layout="fixed" width="100%" space-after="10mm">
                            <fo:table-column column-width="50%"/>
                            <fo:table-column column-width="50%"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell>
                                        <fo:block>г. Москва</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block text-align="right"><xsl:value-of select="reportDate"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                        <fo:block>
                            Агент: <xsl:value-of select="agentName"/>
                        </fo:block>
                        <fo:block>
                            Принципал: <xsl:value-of select="principalName"/>
                        </fo:block>
                    </fo:block>

                    <fo:block font-family="Times New Roman" font-size="8pt" space-after="5mm">
                        <fo:table table-layout="fixed" width="100%" border="1pt solid black">
                            <fo:table-column column-width="20%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-column column-width="10%"/>
                            <fo:table-body border="inherit">
                                <fo:table-row border="inherit" text-align="center">
                                    <fo:table-cell border="inherit">
                                        <fo:block>Наименование Рекламодателя</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block>Номера договоров Рекламодателей</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block>Номера выставленных с/ф за отчетный период</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block>Сумма выставленных с/ф за оказанные услуги (без НДС)</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block>Сумма авансов (*) от Рекламодателей на конец отчетного месяца (без НДС)</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block>Сумма оплаты Поставщику инвентаря (без НДС)</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block>База для расчета вознаграждения</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block>Сумма вознаграждения Агента (без НДС)</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block>Сумма, причитающаяся Принципалу (без НДС</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <xsl:apply-templates select="row"/>
                                <fo:table-row border="inherit">
                                    <fo:table-cell border="inherit">
                                        <fo:block>
                                            Всего
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block/>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block/>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit" text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="totalAmount"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit" text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="prepaymentAmount"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit" text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="pubAmount"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit" text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="totalNetAmount"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit" text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="agentAmount"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit" text-align="right">
                                        <fo:block>
                                            <xsl:value-of select="principalAmount"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>

                    <fo:block font-family="Times New Roman" font-size="9pt" text-align="justify" line-height="5mm" space-after="5mm">
                        <fo:block text-indent="5mm">
                            1. За <xsl:value-of select="month"/>&#160;<xsl:value-of select="year"/> г. общее количество заключенных от имени Принципала договоров с Рекламодателями составило <fo:inline font-weight="bold"><xsl:value-of select="contractAmount"/> шт.</fo:inline>, общее количество расторгнутых договоров с Рекламодателями составило <fo:inline font-weight="bold"><xsl:value-of select="annulledContractAmount"/> шт.</fo:inline>
                        </fo:block>
                        <fo:block text-indent="10mm">
                            Агент выставил счета и счета-фактуры клиентам Принципала на сумму (без НДС) <fo:inline font-weight="bold"><xsl:value-of select="totalAmount"/> (<xsl:value-of select="totalAmountInWords"/>)</fo:inline>, кроме того НДС в размере <fo:inline font-weight="bold"><xsl:value-of select="totalAmountVAT"/> (<xsl:value-of select="totalAmountVATInWords"/>).</fo:inline>
                        </fo:block>
                        <fo:block text-indent="5mm">
                            2. За <xsl:value-of select="month"/>&#160;<xsl:value-of select="year"/> г. вознаграждения Агента составляет сумму (без НДС) <fo:inline font-weight="bold"><xsl:value-of select="agentAmount"/> (<xsl:value-of select="agentAmountInWords"/>)</fo:inline>, кроме того НДС в размере <fo:inline font-weight="bold"><xsl:value-of select="agentAmountVAT"/> (<xsl:value-of select="agentAmountVATInWords"/>).</fo:inline>
                        </fo:block>
                        <fo:block text-indent="5mm">
                            3. За <xsl:value-of select="month"/>&#160;<xsl:value-of select="year"/> г. по Договору поставки оборудования и использования программного обеспечения № _________ от __.__.____ г. подлежит удержанию за поставленное оборудование 2% из суммы, причитающейся Принципалу (без НДС) <fo:inline font-weight="bold">_______________________ (_____________________________________ рублей __ копеек)</fo:inline>, кроме того НДС в размере <fo:inline font-weight="bold">___________________________ (_____________________________________________________ рублей __ копеек).</fo:inline>
                        </fo:block>
                        <fo:block text-indent="5mm">
                            4. Агентом подлежит к перечислению на расчетный счет Принципала денежные средства в размере <fo:inline font-weight="bold"><xsl:value-of select="principalAmount"/> (<xsl:value-of select="principalAmountInWords"/>)</fo:inline>, кроме того НДС в размере <fo:inline font-weight="bold"><xsl:value-of select="principalAmountVAT"/> (<xsl:value-of select="principalAmountVATInWords"/>)</fo:inline>, в порядке и сроки, предусмотренные Агентским договором №<xsl:value-of select="contractNumber"/> от <xsl:value-of select="contractDate"/>
                        </fo:block>
                    </fo:block>

                    <fo:block font-family="Times New Roman" font-size="9pt" font-weight="bold" space-after="5mm">
                        <fo:table table-layout="fixed" width="70%" border="1pt solid black">
                            <fo:table-column column-width="50%"/>
                            <fo:table-column column-width="50%"/>
                            <fo:table-body border="inherit" margin-left="2mm">
                                <fo:table-row border="inherit">
                                    <fo:table-cell border="inherit">
                                        <fo:block>От <xsl:value-of select="principalName"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block>От <xsl:value-of select="agentName"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row border="inherit" height="20mm">
                                    <fo:table-cell border="inherit">
                                        <fo:block margin-top="12mm">__________________</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="inherit">
                                        <fo:block margin-top="12mm">__________________</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>

                    <fo:block font-family="Times New Roman" font-size="9pt" space-after="15mm">
                        (*) Сумма авансов, не закрытых реализацией на конец отчетного месяца (разница между суммой авансовых счетов-фактур в Книге продаж и Книге покупок).
                    </fo:block>

                    <fo:block font-family="Arial" font-size="11pt">
                        <fo:block text-align="center" font-weight="bold" padding-top="3mm" border-top="1pt solid black" space-after="10mm">
                            <fo:block>В СВИДЕТЕЛЬСТВО ВСЕГО ВЫШЕИЗЛОЖЕННОГО настоящее Приложение</fo:block>
                            <fo:block>подписано уполномоченными представителями Сторон.</fo:block>
                        </fo:block>
                        <fo:table table-layout="fixed" width="100%">
                            <fo:table-column column-width="50%"/>
                            <fo:table-column column-width="50%"/>
                            <fo:table-body text-align="center">
                                <fo:table-row font-weight="bold" height="8mm">
                                    <fo:table-cell>
                                        <fo:block>За и от имени Билайн:</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>За и от имени Агента:</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="8mm">
                                    <fo:table-cell>
                                        <fo:block>Подпись _________________</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>Подпись _________________</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="8mm">
                                    <fo:table-cell>
                                        <fo:block><xsl:value-of select="principalDelegate"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block><xsl:value-of select="agentDelegate"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>

        </fo:root>
    </xsl:template>

    <xsl:template match="row">
        <fo:table-row border="inherit">
            <fo:table-cell border="inherit">
                <fo:block>
                    <xsl:value-of select="advertiserName"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="inherit">
                <fo:block>
                    <xsl:value-of select="contractNumber"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="inherit">
                <fo:block>
                    <xsl:value-of select="invoiceNumber"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="inherit" text-align="right">
                <fo:block>
                    <xsl:value-of select="totalAmountConfirmed"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="inherit" text-align="right">
                <fo:block>
                    <xsl:value-of select="prepaymentAmount"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="inherit" text-align="right">
                <fo:block>
                    <xsl:value-of select="pubAmountConfirmed"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="inherit" text-align="right">
                <fo:block>
                    <xsl:value-of select="totalNetAmount"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="inherit" text-align="right">
                <fo:block>
                    <xsl:value-of select="agentAmount"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="inherit" text-align="right">
                <fo:block>
                    <xsl:value-of select="principalAmount"/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>

</xsl:stylesheet>