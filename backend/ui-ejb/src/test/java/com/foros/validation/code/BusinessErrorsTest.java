package com.foros.validation.code;

import com.foros.model.security.Language;

import group.Resource;
import group.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import resource.ResourceMatchTest;

@Category({ Unit.class, Resource.class })
public class BusinessErrorsTest extends Assert {

    @Test
    public void testLinkedResources() {
        List<BusinessErrors> wrong = new ArrayList<BusinessErrors>();
        Map<ForosError, List<String>> mapOfErrors = BusinessErrors.MAP_OF_ERRORS;
        // used in code
        List<BusinessErrors> directlyUsed = Arrays.asList(
                BusinessErrors.CAMPAIGN_DELIVERY_PACING_DYNAMIC_NO_END_DATE
        );

        for (BusinessErrors errorCode : BusinessErrors.values()) {
            if (errorCode.getCode() % 1000 != 0
                    && (mapOfErrors.get(errorCode) == null || mapOfErrors.get(errorCode).isEmpty())) {
                if (!directlyUsed.contains(errorCode)) {
                    wrong.add(errorCode);
                    System.out.println(errorCode + " should have linked resources");
                }

            }
        }
        assertTrue(wrong.size() == 0);
    }

    @Test
    public void testUniqueCode() {
        List<BusinessErrors> wrong = new ArrayList<BusinessErrors>();
        Map<Integer, BusinessErrors> uniqueCodes = new HashMap<Integer, BusinessErrors>(BusinessErrors.values().length);
        for (BusinessErrors code: BusinessErrors.values()) {
            if (uniqueCodes.get(code.getCode()) != null) {
                wrong.add(code);
                System.out.println(code + " and " + uniqueCodes.get(code.getCode()) + " can't be have same codes");
            }

            uniqueCodes.put(code.getCode(), code);
        }

        assertTrue(wrong.size() == 0);
    }

    @Test
    public void testCodesStructure() {
        List<BusinessErrors> wrong = new ArrayList<BusinessErrors>();
        Map<Integer, BusinessErrors> uniqueCodes = new HashMap<Integer, BusinessErrors>(BusinessErrors.values().length);
        for (BusinessErrors code: BusinessErrors.values()) {
            if (code.getCode() % 1000 == 0 || code.getCode() % 100000 == 0) {
                uniqueCodes.put(code.getCode(), code);
            }
        }

        for (BusinessErrors code: BusinessErrors.values()) {
            int i = code.getCode() % 1000;
            int parentCode = code.getCode() - i;
            if (i != 0 && uniqueCodes.get(parentCode) == null) {
                wrong.add(code);
                System.out.println("We should have parent code:" + parentCode + "for " + code);
            }

            i = code.getCode() % 100000;
            parentCode = code.getCode() - i;
            if (i != 0 && uniqueCodes.get(parentCode) == null) {
                wrong.add(code);
                System.out.println("We should have parent code:" + parentCode + " for " + code);
            }

        }

        assertTrue(wrong.size() == 0);
    }

    @Test
    public void testCodesSorting() {
        final List<BusinessErrors> wrong = new ArrayList<BusinessErrors>();
        BusinessErrors previous = null;
        for (BusinessErrors code: BusinessErrors.values()) {
            if (previous != null && previous.getCode() > code.getCode()) {
                wrong.add(previous);
                System.out.println(previous + " has invalid sorting");
            }
            previous = code;
        }

        assertTrue(wrong.size() == 0);
    }

    @Test
    public void testCodesResources() throws Exception {
        List<String> wrong = new ArrayList<String>();
        List<String> properties = new ArrayList<String>(ResourceMatchTest.getPropertiesAsMap(Language.EN).keySet());
        for (BusinessErrors code: BusinessErrors.values()) {
            List<String> resources = BusinessErrors.MAP_OF_ERRORS.get(code);
            if (resources != null) {
                for (String resource : resources) {
                    if (!properties.contains(resource)) {
                        wrong.add(resource);
                        System.out.println("Linked resource :\"" + resource + "\" doesn't exist");
                    }
                }
            }
        }

        assertTrue(wrong.size() == 0);
    }

    @Test
    public void testUniqueResources() throws Exception {
        List<String> wrong = new ArrayList<String>();
        Set<String> processed = new HashSet<String>();
        for (List<String> resources : BusinessErrors.MAP_OF_ERRORS.values()) {
            for (String resource : resources) {
                if (processed.contains(resource)) {
                    wrong.add(resource);
                    System.out.println("Duplicate resource : " + resource);
                }
                processed.add(resource);
            }
        }
        assertTrue(wrong.size() == 0);
    }
}
