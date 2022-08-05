package com.foros.session.creative;

import com.foros.AbstractValidationsTest;
import com.foros.model.creative.CreativeCategoryType;

import group.Db;
import group.Validation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Db.class, Validation.class })
public class CreativeCategoryValidationsTest extends AbstractValidationsTest {
    @Test
    public void testValidateUpdate() throws Exception {
        CreativeCategoryEditTO creativeCategoryEditTO = createCreativeCategoryEditTO();
        validate("CreativeCategory.update", creativeCategoryEditTO);
        assertViolationsCount(0);
    }

    @Test
    public void testValidateUpdateType() throws Exception {
        // type is required
        CreativeCategoryEditTO nullType = createCreativeCategoryEditTO();
        nullType.setType(null);
        validate("CreativeCategory.update", nullType);
        assertHasViolation("type");
        assertViolationsCount(1);
    }

    @Test
    public void testValidateTanXAndIAB() throws Exception {
        // type is required
        CreativeCategoryTO category1 = new CreativeCategoryTO();
        category1.setName(StringUtils.repeat("0", 99));
        List<String> rtbCategories = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rtbCategories.add(StringUtils.repeat("0", 51));
        }
        category1.setRtbCategories(rtbCategories);
        CreativeCategoryEditTO invalidKeys = createCreativeCategoryEditTO();
        invalidKeys.getCategories().add(category1);
        validate("CreativeCategory.update", invalidKeys);
        assertHasViolation("categories");
        assertViolationsCount(5);
    }

    @Test
    public void testValidateUpdateName() throws Exception {
        CreativeCategoryTO category1 = new CreativeCategoryTO();
        category1.setName(StringUtils.repeat("0", 101));
        // name length
        CreativeCategoryEditTO longCategory = createCreativeCategoryEditTO();
        longCategory.getCategories().add(category1);
        validate("CreativeCategory.update", longCategory);
        assertHasViolation("categories");
        assertViolationsCount(1);

        category1 = new CreativeCategoryTO();
        category1.setName(StringUtils.repeat("0", 50));
        CreativeCategoryEditTO normalCategory = createCreativeCategoryEditTO();
        normalCategory.getCategories().add(category1);
        validate("CreativeCategory.update", normalCategory);
        assertViolationsCount(0);
    }

    @Test
    public void testValidateUpdateCategories() throws Exception {
        // allowed symbols
        CreativeCategoryTO category1 = new CreativeCategoryTO();
        category1.setName("@@@@");
        CreativeCategoryEditTO invalidSymbols = createCreativeCategoryEditTO();
        invalidSymbols.getCategories().add(category1);
        validate("CreativeCategory.update", invalidSymbols);
        assertHasViolation("categories");
        assertEquals(1, violations.size());
    }

    @Test
    public void testValidateUpdateDuplicates() throws Exception {
        CreativeCategoryTO category1 = new CreativeCategoryTO();
        category1.setName("123");
        CreativeCategoryTO category2 = new CreativeCategoryTO();
        category2.setName("123");
        // duplicates
        CreativeCategoryEditTO duplicateCategories = createCreativeCategoryEditTO();
        duplicateCategories.getCategories().add(category1);
        duplicateCategories.getCategories().add(category2);
        validate("CreativeCategory.update", duplicateCategories);
        assertHasViolation("categories");
        assertEquals(1, violations.size());

        category1 = new CreativeCategoryTO();
        category1.setName("abc");
        category2 = new CreativeCategoryTO();
        category2.setName("ABC");
        CreativeCategoryEditTO differentCaseCategories = createCreativeCategoryEditTO();
        differentCaseCategories.getCategories().add(category1);
        differentCaseCategories.getCategories().add(category2);
        validate("CreativeCategory.update", differentCaseCategories);
        assertViolationsCount(0);
    }

    @Test
    public void testValidateUpdateTag() throws Exception {
        CreativeCategoryEditTO upperCaseTagCategories = createCreativeCategoryEditTO();
        upperCaseTagCategories.setType(CreativeCategoryType.TAG);
        CreativeCategoryTO category1 = new CreativeCategoryTO();
        category1.setName("ABC");
        upperCaseTagCategories.getCategories().add(category1);
        validate("CreativeCategory.update", upperCaseTagCategories);
        assertHasViolation("categories");
        assertViolationsCount(1);
    }

    private CreativeCategoryEditTO createCreativeCategoryEditTO() {
        CreativeCategoryEditTO creativeCategoryEditTO = new CreativeCategoryEditTO();
        creativeCategoryEditTO.setVersion(new Timestamp(System.currentTimeMillis()));
        creativeCategoryEditTO.setType(CreativeCategoryType.CONTENT);
        return creativeCategoryEditTO;
    }
}
