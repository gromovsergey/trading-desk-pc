package com.foros.util;

import com.foros.AbstractUnitTest;
import com.foros.model.EntityBase;
import com.foros.model.Status;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.security.MockPrincipal;
import com.foros.security.SecurityContextMock;
import com.foros.session.EntityTO;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Id;

import static org.junit.Assert.*;

@Category(Unit.class)
public class EntityUtilsTest extends AbstractUnitTest {
    @Test
    public void findIdField() {
        Field idProperty = EntityUtils.findIdProperty(TestEntity.class);
        assertNotNull(idProperty);
        assertEquals(idProperty.getName(), "testId");

        idProperty = EntityUtils.findIdProperty(EntityBase.class);
        assertNull(idProperty);
    }

    @Test
    public void applyStatusRules() {
        List<EntityTO> base = initEntities();
        List<EntityTO> entities = initEntities();

        SecurityContextMock.getInstance().setPrincipal(new MockPrincipal());

        EntityUtils.applyStatusRules(entities, null, false);
        assertEquals("Wrong collection length", 3, entities.size());
        assertTrue("entity 1 not found", entities.contains(base.get(0)));
        assertTrue("entity 2 not found", entities.contains(base.get(1)));
        assertTrue("entity 4 not found", entities.contains(base.get(3)));
        assertEquals("Wrong name", "Name1", entities.get(0).getName());
        assertEquals("Wrong name", "Name2",entities.get(1).getName());
        assertEquals("Wrong name", "Name4 " + StringUtil.getLocalizedString("suffix.inactive"), entities.get(2).getName());

        entities = initEntities();
        EntityUtils.applyStatusRules(entities, null, true);
        assertEquals("Wrong collection length", entities.size(), 5);
        assertTrue("entity 1 not found", entities.contains(base.get(0)));
        assertTrue("entity 2 not found", entities.contains(base.get(1)));
        assertTrue("entity 3 not found", entities.contains(base.get(2)));
        assertTrue("entity 4 not found", entities.contains(base.get(3)));
        assertTrue("entity 5 not found", entities.contains(base.get(4)));
        assertEquals("Wrong name", "Name1", entities.get(0).getName());
        assertEquals("Wrong name", "Name2", entities.get(1).getName());
        assertEquals("Wrong name", "Name3 " + StringUtil.getLocalizedString("suffix.deleted"), entities.get(2).getName());
        assertEquals("Wrong name", "Name4 " + StringUtil.getLocalizedString("suffix.inactive"), entities.get(3).getName());
        assertEquals("Wrong name", "Name5 " + StringUtil.getLocalizedString("suffix.deleted"), entities.get(4).getName());

        entities = initEntities();
        EntityUtils.applyStatusRules(entities, 3L, false);
        assertEquals("Wrong collection length", entities.size(), 4);
        assertTrue("entity 1 not found", entities.contains(base.get(0)));
        assertTrue("entity 2 not found", entities.contains(base.get(1)));
        assertTrue("entity 3 not found", entities.contains(base.get(2)));
        assertTrue("entity 4 not found", entities.contains(base.get(3)));
        assertEquals("Wrong name", "Name1", entities.get(0).getName());
        assertEquals("Wrong name", "Name2", entities.get(1).getName());
        assertEquals("Wrong name", "Name3 " + StringUtil.getLocalizedString("suffix.deleted"), entities.get(2).getName());
        assertEquals("Wrong name", "Name4 " + StringUtil.getLocalizedString("suffix.inactive"), entities.get(3).getName());

        SecurityContextMock.getInstance().tearDown();
    }

    @Test
    public void cloneCCGWithDeletedCreatives() {
        CampaignCreativeGroup ccg = new CampaignCreativeGroup(1L);
        ccg.setStatus(Status.ACTIVE);
        ccg.setCcgType(CCGType.DISPLAY);

        Set<CampaignCreative> campaignCreatives = new LinkedHashSet<CampaignCreative>();

        CampaignCreative cc1 = new CampaignCreative(1L);
        cc1.setCreativeGroup(ccg);
        cc1.setStatus(Status.ACTIVE);
        Creative creative1 = new Creative(1L);
        creative1.setStatus(Status.DELETED);
        cc1.setCreative(creative1);
        campaignCreatives.add(cc1);

        CampaignCreative cc2 = new CampaignCreative(2L);
        cc2.setCreativeGroup(ccg);
        cc2.setStatus(Status.ACTIVE);
        Creative creative2 = new Creative(2L);
        creative2.setStatus(Status.ACTIVE);
        cc2.setCreative(creative2);
        campaignCreatives.add(cc2);

        ccg.setCampaignCreatives(campaignCreatives);

        CampaignCreativeGroup clonedCCG = EntityUtils.clone(ccg);

        assertEquals("Wrong amount of CCs", 1, clonedCCG.getCampaignCreatives().size());
        assertSame("Wrong creative", creative2, clonedCCG.getCampaignCreatives().iterator().next().getCreative());
    }

    @Test
    public void copyCampaign() {
        Campaign campaign1 = createCampaign(1L, "Name 1", BigDecimal.ONE);

        Campaign campaign2 = createCampaign(2L, "Name 2", BigDecimal.TEN);
        campaign2.getChanges().remove("budget");

        EntityUtils.copy(campaign1, campaign2);

        assertEquals("Wrong name", "Name 2", campaign1.getName());
        assertEquals("Wrong budget", BigDecimal.ONE, campaign1.getBudget());
    }

