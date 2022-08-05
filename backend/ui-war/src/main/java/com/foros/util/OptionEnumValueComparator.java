package com.foros.util;

import com.foros.model.template.OptionEnumValue;

import java.util.Comparator;


/**
 * Issue: https://jira.ocslab.com/browse/OUI-19215
 * 
 * The comparator is used to sort OptionEnumValue by OptionEnumValue.value 
 * in accordance with {@link}StringOptionEnumValueComparator. 
 * 
 * @author igor_kuksov
 *
 */
public class OptionEnumValueComparator implements Comparator<OptionEnumValue>{

    private StringOptionEnumValueComparator stringComparator = new StringOptionEnumValueComparator();

    @Override
    public int compare(OptionEnumValue o1, OptionEnumValue o2) {

        return stringComparator.compare(o1.getValue(), o2.getValue());
    }

}
