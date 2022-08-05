package com.foros.util.preview;


import static com.foros.util.preview.SubstitutionTemplateParser.PREFIX;
import static com.foros.util.preview.SubstitutionTemplateParser.SUBSTITUTION;
import static com.foros.util.preview.SubstitutionTemplateParser.SUBSTITUTION_CONTENT;
import static com.foros.util.preview.SubstitutionTemplateParser.TEMPLATE;
import static com.foros.util.preview.SubstitutionTemplateParser.TEXT;
import static com.foros.util.preview.SubstitutionTemplateParser.TOKEN;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;

public class ParsecSubstitutionTemplateParserTest extends Assert {

    @Test
    public void testTemplate() {
        assertEquals(
                Collections.singletonList(Substitution.ptd("url", "TOKEN", "some text")),
                TEMPLATE.parse("##url:TOKEN=some text##")
        );

        assertEquals(
                Arrays.asList(
                        Substitution.ptd("url", "TOKEN", "some text"),
                        " <other text=''/>"
                ),
                TEMPLATE.parse("##url:TOKEN=some text## <other text=''/>")
        );
        assertEquals(
                Arrays.asList(
                        Substitution.ptd("url", "TOKEN1", "some text"),
                        " <other text=''/> ",
                        Substitution.td("TOKEN2", "awesome text"),
                        Substitution.td("TOKEN3", null),
                        " <another text=''/>"
                ),
                TEMPLATE.parse("##url:TOKEN1=some text## <other text=''/> ##TOKEN2=awesome text####TOKEN3## <another text=''/>")
        );
        assertEquals(
                Arrays.asList(
                        "#", "#",
                        Substitution.ptd("url", "TOKEN", "some text"),
                        "#", " #<other text='", "#", "#", "'/>"
                ),
                TEMPLATE.parse("####url:TOKEN=some text### #<other text='##'/>")
        );
        assertEquals(
                Arrays.asList(
                        "#",
                        Substitution.t("RANDOM")
                ),
                TEMPLATE.parse("###RANDOM##")
        );

        assertEquals(
                Arrays.asList(
                        Substitution.t("RANDOM"),
                        " test ",
                        Substitution.pt("mime-url", "CLICK"),
                        " template"
                ),
                TEMPLATE.parse("##RANDOM## test ##mime-url:CLICK## template")
        );

        assertEquals(Collections.emptyList(), TEMPLATE.parse(""));
    }

    @Test
    public void testBasic() {
        assertEquals("TOKEN", TOKEN.parse("TOKEN"));
        assertEquals("url", PREFIX.parse("url:"));
        assertEquals("some text", TEXT.parse("some text"));
    }

    @Test
    public void testSubstitution() {
        assertEquals(Substitution.ptd("url", "TOKEN", "some text"), SUBSTITUTION_CONTENT.parse("url:TOKEN=some text"));
        assertEquals(Substitution.ptd("url", "TOKEN", null), SUBSTITUTION_CONTENT.parse("url:TOKEN"));
        assertEquals(Substitution.td("TOKEN", null), SUBSTITUTION_CONTENT.parse("TOKEN"));
        assertEquals(Substitution.td("TOKEN", ""), SUBSTITUTION_CONTENT.parse("TOKEN="));
        assertEquals(Substitution.ptd("url", "TOKEN", ""), SUBSTITUTION_CONTENT.parse("url:TOKEN="));

        assertEquals(Substitution.ptd("url", "TOKEN", "some text"), SUBSTITUTION.parse("##url:TOKEN=some text##"));
    }


}