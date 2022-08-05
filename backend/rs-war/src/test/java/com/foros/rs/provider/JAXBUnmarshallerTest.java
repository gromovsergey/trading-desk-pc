package com.foros.rs.provider;

import com.foros.AbstractUnitTest;
import com.foros.model.EntityBase;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.ServiceLocatorMock;
import com.foros.session.bulk.Operations;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters;
import com.foros.validation.code.InputErrors;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.constraint.violation.parsing.ParseErrorsContainer;
import com.foros.validation.constraint.violation.parsing.ParseErrorsSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import group.Unit;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class JAXBUnmarshallerTest extends AbstractUnitTest {
    private ConstraintViolation[] constraintViolations;

    @Rule
    public ServiceLocatorMock serviceLocatorMock = ServiceLocatorMock.getInstance();

    @Test
    public void empty() throws Exception {
        read(Operations.class, "testXml/empty.xml");
        assertNotNull(constraintViolations);
        assertEquals(1, constraintViolations.length);
        assertEquals(InputErrors.XML_ILL_FORMED, constraintViolations[0].getError());
    }

    @Test
    public void illFormed() throws Exception {
        read(Operations.class, "testXml/illFormed.xml");
        assertNotNull(constraintViolations);
        assertEquals(1, constraintViolations.length);
        assertEquals(InputErrors.XML_ILL_FORMED, constraintViolations[0].getError());
    }

    @Test
    public void invalidDate() throws Exception {
        read(OlapAdvertiserReportParameters.class, "testXml/invalidDate.xml");
        assertNotNull(constraintViolations);
        assertEquals(1, constraintViolations.length);
        assertEquals("advertiserReportParameters.dateRange.begin", constraintViolations[0].getPropertyPath().toString());
        assertEquals(InputErrors.XML_DATE_INVALID, constraintViolations[0].getError());
    }

    @Test
    public void invalidBoolean() throws Exception {
        read(CampaignCreativeGroup.class, "testXml/invalidBoolean.xml");
        assertNotNull(constraintViolations);
        assertEquals(1, constraintViolations.length);
        assertEquals("campaignCreativeGroup.deliveryScheduleFlag", constraintViolations[0].getPropertyPath().toString());
        assertEquals(InputErrors.XML_BOOLEAN_ERROR, constraintViolations[0].getError());
    }

    @Test
    public void unexpectedCollection() throws Exception {
        read(OlapAdvertiserReportParameters.class, "testXml/invalidCollection.xml");
        assertNotNull(constraintViolations);
        assertEquals(1, constraintViolations.length);
        assertEquals("advertiserReportParameters.dateRange[1]", constraintViolations[0].getPropertyPath().toString());
        assertEquals(InputErrors.XML_UNEXPECTED_COLLECTION, constraintViolations[0].getError());
    }

    @Test
    public void wrongRootTag() throws Exception {
        read(OlapAdvertiserReportParameters.class, "testXml/wrongRootTag.xml");
        assertNotNull(constraintViolations);
        assertEquals(1, constraintViolations.length);
        assertEquals("AdvertiserReportParameters", constraintViolations[0].getPropertyPath().toString());
        assertEquals(InputErrors.XML_WRONG_TAG, constraintViolations[0].getError());
    }

    @Test
    public void wrongTag() throws Exception {
        read(OlapAdvertiserReportParameters.class, "testXml/wrongTag.xml");
        assertNotNull(constraintViolations);
        assertEquals(1, constraintViolations.length);
        assertEquals("advertiserReportParameters.dateRange.date", constraintViolations[0].getPropertyPath().toString());
        assertEquals(InputErrors.XML_WRONG_TAG, constraintViolations[0].getError());
    }

    @Test
    public void complex() throws Exception {
        read(OlapAdvertiserReportParameters.class, "testXml/complex.xml");
        assertNotNull(constraintViolations);
        assertEquals(4, constraintViolations.length);
    }

    private <T> T read(Class<T> type, String file) throws Exception {
        constraintViolations = null;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
            T object = new JAXBUnmarshaller<T>().readFrom(type, null, new Annotation[0], MediaType.TEXT_XML_TYPE, null, is);
            if (object instanceof EntityBase) {
                ParseErrorsContainer container = ((EntityBase) object).getProperty(ParseErrorsSupport.PARSE_ERRORS);
                if (container != null) {
                    ParseErrorsSupport.throwIfAnyErrorsPresent(container);
                }
            }
            return object;
        } catch (ConstraintViolationException e) {
            Set<ConstraintViolation> set = e.getConstraintViolations();
            constraintViolations = set.toArray(new ConstraintViolation[set.size()]);
            return null;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
