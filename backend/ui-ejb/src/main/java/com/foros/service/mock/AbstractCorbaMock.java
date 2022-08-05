package com.foros.service.mock;

import org.omg.CORBA.*;

/**
 * Does nothing, just empty implementation.
 */
public class AbstractCorbaMock {

    public boolean _is_a(String repositoryIdentifier) {
        return false;
    }

    public boolean _is_equivalent(org.omg.CORBA.Object other) {
        return false;
    }

    public boolean _non_existent() {
        return false;
    }

    public int _hash(int maximum) {
        return 0;
    }

    public org.omg.CORBA.Object _duplicate() {
        return null;
    }

    public void _release() {
    }

    public org.omg.CORBA.Object _get_interface_def() {
        return null;
    }

    public Request _request(String operation) {
        return null;
    }

    public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result) {
        return null;
    }

    public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result, ExceptionList exclist, ContextList ctxlist) {
        return null;
    }

    public Policy _get_policy(int policy_type) {
        return null;
    }

    public DomainManager[] _get_domain_managers() {
        return new DomainManager[0];
    }

    public org.omg.CORBA.Object _set_policy_override(Policy[] policies, SetOverrideType set_add) {
        return null;
    }
}
