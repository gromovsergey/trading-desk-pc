package app.programmatic.ui.common.validation.forosApiViolation.mappers;

import app.programmatic.ui.common.validation.forosApiViolation.ForosApiPathMapper;
import app.programmatic.ui.common.validation.forosApiViolation.PathMapperResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EntityIdMapper implements ForosApiPathMapper {
    private static final Pattern matchingRegex = Pattern.compile("(.*)([a-z]+\\w+)(\\.id)(.*)");

    @Override
    public PathMapperResult map(String originalPath, Object[] methodArgs) {
        Matcher matcher = matchingRegex.matcher(originalPath);
        if (matcher.matches()) {
            return new PathMapperResult(matcher.group(1) + matcher.group(2) + "Id" + matcher.group(4), true);
        }
        return new PathMapperResult(originalPath, false);
    }
}

