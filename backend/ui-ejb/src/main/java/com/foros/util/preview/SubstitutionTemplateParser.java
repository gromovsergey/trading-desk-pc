package com.foros.util.preview;

import java.util.List;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.misc.Mapper;
import org.codehaus.jparsec.pattern.CharPredicate;
import org.codehaus.jparsec.pattern.CharPredicates;
import org.codehaus.jparsec.pattern.Pattern;
import org.codehaus.jparsec.pattern.Patterns;

public class SubstitutionTemplateParser {

    static final CharPredicate IS_IDENTIFIER_CHAR = CharPredicates.notAmong("#:=");
    static final Parser DOUBLE_HASH = Scanners.string("##");
    static final Pattern NOT_DOUBLE_HASH = new Pattern() {
        @Override
        public int match(CharSequence src, int begin, int end) {
            if (begin == end - 1) return 1;
            if (begin >= end) return MISMATCH;
            if (src.charAt(begin) == '#' && src.charAt(begin + 1) == '#') return MISMATCH;
            return 1;
        }
    };

    static final Pattern IDENTIFIER = Patterns.isChar(IS_IDENTIFIER_CHAR).many1();
    static final Parser PREFIX = IDENTIFIER.toScanner("prefix").source().followedBy(Scanners.string(":"));
    static final Parser TOKEN = IDENTIFIER.toScanner("token").source();
    static final Parser<String> TEXT = Scanners.notChar('#').next(NOT_DOUBLE_HASH.many().toScanner("text")).source();
    static final Parser DEFAULT_VALUE = Scanners.string("=").next(TEXT.optional(""));
    static final Parser<Substitution> SUBSTITUTION_CONTENT = Mapper.curry(Substitution.class)
            .sequence(
                    PREFIX.atomic().optional(),
                    TOKEN,
                    DEFAULT_VALUE.atomic().optional()
            );
    static final Parser<Substitution> SUBSTITUTION = Parsers.between(DOUBLE_HASH, SUBSTITUTION_CONTENT, DOUBLE_HASH);
    static final Parser<Object> SUBSTITUTION_OR_HASH = Parsers.between(DOUBLE_HASH, TEXT, DOUBLE_HASH).peek()
            .ifelse(
                    SUBSTITUTION,
                    Scanners.isChar('#').source()
            );
    static final Parser<List<Object>> TEMPLATE = Parsers.or(
            SUBSTITUTION_OR_HASH,
            TEXT
    ).many();

    public static SubstitutionTemplate parse(String templateStr) {
        List<Object> parse = TEMPLATE.parse(templateStr);
        return new SubstitutionTemplate(parse, templateStr.length() * 2);
    }
}
