package com.foros.config;

import com.foros.security.AccountRole;
import com.foros.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

public class AccountRolesByHostConfigParameter extends AbstractConfigParameter<Set<AccountRole>> {

    private static final Logger logger = Logger.getLogger(AccountRolesByHostConfigParameter.class.getName());

    public AccountRolesByHostConfigParameter(String postfix, AccountRole... defaultValue) {
        super(getConfiguredHostname() + postfix, new HashSet<AccountRole>(Arrays.asList(defaultValue)), false);
    }

    @Override
    public Class<Set<AccountRole>> getType() {
        return (Class) Set.class;
    }

    @Override
    public Set<AccountRole> parse(String str) {
        String[] rolesNames = StringUtil.splitByComma(str);

        Set<AccountRole> roles = new HashSet<AccountRole>();
        for (String name : rolesNames) {
            roles.add(AccountRole.byName(name));
        }

        return roles;
    }

    private static String getConfiguredHostname() {
        try {
            Process hostnameProcess = Runtime.getRuntime().exec("hostname");
            String result = IOUtils.toString(hostnameProcess.getInputStream());
            int exitCode = hostnameProcess.waitFor();

            if (exitCode != 0) {
                String errorMessage = "Failed to execute 'hostname' utility: "
                        + IOUtils.toString(hostnameProcess.getErrorStream())
                        + ": exit code: " + exitCode;

                throw new RuntimeException(errorMessage);
            }

            logger.log(Level.INFO, "Fetched hostname: {0}", new Object[]{result});

            return result.trim();
        } catch (Throwable t) {
            throw new RuntimeException("Can't get 'hostname'. ", t);
        }

    }
}
