package com.foros.action;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.foros.AbstractUnitTest;
import com.foros.util.StringUtil;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TextProviderSupportTest extends AbstractUnitTest {
    @Test
    @Category(Unit.class)
    public void getText() {
        doTest(Locale.UK, "FieldName is required");
        doTest(new Locale("ru", "RU"), "\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435 \u043f\u043e\u043b\u044f \"FieldName\" \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f \u043e\u0431\u044f\u0437\u0430\u0442\u0435\u043b\u044c\u043d\u044b\u043c \u0434\u043b\u044f \u0437\u0430\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f");
    }

    private void doTest(final Locale locale, String localizedMessage) {
        LocaleProvider lp = new LocaleProvider() {
            @Override
            public Locale getLocale() {
                return locale;
            }
        };

        TextProvider tested = new TextProviderSupport(lp);

        List<Object> args = Arrays.asList((Object)"FieldName");
        String originalStr;
        String testedStr;

        // String getText(String key, List args)
        originalStr = localizedMessage;
        testedStr = tested.getText("errors.required", args);
        assertEquals(originalStr, testedStr);

        //String getText(String key, String defaultValue, List args);
        originalStr = localizedMessage;
        testedStr = tested.getText("errors.required", "__default__", args);
        assertEquals(originalStr, testedStr);

        originalStr = "__default__";
        testedStr = tested.getText("__wrong__", "__default__", args);
        assertEquals(originalStr, testedStr);

        // With single quote! 
        originalStr = StringUtil.getLocalizedString("error.statusCollision.FORCE_APPROVE", locale);
        testedStr = tested.getText("error.statusCollision.FORCE_APPROVE");
        assertEquals(originalStr, testedStr);

        originalStr = StringUtil.getLocalizedString("errors.site.status.invalid", locale, "Status");
        testedStr = tested.getText("errors.site.status.invalid", new String[] {"Status"});
        assertEquals(originalStr, testedStr);
    }
}
