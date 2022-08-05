package com.foros.action.admin.creativeCategories;

import com.foros.model.creative.RTBConnector;
import com.foros.session.creative.CreativeCategoryService;

import static junit.framework.Assert.assertEquals;
import java.util.Arrays;
import java.util.List;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class CreativeCategoryFieldCsvHelperTest extends EasyMockSupport {

    @Rule
    public EasyMockRule mockRule = new EasyMockRule(this);

    @Mock
    private CreativeCategoryService creativeCategoryService;

    @TestSubject
    private CreativeCategoryFieldCsvHelper helper = new CreativeCategoryFieldCsvHelper();

    private List<RTBConnector> rtbConnectors;


    @Before
    public void initMock() {
        RTBConnector c1 = new RTBConnector();
        c1.setId(1L);
        c1.setName("Name1");

        RTBConnector c2 = new RTBConnector();
        c2.setId(2L);
        c2.setName("Name2");
        rtbConnectors = Arrays.asList(c1, c2);

        EasyMock.expect(creativeCategoryService.getRTBConnectors()).andReturn(rtbConnectors);

        replayAll();

        helper.init();
    }

    @Test
    public void test() {
        int connectorsSize = rtbConnectors.size();
        assertEquals(connectorsSize, helper.getRtbKeys().size());

        // names should match
        for (int i = 0; i < connectorsSize; i++) {
            RTBConnector rtbConnector = rtbConnectors.get(i);
            CreativeCategoryFieldCsv rtbKey = helper.getRtbKeys().get(i);
            assertEquals(rtbConnector.getName(), rtbKey.getName());
        }

        // plus 3 columns: id, name and localization
        assertEquals(connectorsSize + 3, helper.getAllColumns().size());

        // id should be equal to position
        for (int i = 0; i < helper.getAllColumns().size(); i++) {
            CreativeCategoryFieldCsv field = helper.getAllColumns().get(i);
            assertEquals(i, field.getId());
        }
    }

}