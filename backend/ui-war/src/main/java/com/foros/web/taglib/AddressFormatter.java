package com.foros.web.taglib;

import com.foros.model.AddressField;
import com.foros.model.security.AccountAddress;
import com.foros.session.admin.country.CountryService;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AddressFormatter {
    
    private AddressFormatter() {
    }

    public static String format(AccountAddress address, Collection<AddressField> addressFields) {
        StringBuilder addressText = new StringBuilder();
        List<AddressField> list = new ArrayList<AddressField>(addressFields);
        Collections.sort(list);

        List<String> fieldValues = new LinkedList<String>();
        for (AddressField field : list) {
            if (field.isEnabled()) {
                if (field.getOFFieldName().equals(CountryService.PredefinedAddressField.COUNTRY.getName())) {
                    continue;
                }

                String fieldValue = getCorrespondingFieldValue(address, field);
                if (field.isMandatory() || StringUtil.isPropertyNotEmpty(fieldValue)) {
                    fieldValues.add(fieldValue);
                }
            }
        }
        int i = 0;
        int fieldsCount = fieldValues.size();
        for (String fieldValue : fieldValues) {
            addressText.append(fieldValue);
            if (i < fieldsCount - 1) {
                addressText.append(", ");
            }
            i++;
        }
        return addressText.toString();
    }

    private static String getCorrespondingFieldValue(AccountAddress address, AddressField field) {
        String fieldValue;
        if (field.getOFFieldName().equals(CountryService.PredefinedAddressField.LINE1.getName())) {
            fieldValue = address.getLine1();
        } else if (field.getOFFieldName().equals(CountryService.PredefinedAddressField.LINE2.getName())) {
            fieldValue = address.getLine2();
        } else if (field.getOFFieldName().equals(CountryService.PredefinedAddressField.LINE3.getName())) {
            fieldValue = address.getLine3();
        } else if (field.getOFFieldName().equals(CountryService.PredefinedAddressField.CITY.getName())) {
            fieldValue = address.getCity();
        } else if (field.getOFFieldName().equals(CountryService.PredefinedAddressField.STATE.getName())) {
            fieldValue = address.getState();
        } else if (field.getOFFieldName().equals(CountryService.PredefinedAddressField.PROVINCE.getName())) {
            fieldValue = address.getProvince();
        } else if (field.getOFFieldName().equals(CountryService.PredefinedAddressField.ZIP.getName())) {
            fieldValue = address.getZip();
        } else {
            throw new IllegalStateException("Wrong OF field name: +" + field.getOFFieldName() + "!");
        }
        if (fieldValue == null) {
            fieldValue = "";
        }
        return fieldValue;
    }

}
