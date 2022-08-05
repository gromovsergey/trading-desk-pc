package com.foros.session.creative;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptGroupState;
import com.foros.model.creative.CreativeOptGroupStatePK;
import com.foros.model.creative.CreativeSize;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.model.site.TagOptGroupState;
import com.foros.model.site.TagOptGroupStatePK;
import com.foros.model.site.WDTag;
import com.foros.model.site.WDTagOptGroupState;
import com.foros.model.site.WDTagOptGroupStatePK;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.Template;
import com.foros.session.site.TagsService;
import com.foros.session.site.WDTagService;
import com.foros.session.template.OptionGroupService;
import com.foros.session.template.TemplateService;
import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.CreativeTemplateTestFactory;
import com.foros.test.factory.DiscoverTemplateTestFactory;
import com.foros.test.factory.DisplayCreativeTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.SiteTestFactory;
import com.foros.test.factory.TagsTestFactory;
import com.foros.test.factory.WDTagTestFactory;

import group.Db;

import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class OptionGroupServiceIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private OptionGroupService optionGroupService;

    @Autowired
    private OptionGroupTestFactory optionGroupTF;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    @Autowired
    private CreativeSizeService creativeSizeService;

    @Autowired
    private TagsService tagsService;

    @Autowired
    private SiteTestFactory siteTF;

    @Autowired
    private TagsTestFactory tagsTF;

    @Autowired
    private WDTagTestFactory wdTagTF;

    @Autowired
    private WDTagService wdTagService;

    @Autowired
    private DisplayCreativeTestFactory displayCreativeTF;

    @Autowired
    private DisplayCreativeService displayCreativeService;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    @Autowired
    private DiscoverTemplateTestFactory discoverTemplateTF;

    @Test
    public void testCreateDeleteOnCreativeTemplate() {
        CreativeTemplate creativeTemplate = creativeTemplateTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(creativeTemplate);
        commitChanges();

        clearContext();
        optionGroup = optionGroupService.findById(optionGroup.getId());
        assertNotNull("OptionGroup was not persisted", entityManager.find(OptionGroup.class, optionGroup.getId()));

        Template template = templateService.findById(creativeTemplate.getId());
        assertTrue("Template does not contain OptionGroup", template.getOptionGroups().contains(optionGroup));

        optionGroupService.remove(optionGroup.getId());
        commitChanges();

        clearContext();
        assertNull("OptionGroup was not removed", entityManager.find(OptionGroup.class, optionGroup.getId()));
    }

    @Test
    public void testCreateDeleteOnWDTagTemplate() {
        DiscoverTemplate discoverTemplate = discoverTemplateTF.createPersistent();
        Long groupId = optionGroupTF.createPersistent(discoverTemplate).getId();
        commitChanges();

        clearContext();
        assertNotNull("OptionGroup was not persisted", entityManager.find(OptionGroup.class, groupId));

        Template template = templateService.findById(discoverTemplate.getId());
        assertTrue("Template does not contain OptionGroup", isGroupExist(template.getOptionGroups(), groupId));

        optionGroupService.remove(groupId);
        commitChanges();

        clearContext();
        assertNull("OptionGroup was not removed", entityManager.find(OptionGroup.class, groupId));
    }

    @Test
    public void testCreateDeleteOnCreativeSize() {
        CreativeSize creativeSize = creativeSizeTF.createPersistent();
        Long groupId = optionGroupTF.createPersistent(creativeSize).getId();
        commitChanges();

        clearContext();
        assertNotNull("OptionGroup was not persisted", entityManager.find(OptionGroup.class, groupId));

        CreativeSize size = creativeSizeService.findById(creativeSize.getId());
        assertTrue("CreativeSize does not contain OptionGroup", isGroupExist(size.getOptionGroups(), groupId));

        optionGroupService.remove(groupId);
        commitChanges();

        clearContext();
        assertNull("OptionGroup was not removed", entityManager.find(OptionGroup.class, groupId));
    }

    @Test
    public void testOptionGroupMove() {
        CreativeSize creativeSize = creativeSizeTF.createPersistent();
        OptionGroup group1 = optionGroupTF.createPersistent(creativeSize);
        OptionGroup group2 = optionGroupTF.createPersistent(creativeSize);
        assertTrue("Type of groups must be equal", group1.getType().equals(group2.getType()));
        if (group1.getSortOrder() > group2.getSortOrder()) {
            OptionGroup tmp = group1;
            group1 = group2;
            group2 = tmp;
        }
        commitChanges();
        clearContext();

        creativeSize = creativeSizeService.findById(creativeSize.getId());
        int groupsNum = creativeSize.getOptionGroups().size();
        group1.setSortOrder(group2.getSortOrder());
        optionGroupService.update(group1);

        commitChanges();
        clearContext();

        creativeSize = creativeSizeService.findById(creativeSize.getId());
        assertTrue("Option Groups size changed after move", creativeSize.getOptionGroups().size() == groupsNum);

        group1 = optionGroupService.findById(group1.getId());
        group2 = optionGroupService.findById(group2.getId());
        assertTrue("Group is not moved", group1.getSortOrder() > group2.getSortOrder());
    }

    @Test
    public void testOptionGroupTagRelationships() {
        CreativeTemplate template = templateService.findTextTemplate();
        OptionGroup optionGroup = optionGroupTF.createPersistent(template);
        template.getOptionGroups().add(optionGroup);
        commitChanges();

        clearContext();
        assertNotNull("OptionGroup was not persisted", entityManager.find(OptionGroup.class, optionGroup.getId()));

        Site site = siteTF.createPersistent();
        Tag tag = tagsTF.createPersistent(site);

        clearContext();
        TagOptGroupState optGroupState = new TagOptGroupState();
        optGroupState.setId(new TagOptGroupStatePK(optionGroup.getId(), tag.getId()));
        tag.getGroupStates().add(optGroupState);
        tagsService.updateOptions(tag);
        commitChanges();

        clearContext();
        tag = tagsService.find(tag.getId());
        assertTrue("TagOptGroupStates table is empty", tag.getGroupStates().size() > 0);

        boolean tagOptGroupStateError = true;
        for (TagOptGroupState togs : tag.getGroupStates()) {
            if (togs.getId().equals(new TagOptGroupStatePK(optionGroup.getId(), tag.getId()))) {
                tagOptGroupStateError = false;
            }
        }
        assertFalse("OptionGroup is not found in the TagOptGroupState table", tagOptGroupStateError);
    }

    @Test
    public void testOptionGroupWDTagRelationships() {
        DiscoverTemplate discoverTemplate = discoverTemplateTF.create();
        OptionGroup optionGroup = optionGroupTF.create(discoverTemplate);
        discoverTemplate.getOptionGroups().add(optionGroup);
        discoverTemplateTF.persist(discoverTemplate);
        optionGroupTF.persist(optionGroup);

        Site site = siteTF.createPersistent();
        WDTag wTag = wdTagTF.create(site);
        wTag.setTemplate(discoverTemplate);

        WDTagOptGroupState optGroupState = new WDTagOptGroupState();
        optGroupState.setId(new WDTagOptGroupStatePK(optionGroup.getId(), Long.valueOf("0")));
        wTag.getGroupStates().add(optGroupState);
        wdTagService.create(wTag);
        commitChanges();

        clearContext();
        wTag = wdTagService.find(wTag.getId());
        assertTrue("WDTagOptGroupStates table is empty", wTag.getGroupStates().size() > 0);

        boolean tagOptGroupStateError = true;
        for (WDTagOptGroupState togs : wTag.getGroupStates()) {
            if (togs.getId().equals(new WDTagOptGroupStatePK(optionGroup.getId(), wTag.getId()))) {
                tagOptGroupStateError = false;
            }
        }
        assertFalse("OptionGroup is not found in the WDTagOptGroupState table", tagOptGroupStateError);

        optionGroup = optionGroupService.findById(optionGroup.getId());
        optionGroupService.remove(optionGroup.getId());
        commitChanges();

        clearContext();
        assertNull("OptionGroup was not removed", entityManager.find(OptionGroup.class, optionGroup.getId()));

        clearContext();
        wTag = wdTagService.find(wTag.getId());

        tagOptGroupStateError = false;
        for (WDTagOptGroupState togs : wTag.getGroupStates()) {
            if (togs.getId().getOptionGroupId() == optionGroup.getId()) {
                tagOptGroupStateError = true;
            }
        }
        assertFalse("WDTagOptGroupState was not removed upon OptionGroup removal", tagOptGroupStateError);
    }

    @Test
    public void testOptionGroupCreativeRelationships() {
        OptionGroup optionGroup = optionGroupTF.create();
        Creative creative = displayCreativeTF.create();
        optionGroup.setTemplate(creative.getTemplate());
        creative.getSize().getOptionGroups().add(optionGroup);
        optionGroupService.create(optionGroup);
        commitChanges();

        clearContext();
        assertNotNull("OptionGroup was not persisted", entityManager.find(OptionGroup.class, optionGroup.getId()));

        CreativeOptGroupState optGroupState = new CreativeOptGroupState();
        optGroupState.setId(new CreativeOptGroupStatePK(optionGroup.getId(), Long.valueOf("0")));
        creative.getGroupStates().add(optGroupState);
        displayCreativeService.create(creative);
        commitChanges();

        clearContext();
        creative = displayCreativeService.find(creative.getId());
        assertTrue("CreativeOptGroupStates table is empty", creative.getGroupStates().size() > 0);

        boolean tagOptGroupStateError = true;
        for (CreativeOptGroupState togs : creative.getGroupStates()) {
            if (togs.getId().equals(new CreativeOptGroupStatePK(optionGroup.getId(), creative.getId()))) {
                tagOptGroupStateError = false;
            }
        }
        assertFalse("OptionGroup is not found in the WDTagOptGroupState table", tagOptGroupStateError);

        optionGroup = optionGroupService.findById(optionGroup.getId());
        optionGroupService.remove(optionGroup.getId());
        commitChanges();

        clearContext();
        assertNull("OptionGroup was not removed", entityManager.find(OptionGroup.class, optionGroup.getId()));

        creative = displayCreativeService.find(creative.getId());
        tagOptGroupStateError = false;
        for (CreativeOptGroupState togs : creative.getGroupStates()) {
            if (togs.getId().getOptionGroupId() == optionGroup.getId()) {
                tagOptGroupStateError = true;
            }
        }
        assertFalse("WDTagOptGroupState was not removed upon OptionGroup removal", tagOptGroupStateError);
    }

    private boolean isGroupExist(Set<OptionGroup> groups, Long id) {
        for(OptionGroup currentGroup: groups) {
            if (currentGroup.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
