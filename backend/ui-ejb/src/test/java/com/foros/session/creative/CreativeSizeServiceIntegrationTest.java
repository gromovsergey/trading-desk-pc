package com.foros.session.creative;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.Status;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.CreativeSizeExpansion;
import com.foros.model.security.AccountType;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionType;
import com.foros.session.EntityTO;
import com.foros.session.template.OptionGroupService;
import com.foros.session.template.OptionService;
import com.foros.test.factory.AdvertiserAccountTypeTestFactory;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.SizeTypeTestFactory;

import group.Db;
import java.util.LinkedHashSet;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class CreativeSizeServiceIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CreativeSizeService creativeSizeService;

    @Autowired
    private OptionGroupService optionGroupService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private SizeTypeTestFactory sizeTypeTF;

    @Autowired
    private AdvertiserAccountTypeTestFactory accountTypeTestFactory;

    @Test
    public void testCreate() throws Exception {
        CreativeSize creativeSize = creativeSizeTF.createPersistent();

        assertNotNull("ID wasn't set", creativeSize.getId());
        assertEquals("Status wasn't set", Status.ACTIVE, creativeSize.getStatus());
        assertEquals("CreativeSize wasn't found in the DB", 1,
                jdbcTemplate.queryForInt("select count(*) from CREATIVESIZE where name = ?",
                creativeSize.getName().getDefaultName()));
    }

    @Test
    public void testSearch() {
        CreativeSize creativeSize = creativeSizeTF.createPersistent();
        creativeSizeService.delete(creativeSize.getId());
        commitChanges();

        setDeletedObjectsVisible(true);
        List<CreativeSize> result = creativeSizeService.findAll();
        assertTrue(result.contains(creativeSize));
        setDeletedObjectsVisible(false);
        result = creativeSizeService.findAllNotDeleted();
        assertTrue(!result.contains(creativeSize));
    }

    @Test
    public void testUpdate() throws Exception {
        CreativeSize creativeSize = creativeSizeTF.createPersistent();

        creativeSizeService.update(creativeSize);
        assertEquals("CreativeSize wasn't found in the DB", 1,
                jdbcTemplate.queryForInt("select count(*) from CREATIVESIZE where name = ?",
                creativeSize.getName().getDefaultName()));
    }

    @Test
    public void testCreateWithEmptyOptions() throws Exception {
        CreativeSize creativeSize = creativeSizeTF.create();
        //creativeSize.setOptions(new LinkedHashSet<Option>());

        creativeSizeService.create(creativeSize);

        assertTrue(creativeSize.getAllOptions().isEmpty());
        assertNotNull("ID wasn't set", creativeSize.getId());
        assertEquals("Status wasn't set", Status.ACTIVE, creativeSize.getStatus());
        assertEquals("CreativeSize wasn't found in the DB", 1,
                jdbcTemplate.queryForInt("select count(*) from CREATIVESIZE where name = ?",
                creativeSize.getName().getDefaultName()));

    }

    @Test
    public void testIndex() {
        setDeletedObjectsVisible(true);
        CreativeSize creativeSize = creativeSizeTF.create();
        creativeSizeService.create(creativeSize);

        getEntityManager().clear();
        creativeSizeService.delete(creativeSize.getId());
        commitChanges();

        List<EntityTO> entities = creativeSizeService.getIndex();
        assertTrue(sizeExists(entities, creativeSize.getId()));

        setDeletedObjectsVisible(false);
        entities = creativeSizeService.getIndex();
        assertFalse(sizeExists(entities, creativeSize.getId()));
    }

    @Test
    public void testUpdateWithEmptyOptions() throws Exception {
        CreativeSize creativeSize = creativeSizeTF.createPersistent();
        creativeSize.setOptionGroups(new LinkedHashSet<OptionGroup>());

        creativeSizeService.update(creativeSize);

        assertTrue(creativeSize.getAllOptions().isEmpty());
        assertEquals("CreativeSize wasn't found in the DB", 1,
                jdbcTemplate.queryForInt("select count(*) from CREATIVESIZE where name = ?",
                creativeSize.getName().getDefaultName()));
    }

    @Test
    public void testFindTextSize() throws Exception {
        CreativeSize creativeSize = creativeSizeTF.findText();
        CreativeSize size = creativeSizeService.findTextSize();
        assertEquals(creativeSize, size);
    }

    @Test
    public void testCreativeSizeCreateCopy() throws Exception {
        CreativeSize creativeSize = new CreativeSize();
        creativeSize.setDefaultName("automated test default name");
        creativeSize.setProtocolName("automated test protocol name");
        creativeSize.setWidth(10L);
        creativeSize.setHeight(10L);
        creativeSize.setSizeType(sizeTypeTF.createPersistent());
        creativeSize.setStatus(Status.ACTIVE);
        creativeSizeService.create(creativeSize);
        commitChanges();
        clearContext();

        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setDefaultLabel("automated test option group label");
        optionGroup.setDefaultName("automated test option group name");
        optionGroup.setSortOrder(1);
        optionGroup.setCreativeSize(creativeSize);
        optionGroup.setType(OptionGroupType.Advertiser);
        optionGroupService.create(optionGroup);
        commitChanges();
        clearContext();

        Option option = new Option();
        option.setDefaultLabel("automated test option label");
        option.setDefaultName("automated test option name");
        option.setOptionGroup(optionGroup);
        option.setSortOrder(0);
        option.setType(OptionType.STRING);
        option.setToken("automated test token");
        optionService.create(option);
        commitChanges();
        clearContext();

        CreativeSize copy = creativeSizeService.createCopy(creativeSize.getId());
        creativeSize = creativeSizeTF.refresh(creativeSize);
        assertNotNull(copy.getId());
        assertEquals("Status wasn't set", creativeSize.getStatus(), copy.getStatus());
        assertEquals("Copy of automated test default name", copy.getDefaultName());
        assertEquals("Option Group names doesn't equals each other", creativeSize.getOptionGroups().iterator().next().getDefaultName(), copy.getOptionGroups().iterator().next().getDefaultName());
        assertNotSame("Option Group ids equals each other", creativeSize.getOptionGroups().iterator().next().getId(), copy.getOptionGroups().iterator().next().getId());
        assertEquals("Option names doesn't equals each other", creativeSize.getAllOptions().iterator().next().getDefaultName(), copy.getAllOptions().iterator().next().getDefaultName());
        assertNotSame("Option ids equals each other", creativeSize.getAllOptions().iterator().next().getId(), copy.getAllOptions().iterator().next().getId());
        assertEquals("SizeType ids equals each other", creativeSize.getSizeType().getId(), copy.getSizeType().getId());
    }

    private boolean sizeExists(List<EntityTO> entities, Long sizeId) {
        for (EntityTO entity : entities) {
            if (entity.getId().equals(sizeId)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testFindByAccountTypeAndSizeType() throws Exception {
        CreativeSize creativeSize = creativeSizeTF.createPersistent();
        AccountType accountType = accountTypeTestFactory.createPersistent(creativeSize, null);
        commitChanges();
        clearContext();

        List<CreativeSize> list = creativeSizeService.findByAccountTypeAndSizeType(accountType.getId(), creativeSize.getSizeType().getId());
        assertNotNull(list);
        assertTrue(list.iterator().next().getId().equals(creativeSize.getId()));

    }

    @Test
    public void testCountExpandable() {
        CreativeSize creativeSize = creativeSizeTF.create();
        creativeSize.getExpansions().add(CreativeSizeExpansion.DOWN_LEFT);
        creativeSizeTF.persist(creativeSize);

        assertEquals(0, creativeSizeService.countExpandableTags(creativeSize.getId()));
        assertEquals(0, creativeSizeService.countExpandableCreatives(creativeSize.getId()));
        assertEquals(0, creativeSizeService.countCreativesByExpansions(creativeSize.getId(), creativeSize.getExpansions()));
    }
}
