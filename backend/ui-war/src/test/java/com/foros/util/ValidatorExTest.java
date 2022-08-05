package com.foros.util;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ValidatorExTest {
    @Test
    @Category(Unit.class)
    public void validateEmail() {
        // Valid addresses
        assertTrue("Simple address", ValidatorEx.validateEmail("steve_jobs@ocslab.com"));
        assertTrue("Quoted space", ValidatorEx.validateEmail("\"steve jobs\"@ocslab.com"));
        assertTrue("Quoted @", ValidatorEx.validateEmail("steve\\@jobs@ocslab.com"));
        assertTrue("Quoted @ and space", ValidatorEx.validateEmail("\"steve @jobs\"@ocslab.com"));
        assertTrue("Quoted with \\ invalid charactrer", ValidatorEx.validateEmail("ab\\&@ocslab.com"));
        assertTrue("Quoted invalid character", ValidatorEx.validateEmail("\"ab&\"@ocslab.com"));

        // Invalid addresses
        assertFalse("No local part", ValidatorEx.validateEmail("ocslab.com"));
        assertFalse("Local part ends with dot", ValidatorEx.validateEmail("steve.@ocslab.com"));
        assertFalse("Two consecutive dots in local part", ValidatorEx.validateEmail("steve..jobs@ocslab.com"));
        assertFalse("Local part starts with dot", ValidatorEx.validateEmail(".steve@ocslab.com"));
        assertFalse("@ in domain part", ValidatorEx.validateEmail("test@ocs@lab.com"));
        assertFalse("Two consecutive dots in domain part", ValidatorEx.validateEmail("test@ocslab..com"));
        assertFalse("@ in domain part, even quoted", ValidatorEx.validateEmail("test@ocs\\@lab.com"));
        assertFalse("Space in local part", ValidatorEx.validateEmail("a b@ocslab.com"));
        assertFalse("Invalid character in domain part", ValidatorEx.validateEmail("ab@&ocslab.com"));
        assertFalse("Domain label length greater than 63", ValidatorEx.validateEmail("username@1111111111222222222233333333334444444444555555555566666666667777.ocslab.com"));
        assertFalse("Domain label length greater than 63, local part is not valid, but quoted", ValidatorEx.validateEmail("\"! # $ % & ' * + - / = ?  ^ _ ` . { | } ~ \"@ocslab.com.11111111112222222222333333333344444444445555555555666666666677777.bn"));
    }
}
