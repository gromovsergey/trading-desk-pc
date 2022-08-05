package com.foros.session.admin.behavioralParameters;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.channel.BehavioralParameters;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.trigger.TriggerType;
import com.foros.test.factory.BehavioralParamsTestFactory;
import com.foros.test.factory.DiscoverChannelTestFactory;

import group.Db;

import java.util.Iterator;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class BehavioralParamsListServiceBeanIntegrationTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private BehavioralParamsListService bparamsListSvc;

    @Autowired
    private BehavioralParamsTestFactory bparamsTF;

    @Autowired
    private DiscoverChannelTestFactory discoverChannelTF;

    @Test
    public void testCreate() throws Exception {
        BehavioralParametersList bparamsList = bparamsTF.createPersistent();
        getEntityManager().flush();
        assertEquals("Behvioral params are not saved correctly",
                bparamsList.getBehavioralParameters().size(),
                jdbcTemplate.queryForInt(
                        "select count(*) from behavioralparameters where behav_params_list_id = ?",
                        bparamsList.getId()));
    }

    @Test
    public void testUpdate() throws Exception {
        BehavioralParametersList bparamsList = bparamsTF.createPersistent();
        getEntityManager().flush();
        getEntityManager().clear();

        BehavioralParameters param = bparamsTF.createBParam(TriggerType.URL);
        param.setParamsList(bparamsList);
        bparamsList.getBehavioralParameters().add(param);

        getEntityManager().clear();
        bparamsListSvc.update(bparamsList);
        getEntityManager().flush();

        assertEquals("Behavioral params are not updated correctly",
                bparamsList.getBehavioralParameters().size(),
                jdbcTemplate.queryForInt(
                        "select count(*) from behavioralparameters where behav_params_list_id = ?",
                        bparamsList.getId()));

        BehavioralParametersList persisted = bparamsListSvc.find(bparamsList.getId());
        persisted.getBehavioralParameters().remove(persisted.getBehavioralParameters().iterator().next());

        getEntityManager().clear();
        bparamsListSvc.update(persisted);
        getEntityManager().flush();
        assertEquals("Behvioral params are not updated correctly",
                persisted.getBehavioralParameters().size(),
                jdbcTemplate.queryForInt(
                        "select count(*) from behavioralparameters where behav_params_list_id = ?",
                        persisted.getId()));
    }

    @Test
    public void testDelete() throws Exception {
        BehavioralParametersList bparamsList = bparamsTF.createPersistent();
        bparamsListSvc.delete(bparamsList.getId());

        try {
            bparamsListSvc.find(bparamsList.getId());
            fail("BehavioralParametersList is not deleted correctly");
        } catch (EntityNotFoundException ex) {
        }
    }

    private void removeParamById(Set<BehavioralParameters> params, Long id) {
        for (Iterator<BehavioralParameters> it = params.iterator(); it.hasNext();) {
            if (id.equals(it.next().getId())) {
                it.remove();
                return;
            }
        }

        fail("Couldn't find behavioral param: " + id);
    }

    private BehavioralParameters findByType(Set<BehavioralParameters> params, Character type) {
        for (BehavioralParameters bp : params) {
            if (type.equals(bp.getTriggerType())) {
                return bp;
            }
        }
        return null;
    }
}
