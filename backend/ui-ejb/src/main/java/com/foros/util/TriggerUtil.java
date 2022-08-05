package com.foros.util;

import com.foros.model.channel.trigger.TriggerType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TriggerUtil {
    public static final Pattern NEWLINE_PATTERN = Pattern.compile("\r\n|\n");
    public static final int MAX_KEYWORD_LENGTH = 512;
    public static final int MAX_URL_LENGTH = 2048;

    public static boolean isWildCard(TriggerType type, String trigger) {
        return TriggerType.URL.equals(type) && !trigger.startsWith("\"");
    }

    public static List<String> splitPhrase(String phrase) {
        return new PhraseParser().parse(phrase);
    }

    private static class PhraseParser {
        private List<String> phrases;
        private StringBuffer currentWord;

        public List<String> parse(String phrase) {
            if (phrase == null || phrase.trim().length() == 0) {
                return new ArrayList<String>(0);
            }

            this.currentWord = new StringBuffer();
            this.phrases = new ArrayList<String>();
            boolean quoted = false;

            for (char c : phrase.toCharArray()) {
                if (c == '-' && currentWord.length() == 0) {
                    continue;
                } else if (c == '\"') {
                    if (!quoted) {
                        appendWord();
                        currentWord.append(c);
                    } else {
                        currentWord.append(c);
                        appendWord();
                    }
                    quoted = !quoted;
                } else if (c == ' ' && !quoted) {
                    appendWord();
                } else if (c == '[' || c ==']') {
                    // '[' and ']' have no special meaning in trigger qa search
                    // it should like any other non-letter character
                    // but they are special chars for normalization routine so we replace it with '!'
                    currentWord.append('!');
                } else {
                    currentWord.append(c);
                }
            }
            if (quoted) {
                this.phrases.addAll(new PhraseParser().parse(this.currentWord.deleteCharAt(0).toString()));
            } else {
                appendWord();
            }
            return phrases;
        }

        private void appendWord() {
            String word = currentWord.toString().trim();
            if (word.length() > 0) {
                phrases.add(word);
            }
            currentWord = new StringBuffer();
        }
    }
}
