package com.foros.session.creative;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.Status;
import com.foros.model.template.Template;
import com.foros.session.template.TemplateRestrictions;
import com.foros.test.factory.CreativeTemplateTestFactory;

import group.Db;
import group.Restriction;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({ Db.class, Restriction.class })
public class TemplateRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private TemplateRestrictions templateRestrictions;

    @Autowired
    private CreativeTemplateTestFactory creativeTemplateTF;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        Callable callCanView = new Callable("template", "view") {
            @Override
            public boolean call() {
                return templateRestrictions.canView();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdateActive() throws Exception {
        final Template template = creativeTemplateTF.createPersistent();
        Callable callCanUpdate = new Callable("template", "edit") {
            @Override
            public boolean call() {
            	template.setStatus(Status.ACTIVE);
                return templateRestrictions.canUpdate(template);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanUpdateDeleted() throws Exception {
        final Template template = creativeTemplateTF.createPersistent();
        Callable callCanUpdate = new Callable("template", "edit") {
            @Override
            public boolean call() {
            	template.setStatus(Status.DELETED);
                return templateRestrictions.canUpdate(template);
            }
        };

        expectResult(internalAllAccess, false);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanCreate() throws Exception {
        Callable callCanCreate = new Callable("template", "create") {
            @Override
            public boolean call() {
                return templateRestrictions.canCreate();
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanCreate);
    }

    @Test
    public void testCanDeleteActive() throws Exception {
        final Template template = creativeTemplateTF.createPersistent();
        Callable callCanUpdate = new Callable("template", "edit") {
            @Override
            public boolean call() {
            	template.setStatus(Status.ACTIVE);
                return templateRestrictions.canDelete(template);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanDeleteDeleted() throws Exception {
        final Template template = creativeTemplateTF.createPersistent();
        Callable callCanUpdate = new Callable("template", "edit") {
            @Override
            public boolean call() {
            	template.setStatus(Status.DELETED);
                return templateRestrictions.canDelete(template);
            }
        };

        expectResult(internalAllAccess, false);

        doCheck(callCanUpdate);
    }
}
