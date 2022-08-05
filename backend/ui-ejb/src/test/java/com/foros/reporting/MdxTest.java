package com.foros.reporting;

import com.phorm.oix.olap.OlapIdentifier;
import com.phorm.oix.olap.mdx.AxisesElement;
import com.phorm.oix.olap.mdx.BinaryOperatorElement;
import com.phorm.oix.olap.mdx.BindingElement;
import com.phorm.oix.olap.mdx.CurrentMemberElement;
import com.phorm.oix.olap.mdx.DataSetElement;
import com.phorm.oix.olap.mdx.FunctionElement;
import com.phorm.oix.olap.mdx.LevelElement;
import com.phorm.oix.olap.mdx.MemberElement;
import com.phorm.oix.olap.mdx.RootElement;
import org.junit.Assert;
import org.junit.Test;

public class MdxTest {

    public static final String MDX =
            "WITH   SET [MEMBERS_TimeDimension_] AS {[Country Date].[2014].[2].[4].[1] : [Country Date].[2014].[2].[5].[20]}   SET [MEMBERS_ISP_] AS FILTER([ISP].[ISP Account].members , ANCESTOR([ISP].currentmember , [ISP].[Isp Country]) IN {[ISP].[RU]})   SET [MEMBERS_Advertiser_] AS [Advertiser].[Advertiser Account].members   SET [MEMBERS_Measures_] AS {[Measures].[imps] , [Measures].[clicks] , [Measures].[requests] , [Measures].[Campaign Monthly Unique Users] , [Measures].[Campaign Weekly Unique Users]}  SELECT   [MEMBERS_Measures_] ON COLUMNS ,   NON EMPTY NONEMPTYCROSSJOIN(NONEMPTYCROSSJOIN([MEMBERS_ISP_] , [MEMBERS_TimeDimension_]) , [MEMBERS_Advertiser_]) ON ROWS FROM [CustomReport]";

    public static final String MDX1 =
            "WITH   SET [MEMBERS_TimeDimension_] AS {[Country Date].[2014].[2].[4].[1] : [Country Date].[2014].[2].[5].[20]}   SET [MEMBERS_Advertiser_] AS [Advertiser].[Advertiser Account].members   SET [MEMBERS_Measures_] AS {[Measures].[imps] , [Measures].[clicks] , [Measures].[requests] , [Measures].[Campaign Monthly Unique Users] , [Measures].[Campaign Weekly Unique Users]}  SELECT   [MEMBERS_Measures_] ON COLUMNS ,   NON EMPTY NONEMPTYCROSSJOIN([MEMBERS_TimeDimension_] , [MEMBERS_Advertiser_]) ON ROWS FROM [CustomReport] WHERE [ISP].[RU]";

    @Test
    public void testMdxGeneration() {

        RootElement generator = new RootElement("CustomReport");

        generator.getBindingsElement()
                .add(
                        new BindingElement(
                                "MEMBERS_TimeDimension_",
                                new DataSetElement(
                                        new BinaryOperatorElement(
                                                ":",
                                                new MemberElement("Country Date", "2014", "2", "4", "1"),
                                                new MemberElement("Country Date", "2014", "2", "5", "20")
                                        )
                                )
                        )
                )
                .add(
                        new BindingElement(
                                "MEMBERS_ISP_",
                                new FunctionElement(
                                        "FILTER",
                                        new LevelElement("ISP", "ISP Account"),
                                        new BinaryOperatorElement(
                                                "IN",
                                                new FunctionElement(
                                                        "ANCESTOR",
                                                        new CurrentMemberElement("ISP"),
                                                        new MemberElement("ISP", "Isp Country")
                                                ),
                                                new DataSetElement(new MemberElement("ISP", "RU"))
                                        )
                                )
                        )
                )
                .add(
                        new BindingElement(
                                "MEMBERS_Advertiser_",
                                new LevelElement("Advertiser","Advertiser Account")
                        )
                )
                .add(
                        new BindingElement(
                                "MEMBERS_Measures_",
                                new DataSetElement(
                                        new MemberElement("Measures", "imps"),
                                        new MemberElement("Measures", "clicks"),
                                        new MemberElement("Measures", "requests"),
                                        new MemberElement("Measures", "Campaign Monthly Unique Users"),
                                        new MemberElement("Measures", "Campaign Weekly Unique Users")
                                )
                        )
                )
        ;

        AxisesElement axisesElement = generator.getAxisesElement();

        axisesElement.getColumns()
                .add(
                        new MemberElement("MEMBERS_Measures_")
                );

        axisesElement.getRows()
                .add(
                        new FunctionElement(
                                "NONEMPTYCROSSJOIN",
                                new FunctionElement(
                                        "NONEMPTYCROSSJOIN",
                                        new MemberElement("MEMBERS_ISP_"),
                                        new MemberElement("MEMBERS_TimeDimension_")
                                ),
                                new MemberElement("MEMBERS_Advertiser_")
                        )
                );

        Assert.assertEquals("Mdx generation failed", MDX, generator.toMdx().replace('\n', ' ').replace('\t', ' '));
    }

    @Test
    public void testMdxGeneration1() {

        RootElement generator = new RootElement("CustomReport");

        generator.getBindingsElement()
                .add(
                        new BindingElement(
                                "MEMBERS_TimeDimension_",
                                new DataSetElement(
                                        new BinaryOperatorElement(
                                                ":",
                                                new MemberElement("Country Date", "2014", "2", "4", "1"),
                                                new MemberElement("Country Date", "2014", "2", "5", "20")
                                        )
                                )
                        )
                )
                .add(
                        new BindingElement(
                                "MEMBERS_Advertiser_",
                                new LevelElement("Advertiser","Advertiser Account")
                        )
                )
                .add(
                        new BindingElement(
                                "MEMBERS_Measures_",
                                new DataSetElement(
                                        new MemberElement("Measures", "imps"),
                                        new MemberElement("Measures", "clicks"),
                                        new MemberElement("Measures", "requests"),
                                        new MemberElement("Measures", "Campaign Monthly Unique Users"),
                                        new MemberElement("Measures", "Campaign Weekly Unique Users")
                                )
                        )
                )
        ;

        AxisesElement axisesElement = generator.getAxisesElement();

        axisesElement.getColumns()
                .add(new MemberElement("MEMBERS_Measures_"));

        axisesElement.getRows()
                .add(
                        new FunctionElement(
                                "NONEMPTYCROSSJOIN",
                                new MemberElement("MEMBERS_TimeDimension_"),
                                new MemberElement("MEMBERS_Advertiser_")
                        )
                );

        generator.getFilterElement()
                .add(new MemberElement("ISP", "RU"));

        Assert.assertEquals("Mdx generation failed", MDX1, generator.toMdx().replace('\n', ' ').replace('\t', ' '));
    }

    @Test
    public void identifierNodeTest() {
        OlapIdentifier identifier = OlapIdentifier.parse("[dimension.hierarchy].[level1].[name].&[value]");
        System.out.println(identifier.toIdentifierNode());
    }
}
