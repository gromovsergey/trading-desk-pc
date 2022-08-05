package com.foros.session.creative;

import com.foros.AbstractValidationsTest;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.CreativeSizeExpansion;
import com.foros.test.factory.CreativeSizeTestFactory;

import group.Db;
import group.Validation;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Validation.class })
public class CreativeSizeValidationsTest extends AbstractValidationsTest {

    @Autowired
    private CreativeSizeTestFactory creativeSizeTestFactory;

    @Test
    public void testEntityManagement() {
        // Creative and persist creative size with not-empty expansions
        CreativeSize creativeSize = creativeSizeTestFactory.create();
        Long height = creativeSize.getHeight();
        Long width = creativeSize.getWidth();
        creativeSize.setMaxWidth(width);
        creativeSize.setMaxHeight(height);
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.UP, CreativeSizeExpansion.DOWN, CreativeSizeExpansion.LEFT)));
        creativeSizeTestFactory.persist(creativeSize);
        entityManager.flush();

        // Fetch creative size, set empty expansions collection and persist
        CreativeSize fetchedCreativeSize = entityManager.find(CreativeSize.class, creativeSize.getId());
        assertTrue("Fetched creative size expansions size is not vaid", fetchedCreativeSize.getExpansions().size() == 3);
        fetchedCreativeSize.setExpansions(new HashSet<CreativeSizeExpansion>());
        entityManager.merge(fetchedCreativeSize);
        entityManager.flush();

        // Fetch creative size and test expansions collection size
        CreativeSize finalCreativeSize = entityManager.find(CreativeSize.class, fetchedCreativeSize.getId());
        assertTrue("Expansions collection is not empty!", finalCreativeSize.getExpansions().isEmpty());
    }

    @Test
    public void testValidate0() {
        CreativeSize creativeSize = creativeSizeTestFactory.create();
        Long height = creativeSize.getHeight();
        Long width = creativeSize.getWidth();
        creativeSize.setMaxWidth(width);
        creativeSize.setMaxHeight(height);
        creativeSize.setExpansions(new HashSet<CreativeSizeExpansion>());

        // Valid maxW = w and maxH = h
        validate("CreativeSize.create", creativeSize);
        assertViolationsCount(0); // MaxW = w and MaxH = h and expansions.size()=0 did not pass validation (see REQ-2364)"

        // Valid maxH, h, maxW, w are null
        creativeSize.setMaxHeight(null);
        creativeSize.setMaxWidth(null);
        creativeSize.setHeight(null);
        creativeSize.setWidth(null);
        validate("CreativeSize.create", creativeSize);
        assertViolationsCount(0); // Everything is null did not pass validation (see REQ-2364)

        // Invalid sizes are null and expansions.size() >0
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.UP)));
        validate("CreativeSize.create", creativeSize);
        assertTrue("Everything is null and expansions.size() > 0 passed validation (see REQ-2364)", !violations.isEmpty());
        assertHasViolation("expansions");

        // Invalid h is not null (eg.) and others are null
        creativeSize.setHeight(100L);
        validate("CreativeSize.create", creativeSize);
        assertTrue("Everything is null exception H passed validation (see REQ-2364)", !violations.isEmpty());
        // assertHasViolation("sizes");

        // Invalid w > maxW and h = maxH
        creativeSize.setHeight(height);
        creativeSize.setWidth(width);
        creativeSize.setMaxWidth(width - 1);
        creativeSize.setMaxHeight(height);
        validate("CreativeSize.create", creativeSize);
        assertTrue("MaxW < w and MaxH = h passed validation (see REQ-2364)", !violations.isEmpty());
        assertHasViolation("maxWidth");

        // Invalid w = maxW and h > maxH
        creativeSize.setMaxWidth(width);
        creativeSize.setMaxHeight(height - 1);
        validate("CreativeSize.create", creativeSize);
        assertTrue("MaxW = w and MaxH < h passed validation (see REQ-2364)", !violations.isEmpty());
        assertHasViolation("maxHeight");

        // Invalid w = maxW, maxH>h and expansions.size() == 0
        creativeSize.setMaxWidth(width);
        creativeSize.setMaxHeight(height + 1);
        creativeSize.setExpansions(new HashSet<CreativeSizeExpansion>());
        validate("CreativeSize.create", creativeSize);
        assertTrue("MaxW = w, MaxH < h and empty expansions passed validation (see REQ-2364)", !violations.isEmpty());
        assertHasViolation("expansions");
    }

    @Test
    public void testValidate1() {
        CreativeSize creativeSize = creativeSizeTestFactory.create();
        Long height = creativeSize.getHeight();
        Long width = creativeSize.getWidth();

        // Valid with full directions set
        creativeSize.setMaxWidth(width + 1);
        creativeSize.setMaxHeight(height + 1);
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(
                CreativeSizeExpansion.UP, CreativeSizeExpansion.DOWN,
                CreativeSizeExpansion.RIGHT, CreativeSizeExpansion.LEFT,
                CreativeSizeExpansion.DOWN_LEFT, CreativeSizeExpansion.DOWN_RIGHT,
                CreativeSizeExpansion.UP_LEFT, CreativeSizeExpansion.UP_RIGHT)));

        validate("CreativeSize.create", creativeSize);
        assertViolationsCount(0); // 1st string condition did not pass validation (see REQ-2364)

        // Valid with not full directions set
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.DOWN_LEFT, CreativeSizeExpansion.DOWN_RIGHT)));
        validate("CreativeSize.create", creativeSize);
        assertViolationsCount(0); // 1st string condition did not pass validation with not full expansions set (see REQ-2364)
    }

    @Test
    public void testValidate2() {
        CreativeSize creativeSize = creativeSizeTestFactory.create();
        Long height = creativeSize.getHeight();
        Long width = creativeSize.getWidth();

        // Valid with full directions set
        creativeSize.setMaxWidth(width);
        creativeSize.setMaxHeight(height + 1);
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.UP, CreativeSizeExpansion.DOWN)));

        validate("CreativeSize.create", creativeSize);
        assertViolationsCount(0); // 2st string condition did not pass validation (see REQ-2364)

        // Valid with not full directions set
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.UP)));
        validate("CreativeSize.create", creativeSize);
        assertViolationsCount(0); // 2st string condition did not pass validation with not full expansions set (see REQ-2364)

        // Valid with empty directions set
        creativeSize.setExpansions(new HashSet<CreativeSizeExpansion>());
        assertViolationsCount(0); // 2st string condition did not pass validation with empty expansions set (see REQ-2364)

        // Invalid directions set
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.UP, CreativeSizeExpansion.DOWN_LEFT)));
        validate("CreativeSize.create", creativeSize);
        assertEquals("2st string condition passed validation with not valid expansions set (see REQ-2364)", 1, violations.size());
        assertHasViolation("expansions");
    }

    @Test
    public void testValidate3() {
        CreativeSize creativeSize = creativeSizeTestFactory.create();
        Long height = creativeSize.getHeight();
        Long width = creativeSize.getWidth();

        // Valid with full directions set
        creativeSize.setMaxWidth(width + 1);
        creativeSize.setMaxHeight(height);
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.LEFT, CreativeSizeExpansion.RIGHT)));

        validate("CreativeSize.create", creativeSize);
        assertViolationsCount(0); // 3rd string condition did not pass validation (see REQ-2364)

        // Valid with not full directions set
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.RIGHT)));
        validate("CreativeSize.create", creativeSize);
        assertViolationsCount(0); // 3rd string condition did not pass validation with not full expansions set (see REQ-2364)

        // Valid with empty directions set
        creativeSize.setExpansions(new HashSet<CreativeSizeExpansion>());
        assertViolationsCount(0); // 3rd string condition did not pass validation with empty expansions set (see REQ-2364)

        // Invalid directions set
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.UP, CreativeSizeExpansion.DOWN_LEFT)));
        validate("CreativeSize.create", creativeSize);
        assertEquals("rd string condition passed validation with not valid expansions set (see REQ-2364)", 1, violations.size());
        assertHasViolation("expansions");
    }

    @Test
    public void testValidate4() {
        CreativeSize creativeSize = creativeSizeTestFactory.create();
        Long height = creativeSize.getHeight();
        Long width = creativeSize.getWidth();

        // Valid with null directions set
        creativeSize.setMaxWidth(width);
        creativeSize.setMaxHeight(height);
        creativeSize.setExpansions(new HashSet<CreativeSizeExpansion>());

        validate("CreativeSize.create", creativeSize);
        assertViolationsCount(0); // 4th string condition did not pass validation with null expansions set(see REQ-2364)

        // Valid with empty directions set
        creativeSize.setExpansions(new HashSet<CreativeSizeExpansion>());

        validate("CreativeSize.create", creativeSize);
        assertViolationsCount(0); // 4th string condition did not pass validation with empty expansions set(see REQ-2364)

        // Invalid with not empty directions set
        creativeSize.setExpansions(new HashSet<>(Arrays.asList(CreativeSizeExpansion.UP, CreativeSizeExpansion.DOWN_LEFT)));
        validate("CreativeSize.create", creativeSize);
        assertEquals("4th string condition passed validation with not valid expansions set (see REQ-2364)", 1, violations.size());
        assertHasViolation("expansions");
    }
}
