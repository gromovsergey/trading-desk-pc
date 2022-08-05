package com.foros.session.creative;

import static com.foros.model.creative.CreativeCategoryType.CONTENT;
import static com.foros.model.creative.CreativeCategoryType.TAG;
import static com.foros.model.creative.CreativeCategoryType.VISUAL;
import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.ApproveStatus;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.RTBCategory;
import com.foros.test.factory.CreativeCategoryTestFactory;
import com.foros.util.CollectionUtils;
import com.foros.util.VersionCollisionException;
import com.foros.util.mapper.Converter;

import group.Db;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

@Category(Db.class)
public class CreativeCategoryServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private CreativeCategoryServiceBean categoryServiceBean;

    @Autowired
    private CreativeCategoryTestFactory creativeCategoryTF;

    @Test
    public void testCreateCategory() throws Exception {
        CreativeCategory content = creativeCategoryTF.create(CONTENT);
        CreativeCategory tag = creativeCategoryTF.create(TAG);
        CreativeCategory visual = creativeCategoryTF.create(VISUAL);

        categoryServiceBean.create(content);
        categoryServiceBean.create(visual);
        categoryServiceBean.create(tag);
    }

    @Test
    public void testCreateDuplicatedName() throws Exception {
        CreativeCategory content = creativeCategoryTF.create(CONTENT);
        CreativeCategory tag = creativeCategoryTF.create(TAG);
        CreativeCategory visual = creativeCategoryTF.create(VISUAL);
        tag.setDefaultName(content.getDefaultName());
        visual.setDefaultName(content.getDefaultName());

        categoryServiceBean.create(content);
        categoryServiceBean.create(tag);
        categoryServiceBean.create(visual);

        commitChanges();

        assertEquality(content.getId(), content.getDefaultName(), CONTENT);
        assertEquality(tag.getId(), tag.getDefaultName(), TAG);
        assertEquality(visual.getId(), visual.getDefaultName(), VISUAL);
    }

    @Test(expected = VersionCollisionException.class)
    public void testConcurrentUpdate() {
        CreativeCategoryTO testCategoryTo1 = new CreativeCategoryTO();
        testCategoryTo1.setName(UUID.randomUUID().toString());
        CreativeCategoryTO testCategoryTo2 = new CreativeCategoryTO();
        testCategoryTo2.setName(UUID.randomUUID().toString());
        CreativeCategoryTO testCategoryTo3 = new CreativeCategoryTO();
        testCategoryTo3.setName(UUID.randomUUID().toString());

        CreativeCategoryEditTO editTO1 = categoryServiceBean.getForEdit(CreativeCategoryType.CONTENT);
        editTO1.getCategories().add(testCategoryTo1);
        editTO1.getCategories().add(testCategoryTo2);

        categoryServiceBean.update(editTO1);
        commitChanges();

        CreativeCategoryEditTO editTO2 = categoryServiceBean.getForEdit(CreativeCategoryType.CONTENT);
        editTO2.getCategories().get(0).setName(UUID.randomUUID().toString());

        CreativeCategoryEditTO editTO3 = categoryServiceBean.getForEdit(CreativeCategoryType.CONTENT);
        editTO3.getCategories().add(testCategoryTo3);

        categoryServiceBean.update(editTO2);
        commitChanges();

        categoryServiceBean.update(editTO3);
        commitChanges();
    }

    @Test
    public void testUpdate() throws Exception {
        creativeCategoryTF.createPersistent(TAG, ApproveStatus.APPROVED);

        CreativeCategory notApprovedTag = creativeCategoryTF.createPersistent(TAG, ApproveStatus.HOLD);
        CreativeCategoryTO notApprovedTagTo = new CreativeCategoryTO();
        notApprovedTagTo.setName(notApprovedTag.getDefaultName());
        Collection<String> rtbCategories = CollectionUtils.convert(new Converter<RTBCategory, String>() {
            @Override
            public String item(RTBCategory value) {
                return value.getName();
            }
        }, notApprovedTag.getRtbCategories());
        notApprovedTagTo.setRtbCategories((List<String>) rtbCategories);

        CreativeCategoryEditTO editTO = categoryServiceBean.getForEdit(CreativeCategoryType.TAG);

        CreativeCategoryTO deletedCategory = editTO.getCategories().remove(0);
        Long testDeleteId = jdbcTemplate.queryForLong("SELECT CREATIVE_CATEGORY_ID FROM CREATIVECATEGORY WHERE NAME = ? AND CCT_ID = ?", deletedCategory.getName(), 2);

        editTO.getCategories().add(notApprovedTagTo);

        getEntityManager().clear();

        categoryServiceBean.update(editTO);
        commitChanges();

        // check update status
        CreativeCategory testApproveTag = getCategory(notApprovedTag.getId());
        assertEquals(testApproveTag.getQaStatus(), 'A');

        // check delete
        if (testDeleteId != null) {
            EntityNotFoundException e2 = null;
            try {
                categoryServiceBean.findById(testDeleteId);
            } catch (EntityNotFoundException e) {
                // everything OK
                e2 = e;
            }
            assertNotNull("EntityNotFoundException expected but entity still exist", e2);

            long notDeleted = jdbcTemplate.queryForLong("SELECT COUNT(CREATIVE_CATEGORY_ID) FROM CREATIVECATEGORY_TEMPLATE WHERE CREATIVE_CATEGORY_ID = " + testDeleteId);
            assertEquals(0, notDeleted);

            notDeleted = jdbcTemplate.queryForLong("SELECT COUNT(CREATIVE_CATEGORY_ID) FROM SITECREATIVECATEGORYEXCLUSION WHERE CREATIVE_CATEGORY_ID = " + testDeleteId);
            assertEquals(0, notDeleted);

            notDeleted = jdbcTemplate.queryForLong("SELECT COUNT(CREATIVE_CATEGORY_ID) FROM CREATIVECATEGORY_CREATIVE WHERE CREATIVE_CATEGORY_ID = " + testDeleteId);
            assertEquals(0, notDeleted);
        }
    }

    @Test
    public void testEquals() throws Exception {
        CreativeCategory category1 = new CreativeCategory();
        CreativeCategory category2 = new CreativeCategory();
        assertTrue(category1.equals(category2));
        assertEquals(category1.hashCode(), category2.hashCode());

        category1.setType(CreativeCategoryType.CONTENT);
        category2.setType(CreativeCategoryType.CONTENT);
        assertEquals(category1, category2);
        assertTrue(category1.equals(category2));
        assertEquals(category1.hashCode(), category2.hashCode());

        category1.setId(100L);
        category2.setId(200L);
        assertFalse(category1.equals(category2));
        category1.setId(null);
        category2.setId(null);
        assertEquals(category1, category2);

        category2.setId(category1.getId());
        category1.setDefaultName("Name");
        category2.setDefaultName("Name");
        assertEquals(category1, category2);
        assertTrue(category1.equals(category2));
        assertEquals(category1.hashCode(), category2.hashCode());
    }

    @Test
    public void testNotEqualsByType() throws Exception {
        CreativeCategory category1 = new CreativeCategory();
        CreativeCategory category2 = new CreativeCategory();
        category1.setType(CreativeCategoryType.TAG);
        category2.setType(CreativeCategoryType.CONTENT);
        assertFalse(category1.equals(category2));
        assertFalse(category1.hashCode() == category2.hashCode());
    }

    @Test
    public void testNotEqualsByName() throws Exception {
        CreativeCategory category1 = new CreativeCategory();
        CreativeCategory category2 = new CreativeCategory();
        category1.setDefaultName("Name2");
        category2.setDefaultName("Name3");
        assertFalse(category1.equals(category2));
        assertFalse(category1.hashCode() == category2.hashCode());
    }

    private boolean containCategory(List<CreativeCategoryTO> categories, CreativeCategoryTO category) {
        for (CreativeCategoryTO cto : categories) {
            if (cto.getName().equals(category.getName())) {
                return true;
            }
        }
        return false;
    }

    private void removeCategory(List<CreativeCategoryTO> categories, CreativeCategoryTO category) {
        Iterator<CreativeCategoryTO> iter = categories.iterator();
        while (iter.hasNext()) {
            CreativeCategoryTO cto = iter.next();
            if (cto.getName().equals(category.getName())) {
                iter.remove();
            }
        }
    }

    @Test
    public void testEditSimpleEdit() {
        CreativeCategoryTO testCategoryTo1 = new CreativeCategoryTO();
        testCategoryTo1.setName(UUID.randomUUID().toString());
        CreativeCategoryTO testCategoryTo2 = new CreativeCategoryTO();
        testCategoryTo2.setName(UUID.randomUUID().toString());

        CreativeCategoryEditTO editTO1 = categoryServiceBean.getForEdit(CreativeCategoryType.CONTENT);
        editTO1.getCategories().add(testCategoryTo1);
        editTO1.getCategories().add(testCategoryTo2);

        categoryServiceBean.update(editTO1);
        commitChanges();

        CreativeCategoryEditTO editTO2 = categoryServiceBean.getForEdit(CreativeCategoryType.CONTENT);
        assertTrue(containCategory(editTO2.getCategories(), testCategoryTo1));
        assertTrue(containCategory(editTO2.getCategories(), testCategoryTo2));

        removeCategory(editTO2.getCategories(), testCategoryTo1);
        categoryServiceBean.update(editTO2);
        entityManager.flush();
        entityManager.clear();

        CreativeCategoryEditTO editTO3 = categoryServiceBean.getForEdit(CreativeCategoryType.CONTENT);
        assertFalse(containCategory(editTO3.getCategories(), testCategoryTo1));
        assertTrue(containCategory(editTO3.getCategories(), testCategoryTo2));

        try {
            categoryServiceBean.update(editTO1);
            fail();
        } catch (VersionCollisionException e) {
        }
    }

    private void assertEquality(final Long id, final String categoryContentName, final CreativeCategoryType type) {
        jdbcTemplate.query(
                "select * from CREATIVECATEGORY cc where cc.CREATIVE_CATEGORY_ID=?",
                new PreparedStatementSetter(){
                    @Override
                    public void setValues(PreparedStatement preparedStatement) throws SQLException {
                        preparedStatement.setLong(1, id);
                    }
                },

                new ResultSetExtractor(){
                    @Override
                    public Object extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                        if (resultSet.next()) {
                            assertEquals(categoryContentName, resultSet.getString("NAME"));
                            assertEquals(type.ordinal(), resultSet.getInt("CCT_ID"));
                        } else {
                            fail("No object with id " + id  + " exists in database");
                        }
                        return null;
                    }
                });
    }

    private CreativeCategory getCategory(Long id) {
        return jdbcTemplate.queryForObject("select * from CREATIVECATEGORY where CREATIVE_CATEGORY_ID=?",
                new ParameterizedRowMapper<CreativeCategory>(){
                    @Override
                    public CreativeCategory mapRow(ResultSet resultSet, int i) throws SQLException {
                        CreativeCategory category = new CreativeCategory(resultSet.getLong("CREATIVE_CATEGORY_ID"), resultSet.getString("NAME"));
                        category.setType(CreativeCategoryType.values()[resultSet.getInt("CCT_ID")]);
                        category.setQaStatus(resultSet.getString("QA_STATUS").charAt(0));
                        return category;
                    }
                }, id);

    }
}
