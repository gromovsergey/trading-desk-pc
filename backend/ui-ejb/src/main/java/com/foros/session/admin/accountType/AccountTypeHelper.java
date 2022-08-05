package com.foros.session.admin.accountType;

import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.template.Template;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


public class AccountTypeHelper {

    public static  Set<CreativeSize> getNonTextCreativeSizes(AccountType accountType) {
        if (accountType == null) {
            return new LinkedHashSet<CreativeSize>();
        }

        Set<CreativeSize> creativeSizes = accountType.getCreativeSizes();
        Iterator<CreativeSize> iter = creativeSizes.iterator();
        while (iter.hasNext()) {
            CreativeSize creativeSize = iter.next();
            if (creativeSize.isText()) {
                iter.remove();
            }
        }

        return creativeSizes;
    }

    public static Set<Template> getNonTextCreativeTemplates(AccountType accountType) {
        if (accountType == null) {
            return  new LinkedHashSet<Template>();
        }

        Set<Template> templates = accountType.getTemplates();
        Iterator<Template> iter = templates.iterator();
        while (iter.hasNext()) {
            Template template = iter.next();
            if (template.isText()) {
                iter.remove();
            }
        }

        return templates;
    }

}
