package app.programmatic.ui.geo.validation;

import com.foros.rs.client.model.geo.RadiusUnit;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.geo.dao.model.Address;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class AddressValidationServiceImpl implements ConstraintValidator<ValidateAddress, Address> {
    @Override
    public void initialize(ValidateAddress constraintAnnotation) {
    }

    @Override
    public boolean isValid(Address address, ConstraintValidatorContext context) {
        ConstraintViolationBuilder<Address> builder = new ConstraintViolationBuilder<>();

        switch (address.getRadiusUnits()) {
            case m:
                if (address.getRadius() < 50 || address.getRadius() > 50000) {
                    builder.addViolationDescription("radius", "entity.field.error.notInRangeExclusive", 50, 50000);
                }
                break;
            case km:
                if (address.getRadius() < 1 || address.getRadius() > 50) {
                    builder.addViolationDescription("radius", "entity.field.error.notInRangeExclusive", 1, 50);
                }
                break;
            default:
                builder.addViolationDescription("radiusUnits", "entity.field.error.oneOf", new RadiusUnit[]{RadiusUnit.m, RadiusUnit.km});
                break;
        }

        return builder.buildAndPushToContext(context).isValid();
    }
}
