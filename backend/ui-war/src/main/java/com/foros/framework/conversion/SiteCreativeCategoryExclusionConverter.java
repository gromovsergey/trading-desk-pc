package com.foros.framework.conversion;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import com.foros.model.site.CategoryExclusionApproval;

import java.util.Map;

public class SiteCreativeCategoryExclusionConverter extends SingleValueBaseTypeConverter {
    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        try {
            return CategoryExclusionApproval.valueOf(value.charAt(0));
        } catch (Exception e) {
            throw new TypeConversionException(e);
        }
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        if (!(o instanceof CategoryExclusionApproval)) {
            throw new TypeConversionException("Object " + o + " is not an CategoryExclusionApproval");
        }

        return Character.toString(((CategoryExclusionApproval)o).getLetter());
    }
}
