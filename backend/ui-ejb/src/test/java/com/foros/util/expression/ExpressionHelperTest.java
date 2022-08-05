package com.foros.util.expression;

import com.foros.session.channel.exceptions.ExpressionConversionException;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.phorm.oix.util.expression.CDMLParsingError;

@Category( Unit.class )
public class ExpressionHelperTest {
    @Test
    public void extractChannels() {
        Collection<Long> expResult = new HashSet<Long>();
        expResult.add(23321L);
        expResult.add(23322L);
        expResult.add(23323L);
        expResult.add(23324L);
        expResult.add(3321L);
        expResult.add(3322L);
        expResult.add(2332L);
        expResult.add(2324L);

        String expr = "23321^23322&23323|23324&(23321&23322)|(3321|3322^2332|2324)";
        Collection<Long> result = ExpressionHelper.extractChannels(expr);
        assertEquals(expResult, result);
    }

    @Test
    public void extractChannelsFromEmptyExpression() {
        //null
        Collection<Long> expResult = new HashSet<Long>();
        Collection<Long> result = ExpressionHelper.extractChannels(null);
        assertEquals(expResult, result);

        //empty
        result = ExpressionHelper.extractChannels("");
        assertTrue(result.size() == 0);
    }

    @Test
    public void parseNames() throws ExpressionConversionException, IOException, CDMLParsingError {
        Collection<String> expResult = new HashSet<String>();
        expResult.add("channel name 1");
        expResult.add("channel name 2");
        expResult.add("channel name 3");
        expResult.add("channel name 4");
        expResult.add("channel name 5");

        String text =
                "([channel name 1] and_not [channel name 2] AND [channel name 5] " +
                        "OR [channel name 1] and ([channel name 3] AND [channel name 4]))";
        Collection<String> result = ExpressionHelper.parseNames(text);
        assertEquals(expResult, result);
    }

    @Test
    public void validExpressions() throws IOException, CDMLParsingError {
        ExpressionHelper.parseNames("");

        ExpressionHelper.parseNames(" ([Auto New/Custom SSang Yong RespRep] AND " +
                "[Auto New/Custom DriversLoan]) AND_NOT [Auto New/Custom SSang Yong RespRep_#1] " +
                "AND_NOT [Auto New/CustomBudgetMotor]");

        ExpressionHelper.parseNames("[Auto Ne33222##@!@!!!w/Custom*&^&^%^&&**( SSang Yong RespRep] AND " +
                "[Auto New/*(&^&&*&%$%Custom #$@@DriversLoan]");

        ExpressionHelper.parseNames("\n [Test/AdServer/CDML2/Primary/test 1] \n AND \n[Test/AdServer/CDML2/Primary/test 2]\n\n\n");
    }

    @Test
    public void invalidExpressions() throws IOException {
        checkInvalidExpression("[werere ere]()");
        checkInvalidExpression("[werere ere] []");
        checkInvalidExpression("()");
        checkInvalidExpression(")");
        checkInvalidExpression("(");
        checkInvalidExpression("[werere ere] [werere ere]");
        checkInvalidExpression("or[werere ere]");
        checkInvalidExpression("or [werere ere] ()");
        checkInvalidExpression("and");
        checkInvalidExpression("([1f ef efe]and[2]and_not[3])OR[1]and_not");
        checkInvalidExpression("([werere ere])or(and[edfewfe]and_not[rrrr gfe])");
    }

    @Test
    public void parseAccountName() {
        String channelName = "Account name" + ExpressionHelper.ACCOUNT_NAME_DELIMITER + "Channel name";
        String expResult = "Account name";
        String result = ExpressionHelper.parseAccountName(channelName);
        assertEquals(expResult, result);

        channelName = "Channel name";
        expResult = null;
        result = ExpressionHelper.parseAccountName(channelName);
        assertEquals(expResult, result);
    }

    @Test
    public void parseChannelName() {
        String channelName = "Account name" + ExpressionHelper.ACCOUNT_NAME_DELIMITER + "/Channel/name";
        String expResult = "/Channel/name";
        String result = ExpressionHelper.parseChannelName(channelName);
        assertEquals(expResult, result);

        channelName = "Channel name";
        expResult = "Channel name";
        result = ExpressionHelper.parseChannelName(channelName);
        assertEquals(expResult, result);
    }

    private void checkInvalidExpression(String expr) throws IOException {
        try {
            ExpressionHelper.parseNames(expr);
            fail("There is must be an exception for the expression: " + expr);
        } catch (Exception e) {
            // all ok
        }
    }
}
