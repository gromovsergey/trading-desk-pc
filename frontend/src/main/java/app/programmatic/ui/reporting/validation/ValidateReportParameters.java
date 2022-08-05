package app.programmatic.ui.reporting.validation;

import app.programmatic.ui.reporting.advertiser.AdvertiserReportValidationServiceImpl;
import app.programmatic.ui.reporting.conversions.ConversionsReportValidationServiceImpl;
import app.programmatic.ui.reporting.detailed.DetailedReportValidationServiceImpl;
import app.programmatic.ui.reporting.domains.DomainsReportValidationServiceImpl;
import app.programmatic.ui.reporting.publisher.PublisherReportValidationServiceImpl;
import app.programmatic.ui.reporting.referrer.ReferrerReportValidationServiceImpl;
import app.programmatic.ui.reporting.segments.SegmentsReportValidationServiceImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {
        AdvertiserReportValidationServiceImpl.class,
        ConversionsReportValidationServiceImpl.class,
        DomainsReportValidationServiceImpl.class,
        PublisherReportValidationServiceImpl.class,
        ReferrerReportValidationServiceImpl.class,
        DetailedReportValidationServiceImpl.class,
        SegmentsReportValidationServiceImpl.class
})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER})
@NotNull
public @interface ValidateReportParameters {
    String value() default "";

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
