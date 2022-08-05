package app.programmatic.ui.creative.validation;

import app.programmatic.ui.common.validation.forosApiViolation.ForosApiPathMapper;
import app.programmatic.ui.common.validation.forosApiViolation.PathMapperResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ApiOptionsMapper implements ForosApiPathMapper {
    private final Pattern OPTIONS_REGEX = Pattern.compile("(options)\\[(\\d+)\\](.*)");

    @Override
    public PathMapperResult map(String originalPath, Object[] methodArgs) {
        Matcher matcher = OPTIONS_REGEX.matcher(originalPath);
        if (matcher.matches()) {
            return new PathMapperResult(matcher.group(1) + "." + matcher.group(2) + matcher.group(3), true);
        }

        return new PathMapperResult(originalPath, false);
    }
}
