package com.foros.util;

import com.foros.security.AccountRole;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ContextUrlHelper {
    private ContextUrlHelper() {
    }

    public static Set<AccountRole> getContextAccountRoles(String path) {
        if (StringUtil.startsWith(path, UIConstants.PATH_ADVERTISER)) {
            return new HashSet<AccountRole>(Arrays.asList(AccountRole.ADVERTISER, AccountRole.AGENCY));
        } else if (StringUtil.startsWith(path, UIConstants.PATH_PUBLISHER)) {
            return Collections.singleton(AccountRole.PUBLISHER);
        } else if (StringUtil.startsWith(path, UIConstants.PATH_ISP)) {
            return Collections.singleton(AccountRole.ISP);
        } else if (StringUtil.startsWith(path, UIConstants.PATH_CMP)) {
            return Collections.singleton(AccountRole.CMP);
        } else {
            return Collections.singleton(AccountRole.INTERNAL);
        }
    }
}
