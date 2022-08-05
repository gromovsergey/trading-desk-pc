package com.foros.session.creative;

import com.foros.AbstractRestrictionsBeanTest;
import com.foros.model.Status;
import com.foros.model.creative.CreativeSize;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionType;
import com.foros.session.template.OptionRestrictions;

import com.foros.test.factory.CreativeSizeTestFactory;
import com.foros.test.factory.OptionGroupTestFactory;
import com.foros.test.factory.OptionTestFactory;

import group.Db;
import group.Restriction;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ejb.EJB;

@Category({ Db.class, Restriction.class })
public class OptionRestrictionsBeanTest extends AbstractRestrictionsBeanTest {

    @Autowired
    private OptionRestrictions optionRestrictions;

    @Autowired
    private OptionTestFactory optionTF;

    @EJB
    private OptionGroupTestFactory optionGroupTF;

    @Autowired
    private CreativeSizeTestFactory creativeSizeTF;

    public void setUpDefaultExpectations() {
        super.setUpDefaultExpectations();
        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);
    }

    @Test
    public void testView() throws Exception {
        CreativeSize size = creativeSizeTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(size);
        final Option option = optionTF.create(optionGroup, OptionType.STRING);
        Callable callCanView = new Callable("creativeSize", "view") {
            @Override
            public boolean call() {
                return optionRestrictions.canView(option);
            }
        };

        expectResult(internalAllAccess, true);
        expectResult(advertiserAllAccess1, false);

        doCheck(callCanView);
    }

    @Test
    public void testCanUpdate() throws Exception {
        CreativeSize size = creativeSizeTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(size);
        final Option option = optionTF.create(optionGroup, OptionType.STRING);
        Callable callCanUpdate = new Callable("creativeSize", "edit") {
            @Override
            public boolean call() {
                return optionRestrictions.canUpdate(option);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanUpdate);

        option.getOptionGroup().getCreativeSize().setStatus(Status.DELETED);

        expectResult(internalAllAccess, false);
        expectResult(advertiserAllAccess1, false);

        doCheck(callCanUpdate);
    }

    @Test
    public void testCanCreateBySize() throws Exception {
        CreativeSize size = creativeSizeTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(size);
        final Option option = optionTF.create(optionGroup, OptionType.STRING);
        Callable callCanCreate = new Callable("creativeSize", "edit") {
            @Override
            public boolean call() {
                return optionRestrictions.canCreate(null, option.getOptionGroup().getCreativeSize().getId(), null);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanCreate);
    }

    @Test
    public void testCanCreate() throws Exception {
        CreativeSize size = creativeSizeTF.createPersistent();
        OptionGroup optionGroup = optionGroupTF.createPersistent(size);
        final Option option = optionTF.create(optionGroup, OptionType.STRING);
        Callable callCanCreate = new Callable("creativeSize", "edit") {
            @Override
            public boolean call() {
                return optionRestrictions.canCreate(option);
            }
        };

        expectResult(internalAllAccess, true);

        doCheck(callCanCreate);
    }
}
