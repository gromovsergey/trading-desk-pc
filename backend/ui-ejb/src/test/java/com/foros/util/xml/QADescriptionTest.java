package com.foros.util.xml;

import group.Jaxb;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

@Category({ Unit.class, Jaxb.class })
public class QADescriptionTest {
    @Test
    public void fromXML() {
        String samples[] = {
                "<text>simple text</text>",
                "<channelTriggerQA/>",
                "<channelTriggerQA><trigger>abc</trigger><trigger>xyz</trigger></channelTriggerQA>",
                "<channelMinUrlTriggerThreshold><threshold>123</threshold><value>789</value></channelMinUrlTriggerThreshold>",
                "<channelMaxUrlTriggerShare><threshold>123</threshold><value>789</value><group>abc</group><trigger>xyz</trigger></channelMaxUrlTriggerShare>"
        };

        String messages = "";
        for (String sample : samples) {
            QADescription description = QADescriptionHelper.fromXML(sample);
            if (description == null) {
                messages += "\n" + decorate(sample) + " can not be converted into QADescription";
            }
        }
        assertTrue("Has errors:" + messages, messages.isEmpty());
    }

    @Test
    public void toXML() {
        QADescriptionText qaDescription = new QADescriptionText();
        qaDescription.setText("'\"<>&");
        String xml = QADescriptionHelper.toXML(qaDescription);
        assertTrue("Wrong XML: " + xml, "<text>'\"&lt;&gt;&amp;</text>".equals(xml));
    }

    private String decorate(String xml) {
        return xml == null || xml.isEmpty() ? "[empty-xml]" : xml;
    }
}
