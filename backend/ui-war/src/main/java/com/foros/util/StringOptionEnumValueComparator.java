package com.foros.util;

import java.util.Comparator;

/**
 * Issue: OUI-19215
 * 
 * This comparator is used to sort Strings that begin from digits in natural integer order, 
 * while the remaining part of the String is sorted in String natural order. 
 * 
 * Example: 
 * 
 * 1. {"10px", "7px", "11px", "none", "1px"} is sorted to {"1px", "7px", "10px", "11px", "none"}
 * 2. {"1abc", "10abc", "1bcd", "012abc", "abc" ,"bcd"} is sorted to {"1abc", "1bcd", "10abc", "012abc", "abc", "bcd"}
 * 
 * @author igor_kuksov
 *
 */

public class StringOptionEnumValueComparator implements Comparator<String>{

        @Override
        public int compare(String o1, String o2) {
        int i1 = this.getLeadingInt(o1);
        int i2 = getLeadingInt(o2);
        String s1 = getTrailingString(o1);
        String s2 = getTrailingString(o2);
        
        if(i1==i2)
             return s1.compareTo(s2);
        if(i1>i2)
             return 1;
        else if(i1<i2)
                return -1;
        return 0;
        }

        private int getLeadingInt(String s) {
        s=s.trim();
        int i=Integer.MAX_VALUE;
        try {
                 i = Integer.parseInt(s.split("[^0-9]+")[0]);
        } catch(ArrayIndexOutOfBoundsException e) {
                
        } catch(NumberFormatException f) {
                return i;
        }
        
        return i;
        }

        private String getTrailingString(String s) {
        
        return  s.replaceFirst("[0-9]", "");
}
}
