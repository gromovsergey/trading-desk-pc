package com.foros.changes;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.audit.serialize.serializer.DomHelper;
import com.foros.audit.serialize.serializer.TriggersAuditSerializerHelper;
import com.foros.changes.inspection.ChangeDescriptorRegistry;
import com.foros.changes.inspection.EntityChangeDescriptor;
import com.foros.changes.inspection.PrepareChangesContext;
import com.foros.changes.inspection.changeNode.EntityChangeNode;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.admin.WDFrequencyCapWrapper;
import com.foros.model.channel.placementsBlacklist.BlacklistAction;
import com.foros.model.channel.placementsBlacklist.BlacklistReason;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.model.channel.placementsBlacklist.PlacementsBlacklistWrapper;
import com.foros.model.security.User;
import com.foros.session.admin.country.CountryServiceBean;

import group.Db;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import javax.ejb.EJB;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.dom4j.Document;
import org.dom4j.Node;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Db.class)
public class ChangesServiceBeanTest extends AbstractServiceBeanIntegrationTest {

    @EJB
    private ChangesServiceBean changesServiceBean;

    @EJB
    private CountryServiceBean countryService;

    @Test
    public void testRegistry() {
        ChangeDescriptorRegistry registry = changesServiceBean.getRegistry();
        assertNotNull(registry);

        EntityChangeDescriptor aaDescriptor = registry.getDescriptor(new AdvertiserAccount());
        assertNotNull(aaDescriptor);
        assertTrue(aaDescriptor.fieldsCount() > 0);

        EntityChangeDescriptor wdwDescriptor = registry.getDescriptor(new WDFrequencyCapWrapper());
        assertNotNull(wdwDescriptor);
        assertTrue(wdwDescriptor.fieldsCount() > 0);
    }

    @Test
    public void testSerialization() {
        PlacementBlacklist placementBlacklist = createPlacementBlacklist();

        PlacementsBlacklistWrapper rootChange = new PlacementsBlacklistWrapper(countryService.find("RU").getCountryId());
        rootChange.setPlacements(Collections.singletonList(placementBlacklist));

        EntityChangeNode entityChangeNode = prepareChangeContext(rootChange);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
            entityChangeNode.serialize(xmlWriter);
            xmlWriter.close();

            assertNotNull(outputStream.toString());

            Document doc = DomHelper.stringToDocument(outputStream.toString());
            Node triggersNode = doc.selectSingleNode("/entity[1]/property[1]/collection[1]");
            String parsed = TriggersAuditSerializerHelper.fetchAddedTriggers(triggersNode);

            assertEquals(placementBlacklist.toString(), parsed);

        } catch (XMLStreamException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private EntityChangeNode prepareChangeContext(Object obj) {
        ChangeDescriptorRegistry registry = changesServiceBean.getRegistry();
        EntityChangeDescriptor descriptor = registry.getDescriptor(obj);
        EntityChangeNode entityChange = descriptor.newEntityChange(obj);

        PrepareChangesContext changesContext = new PrepareChangesContext(Collections.<Object, EntityChangeNode>emptyMap(), registry);
        entityChange.prepare(changesContext);

        return entityChange;
    }

    private PlacementBlacklist createPlacementBlacklist() {
        PlacementBlacklist result = new PlacementBlacklist();

        result.setSizeName("");
        result.setAction(BlacklistAction.ADD);
        result.setDateAdded(new Date());
        result.setReason(EnumSet.of(BlacklistReason.HIGH_FRAUD));
        result.setUrl("wababy.org/1&2");
        result.setUser(new User(1L));

        return result;
    }
}
