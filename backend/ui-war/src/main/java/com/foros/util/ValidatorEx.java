package com.foros.util;


public class ValidatorEx {

    public static boolean validateEmail(String value) {
        boolean isValid = true;

        int atIndex = value.lastIndexOf("@");
        if (atIndex == -1) {
            isValid = false;
        } else {
            String domain = value.substring(atIndex + 1);
            String local = value.substring(0, atIndex);
            int localLen = local.length();
            int domainLen = domain.length();
            if (localLen < 1 || localLen > 64) {
                // local part length exceeded
                isValid = false;
            } else if (domainLen < 1 || domainLen > 255) {
                // domain part length exceeded
                isValid = false;
            } else if (local.charAt(0) == '.' || local.charAt(localLen - 1) == '.') {
                // local part starts or ends with '.'
                isValid = false;
            } else if (local.indexOf("..") != -1) {
                // local part has two consecutive dots
                isValid = false;
            } else if (!domain.matches("^[A-Za-z0-9\\-\\.]+$")) {
                // character not valid in domain part
                isValid = false;
            } else if (domain.indexOf("..") != -1) {
                // domain part has two consecutive dots
                isValid = false;
            } else if (domain.matches(".*[A-Za-z0-9\\-]{64,}.*")) {
                // domain labels length greater than 63 characters
                isValid = false;
            } else if (!local.replaceAll("\\\\\\\\", "").matches("^(\\\\.|[A-Za-z0-9!#%&`_=\\/$\'*+?^{}|~.-])+$")) {
                // character not valid in local part unless
                // local part is quoted
                if (!local.replaceAll("\\\\\\\\", "").matches("^\"(\\\"|[^\"])+\"$")) {
                    isValid = false;
                }
            }


/*          Do we need check if domain realy exists?

//          if (isValid && !(checkdnsrr($domain,"MX") || checkdnsrr($domain,"A")))
//          {
//              // domain not found in DNS
//              isValid = false;
//          } */
        }
        return isValid;
    }
}
