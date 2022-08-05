package com.foros.session.admin.kwmTool;

import com.foros.util.NameValuePair;
import com.foros.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class KWMToolResult {
    private static final String TAG_OUTPUT_MODE = "output_mode";
    private String plainText;
    private String separatedWords;
    private List<NameValuePair<String, Long>> keywords;
    private List<String> fullText;
    private String originalFullText;
    private long averageTime;
    private KWMOutputMode mode;

    public KWMToolResult(String text, String separator, String absoluteConfigPath) throws KWMToolException {
        if (StringUtil.isPropertyEmpty(text)) {
            return;
        }

        mode = getMode(absoluteConfigPath);

        String[] splittedText = text.split(separator, -1);

        plainText = splittedText[0].trim();
        separatedWords = splittedText[1].trim();

        switch (mode) {
        case KWM:
            keywords = parseWeights(splittedText[2].trim());
            break;
        case FULL_TEXT:
            originalFullText = splittedText[2];
            break;
        }
        averageTime = Long.valueOf(splittedText[3].trim());
    }

    private KWMOutputMode getMode(String absoluteConfigPath) throws KWMToolException{
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new KWMToolException("error.configuration", e);
        }
        File fXmlFile = new File(absoluteConfigPath);
        try {
            Document doc = dBuilder.parse(fXmlFile);
            NodeList nodeList = doc.getElementsByTagName(TAG_OUTPUT_MODE);
            if (nodeList.getLength() > 0 ) {
                String value = nodeList.item(0).getTextContent().trim();
                boolean isFullText = KWMOutputMode.FULL_TEXT.name().equalsIgnoreCase(value);
                return isFullText ? KWMOutputMode.FULL_TEXT : KWMOutputMode.KWM;
            } else {
                return KWMOutputMode.KWM;
            }
        } catch (SAXException e) {
            throw new KWMToolException("error.configuration", e);
        } catch (IOException e) {
            throw new KWMToolException("error.configuration", e);
        }
    }

    public String getPlainText() {
        return plainText;
    }

    public String getSeparatedWords() {
        return separatedWords;
    }

    public List<NameValuePair<String, Long>> getKeywords() {
        return keywords;
    }

    public List<String> getFullText() {
        if (fullText == null) {
            fullText = Arrays.asList(StringUtil.splitAndTrim(originalFullText));
        }
        return fullText;
    }

    public String getOriginalFullText() {
        return originalFullText;
    }

    public long getAverageTime() {
        return averageTime;
    }

    private List<NameValuePair<String, Long>> parseWeights(String rawKeywords) {
        String[] lines = StringUtil.splitAndTrim(rawKeywords);
        List<NameValuePair<String, Long>> result = new ArrayList<NameValuePair<String, Long>>(lines.length);

        for (String line : lines) {
            int lastSpace = line.lastIndexOf(' ');
            String word = line.substring(0, lastSpace);
            Long weight = Long.valueOf(line.substring(lastSpace + 1));
            result.add(new NameValuePair<String, Long>(word, weight));
        }

        return result;
    }

    public KWMOutputMode getMode() {
        return mode;
    }
}