    @Test
    public void badEntity() {
        BadEntity src = new BadEntity();
        src.changeFiled(2L);
        BadEntity dst = new BadEntity();
        try {
            EntityUtils.copy(dst, src);
            fail();
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void copyCampaignWithWrongChanges() {
        Campaign campaign1 = createCampaign(1L, "Name 1", BigDecimal.ONE);

        Campaign campaign2 = createCampaign(2L, "Name 2", BigDecimal.TEN);
        campaign2.getChanges().add("wrongChange");

        try {
            EntityUtils.copy(campaign1, campaign2);
            fail("Exception must be thrown!");
        } catch (RuntimeException e) {
            // all ok
        }
    }

    @Test
    public void appendStatusSuffix() {
        SecurityContextMock.getInstance().setPrincipal(new MockPrincipal());
        //SecurityContextMock.getInstance().getPrincipal().setLocale(Locale.US);

        String value = "Name";
        assertEquals(value + " " + StringUtil.getLocalizedString("suffix.inactive"), EntityUtils.appendStatusSuffix(value, Status.INACTIVE));
        assertEquals(value + " " + StringUtil.getLocalizedString("suffix.deleted"), EntityUtils.appendStatusSuffix(value, Status.DELETED));

        try{
            assertEquals(value, EntityUtils.appendStatusSuffix(value, (Status)null));
            fail("We do not support nulls");
        } catch (IllegalArgumentException ex) {
            assertEquals("Status can't be null", ex.getMessage());
        }
         SecurityContextMock.getInstance().tearDown();
    }

    @Test
    public void copyWithStatusRules() {
        List<EntityTO> base = initEntities();
        List<EntityTO> entities = initEntities();

        SecurityContextMock.getInstance().setPrincipal(new MockPrincipal());
        //SecurityContextMock.getInstance().getPrincipal().setLocale(Locale.US);

        entities = EntityUtils.copyWithStatusRules(entities, null, false);
        assertEquals("Wrong collection length", 3, entities.size());
        assertTrue("entity 1 not found", entities.contains(base.get(0)));
        assertTrue("entity 2 not found", entities.contains(base.get(1)));
        assertTrue("entity 4 not found", entities.contains(base.get(3)));
        assertEquals("Wrong name", "Name1", entities.get(0).getName());
        assertEquals("Wrong name", "Name2", entities.get(1).getName());
        assertEquals("Wrong name", "Name4 " + StringUtil.getLocalizedString("suffix.inactive"), entities.get(2).getName());

        entities = initEntities();
        entities = EntityUtils.copyWithStatusRules(entities, null, true);
        assertEquals("Wrong collection length", entities.size(), 5);
        assertTrue("entity 1 not found", entities.contains(base.get(0)));
        assertTrue("entity 2 not found", entities.contains(base.get(1)));
        assertTrue("entity 3 not found", entities.contains(base.get(2)));
        assertTrue("entity 4 not found", entities.contains(base.get(3)));
        assertTrue("entity 5 not found", entities.contains(base.get(4)));
        assertEquals("Wrong name", "Name1", entities.get(0).getName());
        assertEquals("Wrong name", "Name2", entities.get(1).getName());
        assertEquals("Wrong name", "Name3 " + StringUtil.getLocalizedString("suffix.deleted"), entities.get(2).getName());
        assertEquals("Wrong name", "Name4 " + StringUtil.getLocalizedString("suffix.inactive"), entities.get(3).getName());
        assertEquals("Wrong name", "Name5 " + StringUtil.getLocalizedString("suffix.deleted"), entities.get(4).getName());

        entities = initEntities();
        entities = EntityUtils.copyWithStatusRules(entities, 3L, false);
        assertEquals("Wrong collection length", entities.size(), 4);
        assertTrue("entity 1 not found", entities.contains(base.get(0)));
        assertTrue("entity 2 not found", entities.contains(base.get(1)));
        assertTrue("entity 3 not found", entities.contains(base.get(2)));
        assertTrue("entity 4 not found", entities.contains(base.get(3)));
        assertEquals("Wrong name", "Name1", entities.get(0).getName());
        assertEquals("Wrong name", "Name2", entities.get(1).getName());
        assertEquals("Wrong name", "Name3 " + StringUtil.getLocalizedString("suffix.deleted"), entities.get(2).getName());
        assertEquals("Wrong name", "Name4 " + StringUtil.getLocalizedString("suffix.inactive"), entities.get(3).getName());

        SecurityContextMock.getInstance().tearDown();
    }

    private List<EntityTO> initEntities() {
        List<EntityTO> entities = new LinkedList<EntityTO>();
        entities.add(new EntityTO(1L, "Name1", 'A'));
        entities.add(new EntityTO(2L, "Name2", 'A'));
        entities.add(new EntityTO(3L, "Name3", 'D'));
        entities.add(new EntityTO(4L, "Name4", 'I'));
        entities.add(new EntityTO(5L, "Name5", 'D'));
        return entities;
    }

    private Campaign createCampaign(long id, String name, BigDecimal budget) {
        Campaign campaign = new Campaign(id);
        campaign.setName(name);
        campaign.setBudget(budget);
        campaign.setDateStart(new Date());
        return campaign;
    }

    class TestEntityBease extends EntityBase {
        @SuppressWarnings({"UnusedDeclaration"})
        @Id
        private Long testId;
    }

    class TestEntity extends TestEntityBease {
    }

    // entity with no getter\setter for field
    private class BadEntity extends EntityBase {
        private Long field;

        public void changeFiled(Long field) {
            this.field = field;
            this.registerChange("field");
        }
    }
}
