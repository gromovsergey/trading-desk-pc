package com.foros.validation.code;

import com.foros.validation.interpolator.ForosErrorResolver;
import com.foros.validation.interpolator.MessageTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceKeyErrorResolver implements ForosErrorResolver {

    public static final ForosErrorResolver INSTANCE = new ResourceKeyErrorResolver(BusinessErrors.MAP_OF_ERRORS);

    private final Map<String, ForosError> errorByKey;

    public ResourceKeyErrorResolver(Map<ForosError, List<String>> keysByError) {
        errorByKey = new HashMap<String, ForosError>();
        for (Map.Entry<ForosError, List<String>> entry : keysByError.entrySet()) {
            List<String> keys = entry.getValue();
            ForosError error = entry.getKey();
            for (String key : keys) {
                errorByKey.put(key, error);
            }
        }
    }

    @Override
    public ForosError resolve(MessageTemplate template) {
        return resolve(template.getTemplate());
    }

    @Override
    public ForosError resolve(String template) {
        ForosError error = errorByKey.get(template);
        return error == null ? BusinessErrors.GENERAL_ERROR : error;
    }
}
