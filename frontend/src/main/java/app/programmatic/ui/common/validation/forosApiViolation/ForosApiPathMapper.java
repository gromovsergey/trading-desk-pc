package app.programmatic.ui.common.validation.forosApiViolation;

public interface ForosApiPathMapper {
    PathMapperResult map(String originalPath, Object[] methodArgs);
}
