package com.foros.util;

import com.foros.model.account.Account;
import com.foros.model.site.Site;
import com.foros.model.EntityBase;
import com.foros.model.account.PublisherAccount;
import com.foros.util.bean.Filter;
import group.Unit;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class DuplicatesFilterTest {
    @Test
    @Category(Unit.class)
    public void filters() throws Exception {
        List<Account> accountCollection = new ArrayList<Account>();
        accountCollection.add(new PublisherAccount(0L, "A"));
        accountCollection.add(new PublisherAccount(0L, "B"));
        accountCollection.add(null);
        accountCollection.add(new PublisherAccount(0L, "A"));

        assertEquals(accountCollection.size(), 4);
        CollectionUtils.filter(accountCollection, new DuplicatesFilter<Account>("name", true));
        assertEquals(3, accountCollection.size());
    }

    @Test
    @Category(Unit.class)
    public void filtersRemoveInaccessible() throws Exception {
        List<Account> accountCollection = new ArrayList<Account>();
        accountCollection.add(new PublisherAccount(0L, "A"));
        accountCollection.add(new PublisherAccount(0L, "B"));
        accountCollection.add(null);
        accountCollection.add(new PublisherAccount(0L, "A"));

        assertEquals(accountCollection.size(), 4);
        CollectionUtils.filter(accountCollection, new DuplicatesFilter<Account>("name", false));
        assertEquals(2, accountCollection.size());
    }

    @Test
    @Category(Unit.class)
    public void filtersNull() throws Exception {
        List<Account> accountCollection = new ArrayList<Account>();
        accountCollection.add(0, null);
        accountCollection.add(null);
        assertEquals(accountCollection.size(), 2);
        CollectionUtils.filter(accountCollection, new DuplicatesFilter<Account>("name", true));
        assertEquals(2, accountCollection.size());
    }

    @Test
    @Category(Unit.class)
    public void filter() throws Exception {
        Collection<String> someCollection = new LinkedList<String>();
        someCollection.add("A");
        someCollection.add("B");
        someCollection.add("");
        someCollection.add(null);
        someCollection.add("C");

        CollectionUtils.filter(someCollection, new Filter<String>() {
            @Override
            public boolean accept(String element) {
                return StringUtil.isPropertyNotEmpty(element);
            }
        });

        assertEquals(3, someCollection.size());
    }

    @Test
    @Category(Unit.class)
    public void filterWithOgnl() throws Exception {
        List<MockEntity> mockEntities = new ArrayList<MockEntity>();
        mockEntities.add(createMockEntity("A", "SiteA"));
        mockEntities.add(createMockEntity("B", "SiteA"));
        mockEntities.add(createMockEntity("C", "SiteB"));

        assertEquals(3, mockEntities.size());
        CollectionUtils.filter(mockEntities, new DuplicatesFilter<MockEntity>("entities[0].name", true));
        assertEquals(2, mockEntities.size());
    }

    @Test
    @Category(Unit.class)
    public void filterWithOgnlRemoveInaccessible() throws Exception {
        List<MockEntity> mockEntities = new ArrayList<MockEntity>();
        mockEntities.add(createMockEntity("A", "SiteA"));
        mockEntities.add(createMockEntity("B", "SiteA"));
        mockEntities.add(createMockEntity("C", "SiteB"));
        mockEntities.add(null);

        assertEquals(4, mockEntities.size());
        CollectionUtils.filter(mockEntities, new DuplicatesFilter<MockEntity>("entities[0].name", false));
        assertEquals(2, mockEntities.size());
    }

    private MockEntity createMockEntity(String name, String siteName) {
        MockEntity mockEntity = new MockEntity(1L, name);
        mockEntity.setEntities(new ArrayList<EntityBase>());
        Site site = new Site(0L, siteName);
        mockEntity.getEntities().add(site);
        return mockEntity;
    }

    public class MockEntity {
        private Long id;
        private String name;
        private double value;
        private List<EntityBase> entities;

        public MockEntity(Long id) {
            this(id, null, 0.0d);
        }

        public MockEntity(Long id, String name) {
            this(id, name, 0.0d);
        }

        public MockEntity(Long id, String name, double value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getValue() {
            return value;
        }

        public List<EntityBase> getEntities() {
            return entities;
        }

        public void setEntities(List<EntityBase> entities) {
            this.entities = entities;
        }

        @Override
        public int hashCode() {
            return 17 + (int)(id ^ (id >>> 32));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (getClass() != obj.getClass()) {
                return false;
            }

            final MockEntity other = (MockEntity)obj;
            if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
                return false;
            }

            return true;
        }
    }
}
